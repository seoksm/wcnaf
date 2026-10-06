package com.winitech.smartAsset.domain.acknowledgement;

import com.winitech.smartAsset.domain.tangibleAsset.TangibleAsset;
import lombok.Getter;

import java.time.OffsetDateTime;
import java.util.UUID;

/** S-432/S-440 승인 증빙 1건 - K2. IP는 부분 마스킹해 노출한다(전체 값은 감사 로그 전용, 이번 단계 미구현) */
@Getter
public class AcknowledgementApprovalInfo {

    private final UUID acknowledgementApprovalId;
    private final AcknowledgementApproval.Step approvalStep;
    private final UUID approvedBy;
    private final OffsetDateTime approvedAt;
    private final String approverIpMasked;
    private final String assetSnapshot;
    private final AcknowledgementApproval.ReturnCondition returnCondition;
    private final TangibleAsset.LifeStatus nextLifeStatus;
    private final TangibleAsset.AssignType nextAssignType;

    public AcknowledgementApprovalInfo(AcknowledgementApproval approval) {
        this.acknowledgementApprovalId = approval.getId();
        this.approvalStep = approval.getApprovalStep();
        this.approvedBy = approval.getApprovedBy();
        this.approvedAt = approval.getApprovedAt();
        this.approverIpMasked = maskIp(approval.getApproverIp());
        this.assetSnapshot = approval.getAssetSnapshot();
        this.returnCondition = approval.getReturnCondition();
        this.nextLifeStatus = approval.getNextLifeStatus();
        this.nextAssignType = approval.getNextAssignType();
    }

    private static String maskIp(String ip) {
        if (ip == null || ip.isEmpty()) return null;
        String[] parts = ip.split("\\.");
        if (parts.length == 4) return parts[0] + "." + parts[1] + ".*.*";
        return ip.length() > 8 ? ip.substring(0, 8) + "***" : "***";
    }
}
