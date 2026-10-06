package com.winitech.smartAsset.domain.assetCategory;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.math.BigDecimal;
import java.util.UUID;

@Getter
@Builder
@ToString
public class AssetCategoryCommand {

    private String categoryCode;
    private String categoryName;
    private Integer sortSeq;
    private Integer usefulLifeMonths;
    private BigDecimal residualRate;
    private BigDecimal memorandumValue;

    public AssetCategory toEntity() {
        return AssetCategory.builder()
                .categoryCode(categoryCode)
                .categoryName(categoryName)
                .sortSeq(sortSeq)
                .usefulLifeMonths(usefulLifeMonths)
                .residualRate(residualRate)
                .memorandumValue(memorandumValue)
                .build();
    }

    @Getter
    @Builder
    public static class UpdateCommand {
        @Setter
        private UUID categoryId;
        private String categoryName;
        private Integer sortSeq;
        private Integer usefulLifeMonths;
        private BigDecimal residualRate;
        private BigDecimal memorandumValue;
    }
}
