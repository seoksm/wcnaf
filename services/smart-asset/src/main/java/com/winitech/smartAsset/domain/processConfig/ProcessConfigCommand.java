package com.winitech.smartAsset.domain.processConfig;

import com.winitech.smartAsset.domain.tangibleAsset.TangibleAsset;
import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

@Getter
@Builder
@ToString
public class ProcessConfigCommand {

    @Getter
    @Builder
    public static class UpdateCommand {
        private Boolean loanEnabled;
        private Integer defaultLoanDays;
        private Boolean requireApproval;
        private Boolean blockOnOverdue;
        private Integer maxExtendCount;
        private Integer concurrentLimit;

        private Boolean acknowledgementEnabled;
        private Boolean requireManagerApproval;
        private Boolean blockUnapprovedView;
        private Boolean autoRequestOnAssign;
        private Integer approvalDueDays;
        private Integer remindIntervalDays;
        private TangibleAsset.LifeStatus defaultReturnStatus;
    }
}
