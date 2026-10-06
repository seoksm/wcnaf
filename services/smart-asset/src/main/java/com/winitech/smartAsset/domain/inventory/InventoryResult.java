package com.winitech.smartAsset.domain.inventory;

import com.winitech.common.domain.AbstractEntity;
import com.winitech.common.exception.InvalidParamException;
import lombok.*;
import org.hibernate.annotations.DynamicUpdate;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.time.OffsetDateTime;
import java.util.UUID;

/** I4/I5/S-304/S-308 검수 결과 - InventoryTarget과 1:1, 조사 진행 중 계속 갱신되는 가변 상태 */
@Entity
@Getter
@Setter(AccessLevel.PRIVATE)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@DynamicUpdate
public class InventoryResult extends AbstractEntity {

    @Id
    @GeneratedValue(generator = "UUID")
    @GenericGenerator(name = "UUID", strategy = "com.winitech.common.library.core.JpaUUIDv7Generator")
    @Column(name = "inventory_result_id")
    private UUID id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "inventory_target_id")
    private InventoryTarget inventoryTarget;

    @NonNull
    @Enumerated(EnumType.STRING)
    private Status status;

    @Enumerated(EnumType.STRING)
    private AnomalyType anomalyType;

    private String note;

    private UUID reviewedBy;

    private OffsetDateTime reviewedAt;

    private String rejectionReason;

    @Enumerated(EnumType.STRING)
    private ClosureAction closureAction;

    @Enumerated(EnumType.STRING)
    private ClosureReasonCode closureReasonCode;

    private String closureNote;

    /** S-311 라벨 없음/훼손 예외(I3) - 스캔 없이 촬영한 사진(commonFile fileId). 있으면 승인이 강제된다 */
    private UUID photoFileId;

    private OffsetDateTime photoCapturedAt;

    private OffsetDateTime photoUploadedAt;

    @Getter
    @RequiredArgsConstructor
    public enum Status {
        UNCONFIRMED("미확인"),
        PENDING_APPROVAL("승인대기"),
        CONFIRMED("확인완료"),
        /** I4: 확인은 됐지만 대장과 다른 경우 - CONFIRMED와 별도 상태 */
        ANOMALY("이상");
        private final String description;
    }

    @Getter
    @RequiredArgsConstructor
    public enum AnomalyType {
        DAMAGE("파손"),
        LOCATION_MISMATCH("위치불일치"),
        WRONG_HOLDER("타인보유"),
        OTHER("기타");
        private final String description;
    }

    @Getter
    @RequiredArgsConstructor
    public enum ClosureAction {
        CARRY_OVER("차기이월"),
        MANUAL_VERIFY("소재확인"),
        /** R6: 되돌리기 어려운 동작 - 일괄 처리 대상에서 제외(서비스 계층에서 강제) */
        LOST("분실");
        private final String description;
    }

    @Getter
    @RequiredArgsConstructor
    public enum ClosureReasonCode {
        NOT_PARTICIPATED("미참여"),
        ON_LEAVE("휴직"),
        LABEL_DAMAGED("라벨훼손"),
        LOCATION_UNKNOWN("소재불명");
        private final String description;
    }

    @Builder
    public InventoryResult(@NonNull InventoryTarget inventoryTarget) {
        this.inventoryTarget = inventoryTarget;
        this.status = Status.UNCONFIRMED;
    }

    /**
     * I3: QR 스캔(또는 관리자 대체 확인)으로 확인 처리. 검수자 승인 옵션이 켜져 있으면 승인대기로,
     * 아니면 바로 확인완료로 - 이 판단 기준(inventory.approvalRequired)은 호출자(서비스)가 넘겨준다.
     */
    public void confirm(boolean approvalRequired) {
        this.status = approvalRequired ? Status.PENDING_APPROVAL : Status.CONFIRMED;
        this.anomalyType = null;
        this.rejectionReason = null;
    }

    /**
     * I3 예외 경로(S-311) - 라벨이 없거나 훼손돼 QR 스캔이 불가능한 경우, 목록에서 직접 선택하고
     * 사진을 찍어 확인한다. 승인 옵션(approvalRequired)과 무관하게 항상 승인대기로 만든다 - 스캔
     * 없이 셀프 리포트한 확인이라 검수자 확인을 강제한다.
     */
    public void confirmWithoutScan(UUID photoFileId, OffsetDateTime capturedAt, OffsetDateTime uploadedAt) {
        if (photoFileId == null) {
            throw new InvalidParamException("라벨 없음/훼손 확인은 사진이 필수입니다.");
        }
        this.status = Status.PENDING_APPROVAL;
        this.photoFileId = photoFileId;
        this.photoCapturedAt = capturedAt;
        this.photoUploadedAt = uploadedAt;
        this.anomalyType = null;
        this.rejectionReason = null;
    }

    /** I4: 파손·위치불일치·타인보유 등 이상 보고 - 승인 옵션과 무관하게 항상 관리자 확인이 필요한 별도 상태 */
    public void reportAnomaly(AnomalyType anomalyType, String note) {
        if (anomalyType == null) {
            throw new InvalidParamException("이상 유형은 필수입니다.");
        }
        this.status = Status.ANOMALY;
        this.anomalyType = anomalyType;
        this.note = note;
        this.rejectionReason = null;
    }

    /** S-304: 승인대기 → 확인완료 */
    public void approve(UUID reviewerId) {
        if (this.status != Status.PENDING_APPROVAL) {
            throw new InvalidParamException("승인 대기 상태가 아닙니다.");
        }
        this.status = Status.CONFIRMED;
        this.reviewedBy = reviewerId;
        this.reviewedAt = OffsetDateTime.now();
    }

    /** I5: 반려하면 미확인으로 되돌아가고, 임직원에게 재검수 알림이 간다(알림 발송은 이번 단계 미구현) */
    public void reject(UUID reviewerId, String reason) {
        if (this.status != Status.PENDING_APPROVAL) {
            throw new InvalidParamException("승인 대기 상태가 아닙니다.");
        }
        this.status = Status.UNCONFIRMED;
        this.reviewedBy = reviewerId;
        this.reviewedAt = OffsetDateTime.now();
        this.rejectionReason = reason;
    }

    /** S-308: 조사 종료 시 아직 미확인인 항목에 대한 강제 처리 결정 - 분실(LOST)의 일괄 여부 제한(R6)은 서비스 계층 책임 */
    public void close(ClosureAction action, ClosureReasonCode reasonCode, String note) {
        if (this.status != Status.UNCONFIRMED) {
            throw new InvalidParamException("미확인 상태의 항목만 종결 처리할 수 있습니다.");
        }
        if (action == null || reasonCode == null) {
            throw new InvalidParamException("종결 처리 방법과 사유는 필수입니다.");
        }
        this.closureAction = action;
        this.closureReasonCode = reasonCode;
        this.closureNote = note;
    }
}
