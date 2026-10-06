package com.winitech.smartAsset.domain.depreciation;

import com.winitech.smartAsset.domain.assetCategory.AssetCategory;
import com.winitech.smartAsset.domain.tangibleAsset.TangibleAsset;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

/** S-230 감가상각 현황 목록의 자산 1행 */
@Getter
public class DepreciationRowInfo {

    private final UUID tangibleAssetId;
    private final String assetCode;
    private final String assetName;
    private final String categoryName;
    private final LocalDate acquisitionDate;
    private final BigDecimal acquisitionAmount;
    private final BigDecimal openingAccumulated;
    private final BigDecimal periodDepreciation;
    private final BigDecimal closingAccumulated;
    private final BigDecimal bookValue;
    private final Integer elapsedMonths;
    private final Integer usefulLifeMonths;
    private final String excludedReason;

    private DepreciationRowInfo(UUID tangibleAssetId, String assetCode, String assetName, String categoryName,
                                 LocalDate acquisitionDate, BigDecimal acquisitionAmount, BigDecimal opening,
                                 BigDecimal period, BigDecimal closing, BigDecimal bookValue, Integer elapsed,
                                 Integer usefulLifeMonths, String excludedReason) {
        this.tangibleAssetId = tangibleAssetId;
        this.assetCode = assetCode;
        this.assetName = assetName;
        this.categoryName = categoryName;
        this.acquisitionDate = acquisitionDate;
        this.acquisitionAmount = acquisitionAmount;
        this.openingAccumulated = opening;
        this.periodDepreciation = period;
        this.closingAccumulated = closing;
        this.bookValue = bookValue;
        this.elapsedMonths = elapsed;
        this.usefulLifeMonths = usefulLifeMonths;
        this.excludedReason = excludedReason;
    }

    /** D5/D6/D4: 상각 제외 자산 - 취득가액을 그대로 장부가로 보여준다 */
    public static DepreciationRowInfo excluded(TangibleAsset asset, AssetCategory category, DepreciationCalculator.ExcludedReason reason) {
        return new DepreciationRowInfo(asset.getId(), asset.getAssetCode(), asset.getAssetName(),
                category != null ? category.getCategoryName() : null, asset.getAcquisitionDate(), asset.getAcquisitionAmount(),
                null, null, null, asset.getAcquisitionAmount(), null,
                category != null ? category.getUsefulLifeMonths() : null, reason.name());
    }

    public static DepreciationRowInfo fromCalculation(TangibleAsset asset, AssetCategory category, DepreciationCalculator.Result result) {
        return new DepreciationRowInfo(asset.getId(), asset.getAssetCode(), asset.getAssetName(),
                category != null ? category.getCategoryName() : null, asset.getAcquisitionDate(), asset.getAcquisitionAmount(),
                result.getOpeningAccumulated(), result.getPeriodDepreciation(), result.getClosingAccumulated(), result.getBookValue(),
                result.getElapsedMonths(), category != null ? category.getUsefulLifeMonths() : null, null);
    }

    /**
     * 확정된 기간의 1행 - 스냅샷에 고정된 값만 사용하고 현재 tangible_asset/asset_category는 절대 다시 읽지
     * 않는다(요구사항 1). tangibleAsset 연관관계는 FK 컬럼에서 채워지는 ID만 쓰므로 지연로딩 초기화가 필요 없다.
     */
    public static DepreciationRowInfo fromSnapshot(DepreciationSnapshot snapshot) {
        return new DepreciationRowInfo(snapshot.getTangibleAsset().getId(), snapshot.getAssetCode(), snapshot.getAssetName(),
                snapshot.getCategoryName(), snapshot.getAcquisitionDate(), snapshot.getAcquisitionAmount(),
                snapshot.getOpeningAccumulated(), snapshot.getPeriodDepreciation(), snapshot.getClosingAccumulated(),
                snapshot.getBookValue(), null, snapshot.getUsefulLifeMonths(),
                snapshot.getExcludedReason() != null ? snapshot.getExcludedReason().name() : null);
    }
}
