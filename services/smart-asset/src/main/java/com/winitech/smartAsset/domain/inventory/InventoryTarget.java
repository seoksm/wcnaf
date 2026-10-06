package com.winitech.smartAsset.domain.inventory;

import com.winitech.common.domain.AbstractEntity;
import com.winitech.smartAsset.domain.tangibleAsset.TangibleAsset;
import lombok.*;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.util.UUID;

/** I1 스냅샷 - 조사 시작 시점에 고정한 대상 자산(+책임자) 1건. 조사 중 변경되지 않는다 */
@Entity
@Getter
@Setter(AccessLevel.PRIVATE)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class InventoryTarget extends AbstractEntity {

    @Id
    @GeneratedValue(generator = "UUID")
    @GenericGenerator(name = "UUID", strategy = "com.winitech.common.library.core.JpaUUIDv7Generator")
    @Column(name = "inventory_target_id")
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "inventory_id")
    private Inventory inventory;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "tangible_asset_id")
    private TangibleAsset tangibleAsset;

    /** 임직원형(MEMBER)에서만 값이 있다 - 이 자산을 책임지는 사람. 관리자형(ADMIN)은 null(검수자 풀 전체가 담당) */
    private UUID memberId;

    /** 조사 시작 시점 자산위치 스냅샷 - 검수 결과의 위치불일치 판정 기준 */
    private UUID expectedLocationId;

    /** 조사 시작 시점 배정형태 스냅샷(참고용 표시) */
    @Enumerated(EnumType.STRING)
    private TangibleAsset.AssignType expectedAssignType;

    @Builder
    public InventoryTarget(@NonNull Inventory inventory, @NonNull TangibleAsset tangibleAsset,
                            UUID memberId, UUID expectedLocationId, TangibleAsset.AssignType expectedAssignType) {
        this.inventory = inventory;
        this.tangibleAsset = tangibleAsset;
        this.memberId = memberId;
        this.expectedLocationId = expectedLocationId;
        this.expectedAssignType = expectedAssignType;
    }
}
