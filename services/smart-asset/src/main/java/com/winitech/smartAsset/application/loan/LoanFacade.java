package com.winitech.smartAsset.application.loan;

import com.winitech.common.exception.InvalidParamException;
import com.winitech.smartAsset.domain.loan.Loan;
import com.winitech.smartAsset.domain.loan.LoanAvailabilityInfo;
import com.winitech.smartAsset.domain.loan.LoanInfo;
import com.winitech.smartAsset.domain.loan.LoanService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;
import java.util.function.Supplier;

@Slf4j
@Service
@RequiredArgsConstructor
public class LoanFacade {

    private final LoanService loanService;

    public Page<LoanInfo> getLoanList(Loan.Status statusFilter, Integer page, Integer size) {
        return loanService.loadLoanList(statusFilter, page, size);
    }

    public List<LoanInfo> getPendingApprovalList() {
        return loanService.loadPendingApprovalList();
    }

    public List<LoanAvailabilityInfo> getAvailableAssets() {
        return loanService.loadAvailableAssets();
    }

    public List<LoanInfo> getMyLoans() {
        return loanService.loadMyLoans();
    }

    public UUID borrowBySelf(UUID tangibleAssetId) {
        return withConflictTranslation(() -> loanService.borrowBySelf(tangibleAssetId));
    }

    public UUID borrowByAdmin(UUID tangibleAssetId, UUID memberId) {
        return withConflictTranslation(() -> loanService.borrowByAdmin(tangibleAssetId, memberId));
    }

    public void approveLoan(UUID loanId) {
        loanService.approveLoan(loanId);
    }

    public void rejectLoan(UUID loanId, String reason) {
        loanService.rejectLoan(loanId, reason);
    }

    public void returnLoanBySelf(UUID loanId, boolean abnormal) {
        loanService.returnLoanBySelf(loanId, abnormal);
    }

    public void extendLoanBySelf(UUID loanId) {
        loanService.extendLoanBySelf(loanId);
    }

    /**
     * L1 - 대여 시작은 TangibleAsset.version 낙관적 잠금과 경쟁한다. 그 충돌
     * (ObjectOptimisticLockingFailureException)과 FK 위반(DataIntegrityViolationException)은
     * Spring이 트랜잭션 커밋 시점에 던지므로(도메인 서비스 메서드 본문이 아니라 그 메서드를 호출하는
     * 지점, 즉 여기서 잡힌다) - TangibleAssetFacade.withConflictTranslation과 동일한 이유로 이
     * 파사드 계층에서 사용자가 이해할 수 있는 메시지로 변환한다.
     */
    private <T> T withConflictTranslation(Supplier<T> action) {
        try {
            return action.get();
        } catch (ObjectOptimisticLockingFailureException | DataIntegrityViolationException e) {
            throw new InvalidParamException("이미 다른 분이 대여했습니다. 목록을 새로고침해주세요.");
        }
    }
}
