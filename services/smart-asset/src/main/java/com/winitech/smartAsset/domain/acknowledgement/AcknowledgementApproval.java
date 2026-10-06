package com.winitech.smartAsset.domain.acknowledgement;

import com.winitech.common.domain.AbstractEntity;
import com.winitech.smartAsset.domain.tangibleAsset.TangibleAsset;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import java.time.OffsetDateTime;
import java.util.UUID;

/**
 * K2/K3 - 승인 단계(임직원/담당자)마다 쌓이는 append-only 증빙. 수정·삭제 API를 두지 않는다 -
 * 잘못된 승인은 {@link Acknowledgement#cancel}로 요청 자체를 취소하는 것으로 처리한다.
 */
@Entity
@Getter
@Setter(AccessLevel.PRIVATE)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class AcknowledgementApproval extends AbstractEntity {

    @Id
    @GeneratedValue(generator = "UUID")
    @GenericGenerator(name = "UUID", strategy = "com.winitech.common.library.core.JpaUUIDv7Generator")
    @Column(name = "acknowledgement_approval_id")
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "acknowledgement_id")
    private Acknowledgement acknowledgement;

    @NonNull
    @Enumerated(EnumType.STRING)
    private Step approvalStep;

    @NonNull
    private UUID approvedBy;

    @NonNull
    private OffsetDateTime approvedAt;

    private String approverIp;

    private String assetSnapshot;

    @Enumerated(EnumType.STRING)
    private ReturnCondition returnCondition;

    @Enumerated(EnumType.STRING)
    private TangibleAsset.LifeStatus nextLifeStatus;

    @Enumerated(EnumType.STRING)
    private TangibleAsset.AssignType nextAssignType;

    @Getter
    @RequiredArgsConstructor
    public enum Step {
        EMPLOYEE("임직원"),
        MANAGER("담당자");
        private final String description;
    }

    @Getter
    @RequiredArgsConstructor
    public enum ReturnCondition {
        NORMAL("정상"),
        ABNORMAL("이상");
        private final String description;
    }

    @Builder
    public AcknowledgementApproval(@NonNull Acknowledgement acknowledgement, @NonNull Step approvalStep,
                                    @NonNull UUID approvedBy, String approverIp, String assetSnapshot,
                                    ReturnCondition returnCondition, TangibleAsset.LifeStatus nextLifeStatus,
                                    TangibleAsset.AssignType nextAssignType) {
        this.acknowledgement = acknowledgement;
        this.approvalStep = approvalStep;
        this.approvedBy = approvedBy;
        this.approvedAt = OffsetDateTime.now();
        this.approverIp = approverIp;
        this.assetSnapshot = assetSnapshot;
        this.returnCondition = returnCondition;
        this.nextLifeStatus = nextLifeStatus;
        this.nextAssignType = nextAssignType;
    }
}
