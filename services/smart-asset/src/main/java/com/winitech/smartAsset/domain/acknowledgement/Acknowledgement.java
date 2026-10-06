package com.winitech.smartAsset.domain.acknowledgement;

import com.winitech.common.domain.AbstractEntity;
import com.winitech.common.exception.InvalidParamException;
import com.winitech.smartAsset.domain.tangibleAsset.TangibleAsset;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.DynamicUpdate;
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
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.UUID;

/**
 * S-430~432,440 확인서 요청 1건 - K1(요청 시점 본문 스냅샷 고정)에 따라 {@link #bodySnapshot}은
 * ack_template을 참조하지 않고 완성된 문서 전문을 그대로 들고 있다.
 */
@Entity
@Getter
@Setter(AccessLevel.PRIVATE)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@DynamicUpdate
public class Acknowledgement extends AbstractEntity {

    @Id
    @GeneratedValue(generator = "UUID")
    @GenericGenerator(name = "UUID", strategy = "com.winitech.common.library.core.JpaUUIDv7Generator")
    @Column(name = "acknowledgement_id")
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "tangible_asset_id")
    private TangibleAsset tangibleAsset;

    @NonNull
    private UUID memberId;

    @NonNull
    @Enumerated(EnumType.STRING)
    private Type type;

    @NonNull
    private String bodySnapshot;

    @NonNull
    @Enumerated(EnumType.STRING)
    private Status status;

    private LocalDate dueDate;

    @NonNull
    private UUID requestedBy;

    @NonNull
    private OffsetDateTime requestedAt;

    @NonNull
    private Boolean cancelledYn;

    private String cancelledReason;

    @Getter
    @RequiredArgsConstructor
    public enum Type {
        RECEIPT("수령"),
        RETURN("반납");
        private final String description;
    }

    @Getter
    @RequiredArgsConstructor
    public enum Status {
        PENDING_EMPLOYEE("임직원 승인대기"),
        /** RETURN + process_config.requireManagerApproval=true 일 때만 거치는 단계(K5) */
        PENDING_MANAGER("담당자 승인대기"),
        COMPLETED("완료"),
        CANCELLED("취소");
        private final String description;
    }

    @Builder
    public Acknowledgement(@NonNull TangibleAsset tangibleAsset, @NonNull UUID memberId, @NonNull Type type,
                            @NonNull String bodySnapshot, LocalDate dueDate, @NonNull UUID requestedBy) {
        this.tangibleAsset = tangibleAsset;
        this.memberId = memberId;
        this.type = type;
        this.bodySnapshot = bodySnapshot;
        this.dueDate = dueDate;
        this.requestedBy = requestedBy;
        this.requestedAt = OffsetDateTime.now();
        this.status = Status.PENDING_EMPLOYEE;
        this.cancelledYn = false;
    }

    /** S-440 K4 - 임직원 본인 승인. RETURN이고 담당자 승인 옵션이 켜져 있으면 한 단계 더 거친다(K5) */
    public void approveByEmployee(boolean requireManagerApproval) {
        if (this.status != Status.PENDING_EMPLOYEE) {
            throw new InvalidParamException("임직원 승인대기 상태가 아닙니다.");
        }
        this.status = (this.type == Type.RETURN && requireManagerApproval) ? Status.PENDING_MANAGER : Status.COMPLETED;
    }

    /** S-432 - 담당자 승인. 자산 상태 전환은 호출자(서비스)가 TangibleAsset에 함께 적용한다 */
    public void approveByManager() {
        if (this.status != Status.PENDING_MANAGER) {
            throw new InvalidParamException("담당자 승인대기 상태가 아닙니다.");
        }
        this.status = Status.COMPLETED;
    }

    /** S-431 - 완료 전 요청 자체를 취소(K3 - 취소는 삭제가 아니라 상태 전이) */
    public void cancel(String reason) {
        if (this.status == Status.COMPLETED || this.status == Status.CANCELLED) {
            throw new InvalidParamException("이미 완료되었거나 취소된 요청입니다.");
        }
        this.status = Status.CANCELLED;
        this.cancelledYn = true;
        this.cancelledReason = reason;
    }

    public boolean isOverdue() {
        return (this.status == Status.PENDING_EMPLOYEE || this.status == Status.PENDING_MANAGER)
                && this.dueDate != null && this.dueDate.isBefore(LocalDate.now());
    }
}
