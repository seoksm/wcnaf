package com.winitech.smartAsset.domain.loan;

import com.winitech.common.domain.AbstractEntity;
import com.winitech.common.exception.InvalidParamException;
import com.winitech.smartAsset.domain.tangibleAsset.TangibleAsset;
import lombok.*;
import org.hibernate.annotations.DynamicUpdate;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.UUID;

/** S-410~412,420~421 대여 1건 - QR 스캔(본인) 또는 관리자 대행(S-412)으로 생성, 반납 후에도
 * 삭제하지 않는 append-only 이력(L5). 동시 대여 경쟁 방지(L1)는 이 엔티티가 아니라 대여를 시작할
 * 때 함께 전이하는 {@link TangibleAsset}의 @Version 낙관적 잠금이 담당한다. */
@Entity
@Getter
@Setter(AccessLevel.PRIVATE)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@DynamicUpdate
public class Loan extends AbstractEntity {

    @Id
    @GeneratedValue(generator = "UUID")
    @GenericGenerator(name = "UUID", strategy = "com.winitech.common.library.core.JpaUUIDv7Generator")
    @Column(name = "loan_id")
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "tangible_asset_id")
    private TangibleAsset tangibleAsset;

    @NonNull
    private UUID memberId;

    @NonNull
    @Enumerated(EnumType.STRING)
    private Status status;

    @NonNull
    private LocalDate dueDate;

    @NonNull
    private Integer extendCount;

    @NonNull
    private OffsetDateTime borrowedAt;

    private UUID approvedBy;
    private OffsetDateTime approvedAt;
    private String rejectReason;

    private OffsetDateTime returnedAt;

    @Enumerated(EnumType.STRING)
    private ReturnCondition returnCondition;

    @NonNull
    private UUID createdBy;

    @Getter
    @RequiredArgsConstructor
    public enum Status {
        PENDING_APPROVAL("승인대기"),
        ACTIVE("대여중"),
        RETURNED("반납완료"),
        REJECTED("반려");
        private final String description;
    }

    @Getter
    @RequiredArgsConstructor
    public enum ReturnCondition {
        NORMAL("정상"),
        ABNORMAL("이상");
        private final String description;
    }

    /**
     * S-410/420: 대여 시작. 호출자(서비스)가 이미 {@link TangibleAsset#borrow}로 자산을 ON_LOAN
     * 전이해둔 뒤 이 레코드를 만든다. Q-39 옵션에 따라 승인대기로 시작할지 즉시 대여중으로 시작할지
     * 갈린다.
     */
    @Builder
    public Loan(@NonNull TangibleAsset tangibleAsset, @NonNull UUID memberId, @NonNull LocalDate dueDate,
                boolean requireApproval, @NonNull UUID createdBy) {
        this.tangibleAsset = tangibleAsset;
        this.memberId = memberId;
        this.dueDate = dueDate;
        this.extendCount = 0;
        this.borrowedAt = OffsetDateTime.now();
        this.createdBy = createdBy;
        this.status = requireApproval ? Status.PENDING_APPROVAL : Status.ACTIVE;
    }

    /** S-411: 승인대기 → 대여중 */
    public void approve(UUID approverId) {
        if (this.status != Status.PENDING_APPROVAL) {
            throw new InvalidParamException("승인 대기 상태가 아닙니다.");
        }
        this.status = Status.ACTIVE;
        this.approvedBy = approverId;
        this.approvedAt = OffsetDateTime.now();
    }

    /** S-411: 반려 - 자산을 다시 대여가능으로 되돌리는 것은 호출자(서비스)가 TangibleAsset에 함께 적용한다 */
    public void reject(UUID approverId, String reason) {
        if (this.status != Status.PENDING_APPROVAL) {
            throw new InvalidParamException("승인 대기 상태가 아닙니다.");
        }
        this.status = Status.REJECTED;
        this.approvedBy = approverId;
        this.approvedAt = OffsetDateTime.now();
        this.rejectReason = reason;
    }

    /** S-421: 반납 - 자산 상태 전환(REPAIR 등)은 호출자가 TangibleAsset에 함께 적용한다(L3) */
    public void returnLoan(boolean abnormal) {
        if (this.status != Status.ACTIVE) {
            throw new InvalidParamException("대여중 상태가 아닙니다.");
        }
        this.status = Status.RETURNED;
        this.returnedAt = OffsetDateTime.now();
        this.returnCondition = abnormal ? ReturnCondition.ABNORMAL : ReturnCondition.NORMAL;
    }

    /** S-421: 연장 (Q-41 한도까지만) */
    public void extend(int maxExtendCount, int extensionDays) {
        if (this.status != Status.ACTIVE) {
            throw new InvalidParamException("대여중 상태가 아닙니다.");
        }
        if (this.extendCount >= maxExtendCount) {
            throw new InvalidParamException("연장 한도를 모두 사용했습니다.");
        }
        this.extendCount += 1;
        this.dueDate = this.dueDate.plusDays(extensionDays);
    }

    public boolean isOverdue() {
        return this.status == Status.ACTIVE && this.dueDate.isBefore(LocalDate.now());
    }
}
