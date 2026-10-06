package com.winitech.smartAsset.domain.loan;

import com.winitech.common.bean.LoginUserContext;
import com.winitech.common.exception.InvalidParamException;
import com.winitech.smartAsset.domain.assetAssignment.AssetAssignment;
import com.winitech.smartAsset.domain.assetAssignment.AssetAssignmentReader;
import com.winitech.smartAsset.domain.assetAssignment.AssetAssignmentStore;
import com.winitech.smartAsset.domain.processConfig.ProcessConfig;
import com.winitech.smartAsset.domain.processConfig.ProcessConfigReader;
import com.winitech.smartAsset.domain.tangibleAsset.TangibleAsset;
import com.winitech.smartAsset.domain.tangibleAsset.TangibleAssetReader;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.egovframe.rte.fdl.cmmn.EgovAbstractServiceImpl;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * S-410~412,420,421 대여 관리. L1(동시 대여 경쟁)은 이 서비스가 아니라 TangibleAsset.borrow()가
 * 딸려있는 @Version 낙관적 잠금으로 막는다. 그 충돌 예외(ObjectOptimisticLockingFailureException)는
 * 트랜잭션 커밋 시점에 던져지므로 이 메서드 본문이 아니라 이 메서드를 호출하는 LoanFacade에서
 * 잡아 사용자 메시지로 바꾼다(TangibleAssetFacade.withConflictTranslation과 동일한 이유).
 */
