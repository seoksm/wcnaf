package com.winitech.smartAsset.domain.inventory;

import com.winitech.common.domain.AbstractEntity;
import lombok.*;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.util.UUID;

/** S-302 관리자형 조사의 검수자 풀 - 특정 자산에 고정되지 않고 이 조사의 모든 대상을 검수할 수 있는 사람 */
@Entity
@Getter
@Setter(AccessLevel.PRIVATE)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class InventoryInspector extends AbstractEntity {

    @Id
    @GeneratedValue(generator = "UUID")
    @GenericGenerator(name = "UUID", strategy = "com.winitech.common.library.core.JpaUUIDv7Generator")
    @Column(name = "inventory_inspector_id")
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "inventory_id")
    private Inventory inventory;

    @NonNull
    private UUID memberId;

    @Builder
    public InventoryInspector(@NonNull Inventory inventory, @NonNull UUID memberId) {
        this.inventory = inventory;
        this.memberId = memberId;
    }
}
