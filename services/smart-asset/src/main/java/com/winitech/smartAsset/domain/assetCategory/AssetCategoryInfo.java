package com.winitech.smartAsset.domain.assetCategory;

import lombok.Getter;

import java.math.BigDecimal;
import java.util.UUID;

@Getter
public class AssetCategoryInfo {

    private final UUID categoryId;
    private final String categoryCode;
    private final String categoryName;
    private final Integer sortSeq;
    private final Boolean editable;
    private final Integer usefulLifeMonths;
    private final BigDecimal residualRate;
    private final BigDecimal memorandumValue;
    private final AssetCategory.Status status;

    public AssetCategoryInfo(AssetCategory assetCategory) {
        this.categoryId = assetCategory.getId();
        this.categoryCode = assetCategory.getCategoryCode();
        this.categoryName = assetCategory.getCategoryName();
        this.sortSeq = assetCategory.getSortSeq();
        this.editable = assetCategory.getEditable();
        this.usefulLifeMonths = assetCategory.getUsefulLifeMonths();
        this.residualRate = assetCategory.getResidualRate();
        this.memorandumValue = assetCategory.getMemorandumValue();
        this.status = assetCategory.getStatus();
    }
}
