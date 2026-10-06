package com.winitech.smartAsset.domain.processConfig;

import com.winitech.common.domain.AbstractEntity;
import com.winitech.smartAsset.domain.tangibleAsset.TangibleAsset;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Id;
import java.util.UUID;

/**
 * 프로세스 설정 (S-400) - 대여/수령·반납 On-Off와 부속 정책. 워크스페이스당 단일 행만 존재하며
 * (V17 마이그레이션이 {@link #SINGLETON_ID} 고정값으로 시드), 신규 생성·삭제 API 없이 조회·수정만 한다.
 */
@Entity
@Getter
@Setter(AccessLevel.PRIVATE)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@DynamicUpdate
public class ProcessConfig extends AbstractEntity {

    public static final UUID SINGLETON_ID = UUID.fromString("00000000-0000-0000-0000-000000000001");

    @Id
    @Column(name = "process_config_id")
    private UUID id;

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

    @Enumerated(EnumType.STRING)
    private TangibleAsset.LifeStatus defaultReturnStatus;

    public void modify(ProcessConfigCommand.UpdateCommand command) {
        this.loanEnabled = command.getLoanEnabled();
        this.defaultLoanDays = command.getDefaultLoanDays();
        this.requireApproval = command.getRequireApproval();
        this.blockOnOverdue = command.getBlockOnOverdue();
        this.maxExtendCount = command.getMaxExtendCount();
        this.concurrentLimit = command.getConcurrentLimit();

        this.acknowledgementEnabled = command.getAcknowledgementEnabled();
        this.requireManagerApproval = command.getRequireManagerApproval();
        this.blockUnapprovedView = command.getBlockUnapprovedView();
        this.autoRequestOnAssign = command.getAutoRequestOnAssign();
        this.approvalDueDays = command.getApprovalDueDays();
        this.remindIntervalDays = command.getRemindIntervalDays();
        this.defaultReturnStatus = command.getDefaultReturnStatus();
    }
}