@Slf4j
@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class LoanServiceImpl extends EgovAbstractServiceImpl implements LoanService {

    private static final int DEFAULT_PAGE_SIZE = 20;
    private static final int MAX_PAGE_SIZE = 200;
    private static final Sort DEFAULT_SORT = Sort.by(Sort.Direction.DESC, "borrowedAt");

    private final LoanReader loanReader;
    private final LoanStore loanStore;
    private final TangibleAssetReader tangibleAssetReader;
    private final ProcessConfigReader processConfigReader;
    private final AssetAssignmentReader assetAssignmentReader;
    private final AssetAssignmentStore assetAssignmentStore;
    private final LoginUserContext loginUserContext;

    @Override
    public Page<LoanInfo> loadLoanList(Loan.Status statusFilter, Integer page, Integer size) {
        return loanReader.findAll(statusFilter, toPageable(page, size)).map(LoanInfo::new);
    }

    @Override
    public List<LoanInfo> loadPendingApprovalList() {
        return loanReader.findAllPendingApproval().stream().map(LoanInfo::new).collect(Collectors.toList());
    }

    @Override
    public List<LoanInfo> loadMyLoans() {
        return loanReader.findAllByMemberId(loginUserContext.getUserId()).stream().map(LoanInfo::new).collect(Collectors.toList());
    }

    @Override
    public List<LoanAvailabilityInfo> loadAvailableAssets() {
        List<TangibleAsset> assets = tangibleAssetReader.findAllByAssignTypeIn(
                Arrays.asList(TangibleAsset.AssignType.LOANABLE, TangibleAsset.AssignType.ON_LOAN));

        Map<UUID, Loan> activeLoanByAssetId = loanReader.findAllActive().stream()
                .collect(Collectors.toMap(l -> l.getTangibleAsset().getId(), Function.identity()));

        return assets.stream()
                .map(asset -> new LoanAvailabilityInfo(asset, activeLoanByAssetId.get(asset.getId())))
                .collect(Collectors.toList());
    }

    @Transactional
    @Override
    public UUID borrowBySelf(UUID tangibleAssetId) {
        UUID myId = loginUserContext.getUserId();
        return borrow(tangibleAssetId, myId, myId);
    }

    @Transactional
    @Override
    public UUID borrowByAdmin(UUID tangibleAssetId, UUID memberId) {
        return borrow(tangibleAssetId, memberId, loginUserContext.getUserId());
    }

    private UUID borrow(UUID tangibleAssetId, UUID memberId, UUID createdBy) {
        ProcessConfig config = processConfigReader.getSingleton();
        if (!Boolean.TRUE.equals(config.getLoanEnabled())) {
            throw new InvalidParamException("대여 프로세스가 켜져 있지 않습니다.");
        }

        if (Boolean.TRUE.equals(config.getBlockOnOverdue())) {
            List<Loan> overdue = loanReader.findOverdueActiveByMemberId(memberId, LocalDate.now());
            if (!overdue.isEmpty()) {
                String assetNames = overdue.stream().map(l -> l.getTangibleAsset().getAssetName()).collect(Collectors.joining(", "));
                throw new InvalidParamException("반납하지 않은 연체 자산이 있어 신규 대여할 수 없습니다: " + assetNames);
            }
        }

        Integer concurrentLimit = config.getConcurrentLimit();
        if (concurrentLimit != null && loanReader.countOpenByMemberId(memberId) >= concurrentLimit) {
            throw new InvalidParamException("1인 동시 대여 한도(" + concurrentLimit + "건)를 초과했습니다.");
        }

        TangibleAsset tangibleAsset = tangibleAssetReader.findById(tangibleAssetId);
        tangibleAsset.borrow(memberId);
        openAssignment(tangibleAsset, memberId, createdBy);

        int defaultLoanDays = config.getDefaultLoanDays() != null ? config.getDefaultLoanDays() : 7;
        Loan loan = Loan.builder()
                .tangibleAsset(tangibleAsset)
                .memberId(memberId)
                .dueDate(LocalDate.now().plusDays(defaultLoanDays))
                .requireApproval(Boolean.TRUE.equals(config.getRequireApproval()))
                .createdBy(createdBy)
                .build();
        // 이 save() 자체는 영속화만 예약할 뿐, 실제 UPDATE(버전 검사 포함)는 이 @Transactional
        // 메서드가 반환되며 커밋될 때 실행된다 - 그래서 동시성 충돌 예외는 여기서 못 잡고, 이 메서드를
        // 호출하는 LoanFacade에서 잡는다(TangibleAssetFacade.withConflictTranslation과 동일한 이유).
        return loanStore.store(loan).getId();
    }

    @Transactional
    @Override
    public void approveLoan(UUID loanId) {
        Loan loan = loanReader.findById(loanId);
        loan.approve(loginUserContext.getUserId());
    }

    @Transactional
    @Override
    public void rejectLoan(UUID loanId, String reason) {
        Loan loan = loanReader.findById(loanId);
        loan.reject(loginUserContext.getUserId(), reason);
        loan.getTangibleAsset().returnFromLoan(false);
        releaseCurrentAssignment(loan.getTangibleAsset());
    }

    @Transactional
    @Override
    public void returnLoanBySelf(UUID loanId, boolean abnormal) {
        Loan loan = findOwnLoan(loanId);
        loan.returnLoan(abnormal);
        loan.getTangibleAsset().returnFromLoan(abnormal);
        releaseCurrentAssignment(loan.getTangibleAsset());
    }

    /**
     * S-218(유형자산관리 배정 변경)이 assignType이 PERSONAL/ON_LOAN으로 바뀔 때마다 asset_assignment
     * 이력을 열고 닫는 것과 동일하게, 대여도 같은 이력에 남아야 한다(그러지 않으면 자산 상세의
     * "배정 이력" 탭에 대여 기록이 전혀 보이지 않는다) - TangibleAssetServiceImpl.applyUpdate/
     * openAssignment와 동일한 패턴.
     */
    private void openAssignment(TangibleAsset tangibleAsset, UUID memberId, UUID assignedBy) {
        releaseCurrentAssignment(tangibleAsset);
        assetAssignmentStore.store(AssetAssignment.builder()
                .tangibleAsset(tangibleAsset)
                .memberId(memberId)
                .assignType(TangibleAsset.AssignType.ON_LOAN.name())
                .assignedBy(assignedBy)
                .build());
    }

    private void releaseCurrentAssignment(TangibleAsset tangibleAsset) {
        assetAssignmentReader.findCurrentByTangibleAssetId(tangibleAsset.getId()).ifPresent(assetAssignmentStore::release);
    }

    @Transactional
    @Override
    public void extendLoanBySelf(UUID loanId) {
        Loan loan = findOwnLoan(loanId);
        ProcessConfig config = processConfigReader.getSingleton();
        int maxExtendCount = config.getMaxExtendCount() != null ? config.getMaxExtendCount() : 1;
        int extensionDays = config.getDefaultLoanDays() != null ? config.getDefaultLoanDays() : 7;
        loan.extend(maxExtendCount, extensionDays);
    }

    private Loan findOwnLoan(UUID loanId) {
        Loan loan = loanReader.findById(loanId);
        UUID myId = loginUserContext.getUserId();
        if (!loan.getMemberId().equals(myId)) {
            throw new InvalidParamException("본인이 대여한 자산만 처리할 수 있습니다.");
        }
        return loan;
    }

    private Pageable toPageable(Integer page, Integer size) {
        int safePage = (page == null || page < 0) ? 0 : page;
        int safeSize = (size == null) ? DEFAULT_PAGE_SIZE : Math.max(1, Math.min(size, MAX_PAGE_SIZE));
        return PageRequest.of(safePage, safeSize, DEFAULT_SORT);
    }
}
