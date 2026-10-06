package com.winitech.smartAsset.domain.inventory;

import com.winitech.common.domain.AbstractEntity;
import com.winitech.common.exception.InvalidParamException;
import lombok.*;
import org.hibernate.annotations.DynamicUpdate;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.time.OffsetDateTime;
import java.util.UUID;

/** S-300~306,308 전수조사(자산실사) 1건 - 아래 자산 스냅샷(InventoryTarget)·검수 결과(InventoryResult)의 상위 집합체 */
@Entity
@Getter
@Setter(AccessLevel.PRIVATE)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@DynamicUpdate
public class Inventory extends AbstractEntity {

    @Id
    @GeneratedValue(generator = "UUID")
    @GenericGenerator(name = "UUID", strategy = "com.winitech.common.library.core.JpaUUIDv7Generator")
    @Column(name = "inventory_id")
    private UUID id;

    @NonNull
    private String title;

    @NonNull
    @Enumerated(EnumType.STRING)
    private InventoryType inventoryType;

    @NonNull
    @Enumerated(EnumType.STRING)
    private Status status;

    @NonNull
    private Boolean approvalRequired;

    @NonNull
    private Boolean allowNewAssetRegistration;

    private OffsetDateTime closedAt;

    private UUID closedBy;

    @Enumerated(EnumType.STRING)
    private RecurrenceRule recurrenceRule;

    @NonNull
    private UUID createdBy;

    @Getter
    @RequiredArgsConstructor
    public enum InventoryType {
        /** 개인배정 자산 - 자산의 현재 배정자가 직접 검수 */
        MEMBER("임직원형"),
        /** 공용·미배정 자산 - inventory_inspector 풀이 검수 (Q-32) */
        ADMIN("관리자형");
        private final String description;
    }

    @Getter
    @RequiredArgsConstructor
    public enum Status {
        IN_PROGRESS("진행중"),
        /** I6: 종료 후 이 조사에 속한 모든 검수 결과는 불변 */
        CLOSED("종료");
        private final String description;
    }

    @Getter
    @RequiredArgsConstructor
    public enum RecurrenceRule {
        QUARTERLY("분기", 3),
        SEMIANNUAL("반기", 6),
        ANNUAL("연1회", 12);
        private final String description;
        private final int months;

        /** S-306: 직전 종료 시점 기준 다음 시행 예정일 - "종료 후 N개월"을 그대로 적용한다 */
        public OffsetDateTime nextDueDate(OffsetDateTime from) {
            return from.plusMonths(months);
        }
    }

    @Builder
    public Inventory(@NonNull String title, @NonNull InventoryType inventoryType,
                      Boolean approvalRequired, Boolean allowNewAssetRegistration,
                      RecurrenceRule recurrenceRule, @NonNull UUID createdBy) {
        this.title = title;
        this.inventoryType = inventoryType;
        this.status = Status.IN_PROGRESS;
        this.approvalRequired = approvalRequired != null && approvalRequired;
        this.allowNewAssetRegistration = allowNewAssetRegistration != null && allowNewAssetRegistration;
        this.recurrenceRule = recurrenceRule;
        this.createdBy = createdBy;
    }

    /** I6: 미확인 종결 처리(S-308)가 먼저 끝나 있어야 한다는 전제는 서비스 계층(모든 대상이 처리됐는지)에서 확인한다 */
    public void close(UUID closedBy) {
        assertInProgress();
        this.status = Status.CLOSED;
        this.closedAt = OffsetDateTime.now();
        this.closedBy = closedBy;
    }

    /** I6: 종료된 조사의 검수 결과는 더 이상 바꿀 수 없다 - 모든 변경 진입점이 이 확인을 거친다 */
    public void assertInProgress() {
        if (this.status != Status.IN_PROGRESS) {
            throw new InvalidParamException("종료된 조사는 더 이상 변경할 수 없습니다.");
        }
    }
}
