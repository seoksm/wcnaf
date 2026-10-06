package com.winitech.smartAsset.domain.acknowledgement;

import com.winitech.common.bean.LoginUserContext;
import com.winitech.common.exception.InvalidParamException;
import com.winitech.smartAsset.domain.ackTemplate.AckTemplate;
import com.winitech.smartAsset.domain.ackTemplate.AckTemplateReader;
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

import java.math.BigDecimal;
import java.text.NumberFormat;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * S-430~432,440 수령·반납 승인. K1(문서 스냅샷)은 요청 시점에 렌더링해 고정하고, K2(승인 증빙)는
 * 승인 단계마다 {@link AcknowledgementApproval} 행을 추가해 남긴다.
 */
@Slf4j
@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class AcknowledgementServiceImpl extends EgovAbstractServiceImpl implements AcknowledgementService {

    private static final int DEFAULT_PAGE_SIZE = 20;
    private static final int MAX_PAGE_SIZE = 200;
    private static final Sort DEFAULT_SORT = Sort.by(Sort.Direction.DESC, "requestedAt");

    private final AcknowledgementReader acknowledgementReader;
    private final AcknowledgementStore acknowledgementStore;
    private final AcknowledgementApprovalReader acknowledgementApprovalReader;
    private final AcknowledgementApprovalStore acknowledgementApprovalStore;
    private final TangibleAssetReader tangibleAssetReader;
    private final AckTemplateReader ackTemplateReader;
    private final ProcessConfigReader processConfigReader;
    private final AssetAssignmentReader assetAssignmentReader;
    private final AssetAssignmentStore assetAssignmentStore;
    private final LoginUserContext loginUserContext;

    @Override
    public Page<AcknowledgementInfo> loadList(Acknowledgement.Status statusFilter, Integer page, Integer size) {
        return acknowledgementReader.findAll(statusFilter, toPageable(page, size)).map(AcknowledgementInfo::new);
    }

    @Override
    public AcknowledgementDetailInfo loadDetail(UUID acknowledgementId) {
        Acknowledgement ack = acknowledgementReader.findById(acknowledgementId);
        return new AcknowledgementDetailInfo(ack, acknowledgementApprovalReader.findAllByAcknowledgementId(acknowledgementId));
    }

    @Override
    public List<AcknowledgementInfo> loadMyPending() {
        return acknowledgementReader.findAllPendingEmployeeByMemberId(loginUserContext.getUserId()).stream()
                .map(AcknowledgementInfo::new).collect(Collectors.toList());
    }

    @Override
    public AcknowledgementDetailInfo loadMyDetail(UUID acknowledgementId) {
        Acknowledgement ack = findOwnAcknowledgement(acknowledgementId);
        return new AcknowledgementDetailInfo(ack, acknowledgementApprovalReader.findAllByAcknowledgementId(acknowledgementId));
    }

    @Transactional
    @Override
    public UUID requestAcknowledgement(UUID tangibleAssetId, UUID memberId, String memberName,
                                        Acknowledgement.Type type, String managerName) {
        TangibleAsset asset = tangibleAssetReader.findById(tangibleAssetId);
        AckTemplate template = ackTemplateReader.findByType(AckTemplate.Type.valueOf(type.name()));

        Map<String, String> vars = new HashMap<>();
        vars.put("자산명", asset.getAssetName());
        vars.put("자산코드", asset.getAssetCode());
        vars.put("취득가액", formatAmount(asset.getAcquisitionAmount()));
        vars.put("지급일", LocalDate.now().toString());
        vars.put("담당자", managerName != null ? managerName : "-");
        vars.put("대상자", memberName != null ? memberName : memberId.toString());
        String body = render(template.getBodyTpl(), vars);

        ProcessConfig config = processConfigReader.getSingleton();
        Integer approvalDueDays = config.getApprovalDueDays();
        LocalDate dueDate = approvalDueDays != null ? LocalDate.now().plusDays(approvalDueDays) : null;

        Acknowledgement acknowledgement = Acknowledgement.builder()
                .tangibleAsset(asset)
                .memberId(memberId)
                .type(type)
                .bodySnapshot(body)
                .dueDate(dueDate)
                .requestedBy(loginUserContext.getUserId())
                .build();
        return acknowledgementStore.store(acknowledgement).getId();
    }

    /**
     * S-440 - 임직원 승인. RECEIPT는 항상 이 단계에서 끝나고(K5 미해당), 담당자 승인 옵션이
     * 꺼진 RETURN도 여기서 바로 끝난다 - 두 경우 모두 "승인이 곧 자산 상태 확정 트리거"가 되므로
     * applyCompletionSideEffect에서 함께 처리한다. 담당자 승인이 필요한 RETURN은 PENDING_MANAGER로
     * 넘어갈 뿐 자산은 그대로 두고, approveByManager에서 처리한다.
     */
    @Transactional
    @Override
    public void approveByEmployeeSelf(UUID acknowledgementId, String clientIp) {
        Acknowledgement ack = findOwnAcknowledgement(acknowledgementId);
        ProcessConfig config = processConfigReader.getSingleton();
        ack.approveByEmployee(Boolean.TRUE.equals(config.getRequireManagerApproval()));

        if (ack.getStatus() == Acknowledgement.Status.COMPLETED) {
            applyCompletionSideEffect(ack, config);
        }

        acknowledgementApprovalStore.store(AcknowledgementApproval.builder()
                .acknowledgement(ack)
                .approvalStep(AcknowledgementApproval.Step.EMPLOYEE)
                .approvedBy(loginUserContext.getUserId())
                .approverIp(clientIp)
                .assetSnapshot(buildAssetSnapshot(ack.getTangibleAsset()))
                .build());
    }

    /**
     * RECEIPT 완료 - 확인서 요청 시점이 아니라 임직원이 승인하는 이 시점에 개인배정을 확정한다.
     * RETURN이 담당자 승인 없이 완료된 경우 - 담당자가 지정할 다음 상태가 없으므로
     * process_config.defaultReturnStatus(S-400 기본 제안값)를 그대로 쓴다.
     */
    private void applyCompletionSideEffect(Acknowledgement ack, ProcessConfig config) {
        TangibleAsset asset = ack.getTangibleAsset();
        if (ack.getType() == Acknowledgement.Type.RECEIPT) {
            asset.assignPersonal(ack.getMemberId());
            openAssignment(asset, ack.getMemberId(), ack.getRequestedBy());
        } else {
            TangibleAsset.LifeStatus nextLifeStatus = config.getDefaultReturnStatus() != null
                    ? config.getDefaultReturnStatus() : TangibleAsset.LifeStatus.STORAGE;
            asset.completeReturn(nextLifeStatus, TangibleAsset.AssignType.UNASSIGNED);
            releaseCurrentAssignment(asset);
        }
    }

    @Transactional
    @Override
    public void approveByManager(UUID acknowledgementId, String clientIp, AcknowledgementApproval.ReturnCondition returnCondition,
                                  TangibleAsset.LifeStatus nextLifeStatus, TangibleAsset.AssignType nextAssignType) {
        Acknowledgement ack = acknowledgementReader.findById(acknowledgementId);
        ack.approveByManager();

        TangibleAsset asset = ack.getTangibleAsset();
        asset.completeReturn(nextLifeStatus, nextAssignType);
        releaseCurrentAssignment(asset);

        acknowledgementApprovalStore.store(AcknowledgementApproval.builder()
                .acknowledgement(ack)
                .approvalStep(AcknowledgementApproval.Step.MANAGER)
                .approvedBy(loginUserContext.getUserId())
                .approverIp(clientIp)
                .assetSnapshot(buildAssetSnapshot(asset))
                .returnCondition(returnCondition)
                .nextLifeStatus(nextLifeStatus)
                .nextAssignType(nextAssignType)
                .build());
    }

    /** TangibleAssetServiceImpl.openAssignment/LoanServiceImpl과 동일한 이유 - 배정 변경은 항상
     * asset_assignment 이력을 함께 남겨야 자산 상세의 "배정 이력" 탭에서 빠지지 않는다. */
    private void openAssignment(TangibleAsset tangibleAsset, UUID memberId, UUID assignedBy) {
        releaseCurrentAssignment(tangibleAsset);
        assetAssignmentStore.store(AssetAssignment.builder()
                .tangibleAsset(tangibleAsset)
                .memberId(memberId)
                .assignType(TangibleAsset.AssignType.PERSONAL.name())
                .assignedBy(assignedBy)
                .build());
    }

    private void releaseCurrentAssignment(TangibleAsset tangibleAsset) {
        assetAssignmentReader.findCurrentByTangibleAssetId(tangibleAsset.getId()).ifPresent(assetAssignmentStore::release);
    }

    @Transactional
    @Override
    public void cancelAcknowledgement(UUID acknowledgementId, String reason) {
        Acknowledgement ack = acknowledgementReader.findById(acknowledgementId);
        ack.cancel(reason);
    }

    private Acknowledgement findOwnAcknowledgement(UUID acknowledgementId) {
        Acknowledgement ack = acknowledgementReader.findById(acknowledgementId);
        if (!ack.getMemberId().equals(loginUserContext.getUserId())) {
            throw new InvalidParamException("본인에게 요청된 확인서만 처리할 수 있습니다.");
        }
        return ack;
    }

    private static String render(String tpl, Map<String, String> vars) {
        String result = tpl;
        for (Map.Entry<String, String> entry : vars.entrySet()) {
            result = result.replace("{{" + entry.getKey() + "}}", entry.getValue() == null ? "" : entry.getValue());
        }
        return result;
    }

    private static String buildAssetSnapshot(TangibleAsset asset) {
        return String.format("자산코드: %s, 자산명: %s, 종류: %s, 생애상태: %s, 배정형태: %s, 취득가액: %s원",
                asset.getAssetCode(), asset.getAssetName(),
                asset.getCategory() != null ? asset.getCategory().getCategoryName() : "-",
                asset.getLifeStatus().getDescription(), asset.getAssignType().getDescription(),
                formatAmount(asset.getAcquisitionAmount()));
    }

    private static String formatAmount(BigDecimal amount) {
        return amount == null ? "-" : NumberFormat.getInstance(Locale.KOREA).format(amount);
    }

    private Pageable toPageable(Integer page, Integer size) {
        int safePage = (page == null || page < 0) ? 0 : page;
        int safeSize = (size == null) ? DEFAULT_PAGE_SIZE : Math.max(1, Math.min(size, MAX_PAGE_SIZE));
        return PageRequest.of(safePage, safeSize, DEFAULT_SORT);
    }
}
