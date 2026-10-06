package com.winitech.smartAsset.domain.assetCategory;

import com.winitech.common.domain.AbstractEntity;
import com.winitech.common.exception.IllegalStatusException;
import lombok.*;
import org.hibernate.annotations.DynamicUpdate;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.math.BigDecimal;
import java.util.UUID;

@Entity
@Getter
@Setter(AccessLevel.PRIVATE)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@DynamicUpdate
public class AssetCategory extends AbstractEntity {

    @Id
    @GeneratedValue(generator = "UUID")
    @GenericGenerator(name = "UUID", strategy = "com.winitech.common.library.core.JpaUUIDv7Generator")
    @Column(name = "asset_category_id")
    private UUID id;

    @NonNull
    private String categoryCode;

    @NonNull
    private String categoryName;

    private Integer sortSeq;

    /**
     * 기본 4종(노트북/데스크탑PC/모니터/기타)은 false - 삭제 불가, 명칭만 수정 가능
     */
    @NonNull
    private Boolean editable;

    private Integer usefulLifeMonths;

    private BigDecimal residualRate;

    @NonNull
    private BigDecimal memorandumValue;

    @NonNull
    @Enumerated(EnumType.STRING)
    private Status status;

    @Getter
    @RequiredArgsConstructor
    public enum Status {
        ENABLE("활성화"),
        DISABLE("비활성화");
        private final String description;
    }

    @Builder
    public AssetCategory(
            @NonNull String categoryCode,
            @NonNull String categoryName,
            Integer sortSeq,
            Integer usefulLifeMonths,
            BigDecimal residualRate,
            BigDecimal memorandumValue
    ) {
        this.categoryCode = categoryCode;
        this.categoryName = categoryName;
        this.sortSeq = sortSeq;
        this.editable = true;
        this.usefulLifeMonths = usefulLifeMonths;
        this.residualRate = residualRate;
        this.memorandumValue = memorandumValue == null ? BigDecimal.valueOf(1000) : memorandumValue;
        this.status = Status.ENABLE;
    }

    public void modify(AssetCategoryCommand.UpdateCommand updateCommand) {
        this.categoryName = updateCommand.getCategoryName();
        this.sortSeq = updateCommand.getSortSeq();
        this.usefulLifeMonths = updateCommand.getUsefulLifeMonths();
        this.residualRate = updateCommand.getResidualRate();
        if (updateCommand.getMemorandumValue() != null) {
            this.memorandumValue = updateCommand.getMemorandumValue();
        }
    }

    public void delete() {
        if (!this.editable) {
            throw new IllegalStatusException("기본 제공 자산 종류는 삭제할 수 없습니다. 사용 해제만 가능합니다.");
        }
        this.status = Status.DISABLE;
    }
}
