package com.winitech.smartAsset.domain.inventory;

import com.winitech.common.domain.AbstractEntity;
import lombok.*;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.util.UUID;

/**
 * S-301 생성 시 수동으로 제외한 임직원(Q-37) - 휴직·장기출장자 등을 미리 빼서 미확인 건수를 줄인다.
 * member 상태값에 아직 "휴직"이 없어(원 설계는 상태 자동 판정을 전제했다) 자동 판정은 미구현이고,
 * 관리자가 수동으로 선택한 인원만 저장한다.
 */
@Entity
@Getter
@Setter(AccessLevel.PRIVATE)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class InventoryExcludedMember extends AbstractEntity {

    @Id
    @GeneratedValue(generator = "UUID")
    @GenericGenerator(name = "UUID", strategy = "com.winitech.common.library.core.JpaUUIDv7Generator")
    @Column(name = "inventory_excluded_member_id")
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "inventory_id")
    private Inventory inventory;

    @NonNull
    private UUID memberId;

    @Builder
    public InventoryExcludedMember(@NonNull Inventory inventory, @NonNull UUID memberId) {
        this.inventory = inventory;
        this.memberId = memberId;
    }
}
