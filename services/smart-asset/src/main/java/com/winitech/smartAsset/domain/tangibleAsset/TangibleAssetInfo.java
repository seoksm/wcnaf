package com.winitech.smartAsset.domain.tangibleAsset;

import lombok.Getter;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

@Getter
public class TangibleAssetInfo {

    private final UUID tangibleAssetId;
    private final String assetCode;
    private final String assetName;
    private final UUID categoryId;
    private final String categoryName;
    private final UUID locationId;
    private final String locationName;
    private final TangibleAsset.LifeStatus lifeStatus;
    private final TangibleAsset.AssignType assignType;
    private final LocalDate acquisitionDate;
    private final BigDecimal acquisitionAmount;
    private final String modelName;
    private final String manufacturer;
    private final String serialNo;
    private final UUID currentMemberId;
    private final String memo;
    private final TangibleAsset.Status status;

    public TangibleAssetInfo(TangibleAsset tangibleAsset) {
        this.tangibleAssetId = tangibleAsset.getId();
        this.assetCode = tangibleAsset.getAssetCode();
        this.assetName = tangibleAsset.getAssetName();
        this.categoryId = tangibleAsset.getCategory() == null ? null : tangibleAsset.getCategory().getId();
        this.categoryName = tangibleAsset.getCategory() == null ? null : tangibleAsset.getCategory().getCategoryName();
        this.locationId = tangibleAsset.getLocation() == null ? null : tangibleAsset.getLocation().getId();
        this.locationName = tangibleAsset.getLocation() == null ? null : tangibleAsset.getLocation().getLocationName();
        this.lifeStatus = tangibleAsset.getLifeStatus();
        this.assignType = tangibleAsset.getAssignType();
        this.acquisitionDate = tangibleAsset.getAcquisitionDate();
        this.acquisitionAmount = tangibleAsset.getAcquisitionAmount();
        this.modelName = tangibleAsset.getModelName();
        this.manufacturer = tangibleAsset.getManufacturer();
        this.serialNo = tangibleAsset.getSerialNo();
        this.currentMemberId = tangibleAsset.getCurrentMemberId();
        this.memo = tangibleAsset.getMemo();
        this.status = tangibleAsset.getStatus();
    }
}
