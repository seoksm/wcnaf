package com.winitech.smartAsset.domain.tangibleAsset;

import com.winitech.common.domain.AbstractEntity;
import com.winitech.common.exception.InvalidParamException;
import com.winitech.smartAsset.domain.assetCategory.AssetCategory;
import com.winitech.smartAsset.domain.assetLocation.AssetLocation;
import lombok.*;
import org.hibernate.annotations.DynamicUpdate;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.EnumMap;
import java.util.EnumSet;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

@Entity
@Getter
@Setter(AccessLevel.PRIVATE)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@DynamicUpdate
public class TangibleAsset extends AbstractEntity {

    @Id
    @GeneratedValue(generator = "UUID")
    @GenericGenerator(name = "UUID", strategy = "com.winitech.common.library.core.JpaUUIDv7Generator")
    @Column(name = "tangible_asset_id")
    private UUID id;

    /**
     * 낙관적 잠금(D8) - 배정 변경·회수·불용 전환 등 이 자산을 갱신하는 모든 경로가 공유하는 버전
     * 카운터. 두 요청이 같은 자산을 동시에 수정하면 먼저 커밋한 쪽만 성공하고, 나중 요청은 버전
     * 불일치로 실패한다(ObjectOptimisticLockingFailureException) - PESSIMISTIC_WRITE 행 잠금 대신
     * 이 방식을 선택해 조회(읽기 전용 이력 조회 등)에는 어떤 잠금 비용도 들지 않는다.
     */
    @Version
    private Long version;

    @NonNull
    private String assetCode;

