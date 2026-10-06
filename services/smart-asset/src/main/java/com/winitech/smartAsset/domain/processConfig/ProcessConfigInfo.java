package com.winitech.smartAsset.domain.processConfig;

import com.winitech.smartAsset.domain.tangibleAsset.TangibleAsset;
import lombok.Getter;

@Getter
public class ProcessConfigInfo {

    private final Boolean loanEnabled;
    private final Integer defaultLoanDays;
    private final Boolean requireApproval;
    private final Boolean blockOnOverdue;
    private final Integer maxExtendCount;
    private final Integer concurrentLimit;

    private final Boolean acknowledgementEnabled;
    private final Boolean requireManagerApproval;
    private final Boolean blockUnapprovedView;
    private final Boolean autoRequestOnAssign;
    private final Integer approvalDueDays;
    private final Integer remindIntervalDays;
    private final TangibleAsset.LifeStatus defaultReturnStatus;

    public ProcessConfigInfo(ProcessConfig processConfig) {
        this.loanEnabled = processConfig.getLoanEnabled();
        this.defaultLoanDays = processConfig.getDefaultLoanDays();
        this.requireApproval = processConfig.getRequireApproval();
        this.blockOnOverdue = processConfig.getBlockOnOverdue();
        this.maxExtendCount = processConfig.getMaxExtendCount();
        this.concurrentLimit = processConfig.getConcurrentLimit();

        this.acknowledgementEnabled = processConfig.getAcknowledgementEnabled();
        this.requireManagerApproval = processConfig.getRequireManagerApproval();
        this.blockUnapprovedView = processConfig.getBlockUnapprovedView();
        this.autoRequestOnAssign = processConfig.getAutoRequestOnAssign();
        this.approvalDueDays = processConfig.getApprovalDueDays();
        this.remindIntervalDays = processConfig.getRemindIntervalDays();
        this.defaultReturnStatus = processConfig.getDefaultReturnStatus();
    }
}