    @NonNull
    private String assetName;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "asset_category_id")
    private AssetCategory category;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "asset_location_id")
    private AssetLocation location;

    @NonNull
    @Enumerated(EnumType.STRING)
    private LifeStatus lifeStatus;

    @NonNull
    @Enumerated(EnumType.STRING)
    private AssignType assignType;

    @NonNull
    private LocalDate acquisitionDate;

    @NonNull
    private BigDecimal acquisitionAmount;

    private String modelName;
    private String manufacturer;
    private String serialNo;

    private UUID currentMemberId;

    private String memo;

    @NonNull
    @Enumerated(EnumType.STRING)
    private Status status;

    /** D4: 생애상태(lifeStatus)가 마지막으로 바뀐 시각 - 불용/처분완료 자산의 감가상각 동결 시점으로
     * 쓰인다(DepreciationCalculator). 등록 시점과 lifeStatus가 실제로 바뀌는 모든 경로(modify,
     * disuse, restoreFromDisuse, dispose)에서 함께 갱신되므로 이 값과 현재 lifeStatus만으로
     * "지금 상태가 언제부터였는지"를 항상 알 수 있다. */
    @NonNull
    private OffsetDateTime lifeStatusChangedAt;

    @Getter
    @RequiredArgsConstructor
    public enum LifeStatus {
        USE("사용"),
        STORAGE("보관"),
        REPAIR("수리중"),
        DISUSE("불용"),
        DISPOSED("처분완료");
        private final String description;
    }

    @Getter
    @RequiredArgsConstructor
    public enum AssignType {
        UNASSIGNED("미배정"),
        PERSONAL("개인배정"),
        SHARED("공용"),
        LOANABLE("대여가능"),
        ON_LOAN("대여중");
        private final String description;
    }

    @Getter
    @RequiredArgsConstructor
    public enum Status {
        ENABLE("활성화"),
        DISABLE("비활성화");
        private final String description;
    }

    /** Q-15 2축 상태 조합 유효성 매트릭스 (설계문서 §1) */
    private static final Map<LifeStatus, Set<AssignType>> VALID_ASSIGN_TYPES_BY_LIFE_STATUS = new EnumMap<>(LifeStatus.class);
    static {
        VALID_ASSIGN_TYPES_BY_LIFE_STATUS.put(LifeStatus.USE, EnumSet.allOf(AssignType.class));
        VALID_ASSIGN_TYPES_BY_LIFE_STATUS.put(LifeStatus.STORAGE, EnumSet.of(AssignType.UNASSIGNED, AssignType.LOANABLE));
        VALID_ASSIGN_TYPES_BY_LIFE_STATUS.put(LifeStatus.REPAIR, EnumSet.of(AssignType.UNASSIGNED, AssignType.PERSONAL, AssignType.SHARED, AssignType.ON_LOAN));
        VALID_ASSIGN_TYPES_BY_LIFE_STATUS.put(LifeStatus.DISUSE, EnumSet.of(AssignType.UNASSIGNED));
        VALID_ASSIGN_TYPES_BY_LIFE_STATUS.put(LifeStatus.DISPOSED, EnumSet.of(AssignType.UNASSIGNED));
    }

    /** PERSONAL·ON_LOAN 배정 형태만 특정 사용자(currentMemberId)를 필요로 한다 */
    public static boolean requiresMember(AssignType assignType) {
        return assignType == AssignType.PERSONAL || assignType == AssignType.ON_LOAN;
    }

    /** Q-15 2축 조합 유효성 검사 (엑셀 업서트 미리보기 등에서 사전 검증용으로도 재사용) */
    public static boolean isValidCombination(LifeStatus lifeStatus, AssignType assignType) {
        return VALID_ASSIGN_TYPES_BY_LIFE_STATUS.get(lifeStatus).contains(assignType);
    }

    private static void validateCombination(LifeStatus lifeStatus, AssignType assignType) {
        if (!isValidCombination(lifeStatus, assignType)) {
            throw new InvalidParamException(
                    "생애 상태 '" + lifeStatus.getDescription() + "'에서는 배정 형태 '" + assignType.getDescription() + "'를 선택할 수 없습니다.");
        }
    }

    /**
     * 배정 형태-배정 사용자 불변식을 강제하는 단일 지점. 등록(생성자)과 수정(modify)이 모두
     * 이 메서드 하나만 거치므로, 두 진입점(그리고 그 위의 일괄 변경·복제·엑셀 업서트까지)이
     * 서로 다른 규칙을 적용할 위험이 없다.
     * - PERSONAL/ON_LOAN이면 currentMemberId가 반드시 있어야 한다(없으면 InvalidParamException).
     * - 그 외(UNASSIGNED/SHARED/LOANABLE)는 currentMemberId를 null로 정규화한다.
     */
    private static UUID resolveMemberId(AssignType assignType, UUID currentMemberId) {
        if (requiresMember(assignType)) {
            if (currentMemberId == null) {
                throw new InvalidParamException(
                        "배정 형태 '" + assignType.getDescription() + "'는 배정 대상 사용자가 필요합니다.");
            }
            return currentMemberId;
        }
        return null;
    }

    @Builder
    public TangibleAsset(
            @NonNull String assetCode,
            @NonNull String assetName,
            AssetCategory category,
            AssetLocation location,
            LifeStatus lifeStatus,
            AssignType assignType,
            @NonNull LocalDate acquisitionDate,
            @NonNull BigDecimal acquisitionAmount,
            String modelName,
            String manufacturer,
            String serialNo,
            UUID currentMemberId,
            String memo
    ) {
        LifeStatus resolvedLifeStatus = lifeStatus == null ? LifeStatus.USE : lifeStatus;
        AssignType resolvedAssignType = assignType == null ? AssignType.UNASSIGNED : assignType;
        validateCombination(resolvedLifeStatus, resolvedAssignType);

        this.assetCode = assetCode;
        this.assetName = assetName;
        this.category = category;
        this.location = location;
        this.lifeStatus = resolvedLifeStatus;
        this.assignType = resolvedAssignType;
        this.acquisitionDate = acquisitionDate;
        this.acquisitionAmount = acquisitionAmount;
        this.modelName = modelName;
        this.manufacturer = manufacturer;
        this.serialNo = serialNo;
        this.currentMemberId = resolveMemberId(resolvedAssignType, currentMemberId);
        this.memo = memo;
        this.status = Status.ENABLE;
        this.lifeStatusChangedAt = OffsetDateTime.now();
    }

    /**
     * @return 배정 정보가 실질적으로 변경되었는지 여부 (asset_assignment 이력 갱신 필요 판단용)
     */
    public boolean modify(TangibleAssetCommand.UpdateCommand command, AssetCategory category, AssetLocation location) {
        // 처분완료 자산은 더 이상 어떤 필드도 바꿀 수 없다(Q-28, 처분은 되돌릴 수 없음). 또한 일반
        // 수정 경로로 처분완료로 전환하는 것도 막는다 - dispose()를 거쳐야만 disposal_asset 행이
        // 함께 생성되므로, 이 경로를 그대로 열어두면 처분 기록 없는 처분완료 자산이 생길 수 있다.
        if (this.lifeStatus == LifeStatus.DISPOSED) {
            throw new InvalidParamException("처분완료된 자산은 수정할 수 없습니다.");
        }
        if (command.getLifeStatus() == LifeStatus.DISPOSED) {
            throw new InvalidParamException("처분 처리는 처분 등록 절차를 통해서만 할 수 있습니다.");
        }

        LifeStatus newLifeStatus = command.getLifeStatus();
        AssignType newAssignType = command.getAssignType();

        // A1: 불용 전환 시 배정 강제 해제
        if (newLifeStatus == LifeStatus.DISUSE) {
            newAssignType = AssignType.UNASSIGNED;
        }

        validateCombination(newLifeStatus, newAssignType);

        UUID newMemberId = resolveMemberId(newAssignType, command.getCurrentMemberId());
        boolean assignmentChanged = this.assignType != newAssignType
                || !java.util.Objects.equals(this.currentMemberId, newMemberId);

        if (this.lifeStatus != newLifeStatus) {
            this.lifeStatusChangedAt = OffsetDateTime.now();
        }

        this.assetName = command.getAssetName();
        this.category = category;
        this.location = location;
        this.lifeStatus = newLifeStatus;
        this.assignType = newAssignType;
        this.acquisitionDate = command.getAcquisitionDate();
        this.acquisitionAmount = command.getAcquisitionAmount();
        this.modelName = command.getModelName();
        this.manufacturer = command.getManufacturer();
        this.serialNo = command.getSerialNo();
        this.currentMemberId = newMemberId;
        this.memo = command.getMemo();

        return assignmentChanged;
    }

    /** S-410/420: QR 스캔(또는 관리자 대행)으로 대여 시작. LOANABLE에서만 가능하고, 동시 스캔
     * 경쟁은 이 엔티티의 @Version 낙관적 잠금이 그대로 막아준다(L1) - loan 도메인은 별도 잠금이 없다. */
    public void borrow(UUID memberId) {
        if (this.assignType != AssignType.LOANABLE) {
            throw new InvalidParamException("대여할 수 없는 자산입니다. 이미 대여 중이거나 대여 가능 상태가 아닙니다.");
        }
        validateCombination(this.lifeStatus, AssignType.ON_LOAN);
        this.assignType = AssignType.ON_LOAN;
        this.currentMemberId = memberId;
    }

    /** S-421: 반납. 이상(abnormal) 반납이면 수리중으로 전환하고 대여가능 풀에서 빠진다(L3) */
    public void returnFromLoan(boolean abnormal) {
        if (this.assignType != AssignType.ON_LOAN) {
            throw new InvalidParamException("대여 중인 자산이 아닙니다.");
        }
        if (abnormal) {
            this.lifeStatus = LifeStatus.REPAIR;
            this.assignType = AssignType.UNASSIGNED;
            this.lifeStatusChangedAt = OffsetDateTime.now();
        } else {
            validateCombination(this.lifeStatus, AssignType.LOANABLE);
            this.assignType = AssignType.LOANABLE;
        }
        this.currentMemberId = null;
    }

    /**
     * S-432: 반납확인서 담당자 승인 - 담당자가 승인과 동시에 자산의 다음 상태를 지정한다("이 한
     * 화면에서 처리해야 자산이 붕 뜨지 않는다" - 설계문서 §4). 배정은 항상 해제한다(반납이므로
     * currentMemberId를 요구하는 형태로는 지정할 수 없다).
     */
    public void completeReturn(LifeStatus nextLifeStatus, AssignType nextAssignType) {
        if (requiresMember(nextAssignType)) {
            throw new InvalidParamException("반납 후 상태로 '" + nextAssignType.getDescription() + "'을 지정할 수 없습니다.");
        }
        validateCombination(nextLifeStatus, nextAssignType);
        if (this.lifeStatus != nextLifeStatus) {
            this.lifeStatusChangedAt = OffsetDateTime.now();
        }
        this.lifeStatus = nextLifeStatus;
        this.assignType = nextAssignType;
        this.currentMemberId = null;
    }

    /**
     * S-440 수령확인서 승인 - 확인서 승인이 곧 개인배정을 확정하는 트리거다(요청 시점이 아니라
     * 승인 시점). 이미 다른 사람에게 배정돼 있으면 조용히 덮어쓰지 않고 거부한다 - 동시성은
     * 이 엔티티의 @Version 낙관적 잠금이 커버한다.
     */
    public void assignPersonal(UUID memberId) {
        if (requiresMember(this.assignType) && !memberId.equals(this.currentMemberId)) {
            throw new InvalidParamException("이미 다른 사용자에게 배정된 자산입니다.");
        }
        validateCombination(this.lifeStatus, AssignType.PERSONAL);
        this.assignType = AssignType.PERSONAL;
        this.currentMemberId = memberId;
    }

    /** S-218: 현재 배정을 회수하고 미배정 상태로 되돌린다 */
    public void releaseAssignment() {
        if (!requiresMember(this.assignType) || this.currentMemberId == null) {
            throw new InvalidParamException("현재 배정된 사용자가 없습니다.");
        }
        this.assignType = AssignType.UNASSIGNED;
        this.currentMemberId = null;
    }

    /**
     * S-241: 불용 처리 - 사용/보관/수리중에서만 가능하고, A1(배정 강제 해제)을 적용한다.
     * @return 배정 정보가 실질적으로 변경되었는지 여부 (호출자가 asset_assignment를 회수 처리해야 하는지 판단용)
     */
    public boolean disuse() {
        if (this.lifeStatus == LifeStatus.DISUSE || this.lifeStatus == LifeStatus.DISPOSED) {
            throw new InvalidParamException("이미 불용 또는 처분완료 상태인 자산입니다.");
        }
        boolean assignmentChanged = this.assignType != AssignType.UNASSIGNED || this.currentMemberId != null;
        this.lifeStatus = LifeStatus.DISUSE;
        this.assignType = AssignType.UNASSIGNED;
        this.currentMemberId = null;
        this.lifeStatusChangedAt = OffsetDateTime.now();
        return assignmentChanged;
    }

    /** S-240: 불용 → 사용 복귀 (Q-28 - 처분대기(불용) 상태만 복귀 허용, 처분완료는 되돌릴 수 없음) */
    public void restoreFromDisuse() {
        if (this.lifeStatus != LifeStatus.DISUSE) {
            throw new InvalidParamException("불용 상태의 자산만 사용 복귀할 수 있습니다.");
        }
        this.lifeStatus = LifeStatus.USE;
        this.lifeStatusChangedAt = OffsetDateTime.now();
    }

    /** S-242: 처분 처리 (Q-28 - 처분완료는 다시 되돌릴 수 없음). disposal_asset 행 생성은 호출자(서비스)
     * 책임이다 - 이 엔티티는 자기 자신의 상태 전이 불변식만 지킨다. */
    public void dispose() {
        if (this.lifeStatus != LifeStatus.DISUSE) {
            throw new InvalidParamException("불용 상태의 자산만 처분 처리할 수 있습니다.");
        }
        this.lifeStatus = LifeStatus.DISPOSED;
        this.lifeStatusChangedAt = OffsetDateTime.now();
    }

    /**
     * S-220/221 히스토리 diff용 필드 스냅샷. 항목명은 화면에 그대로 노출되는 한글 라벨을 키로 쓴다.
     * (전체 엔티티 스냅샷이 아니라 변경 여부를 비교할 값만 담는다 — §2 "변경 필드만 저장")
     */
    public Map<String, String> diffableFields() {
        Map<String, String> fields = new LinkedHashMap<>();
        fields.put("자산명", assetName);
        fields.put("자산종류", category != null ? category.getCategoryName() : null);
        fields.put("자산위치", location != null ? location.getLocationName() : null);
        fields.put("생애상태", lifeStatus != null ? lifeStatus.getDescription() : null);
        fields.put("배정형태", assignType != null ? assignType.getDescription() : null);
        fields.put("취득일", acquisitionDate != null ? acquisitionDate.toString() : null);
        // stripTrailingZeros로 정규화 - DB에서 읽은 값(scale 2)과 요청으로 새로 들어온 값(scale 0)의
        // 표현 차이(예: "1500000.00" vs "1500000")만으로 실제 변경 없는 필드가 diff에 잡히는 것을 막는다
        fields.put("취득가액", acquisitionAmount != null ? acquisitionAmount.stripTrailingZeros().toPlainString() : null);
        fields.put("모델명", modelName);
        fields.put("제조사", manufacturer);
        fields.put("시리얼번호", serialNo);
        fields.put("사용자", currentMemberId != null ? currentMemberId.toString() : null);
        fields.put("메모", memo);
        return fields;
    }
}
