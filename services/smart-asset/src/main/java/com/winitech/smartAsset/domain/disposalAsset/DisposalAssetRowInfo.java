package com.winitech.smartAsset.domain.disposalAsset;

import com.winitech.smartAsset.domain.assetCategory.AssetCategory;
import com.winitech.smartAsset.domain.tangibleAsset.TangibleAsset;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.UUID;

/** S-240 불용자산 목록의 자산 1행 - 불용(진행 중) 또는 처분완료(확정) 자산을 함께 보여준다 */
@Getter
public class DisposalAssetRowInfo {

    private final UUID tangibleAssetId;
    private final String assetCode;
    private final String assetName;
    private final String categoryName;
    private final TangibleAsset.LifeStatus lifeStatus;
    private final OffsetDateTime lifeStatusChangedAt;
    private final BigDecimal acquisitionAmount;
    /** 불용: 동결 시점까지 계산한 현재 장부가. 처분완료: disposal_asset에 고정된 장부가 */
    private final BigDecimal bookValue;

    /** 아래는 처분완료(DISPOSED)인 경우에만 값이 있다 */
    private final DisposalAsset.DisposalReason disposalReasonCode;
    private final BigDecimal disposalAmount;
    private final String counterparty;
    private final BigDecimal disposalGainLoss;
    private final String disposalMemo;
    private final OffsetDateTime disposedAt;

    public DisposalAssetRowInfo(TangibleAsset asset, AssetCategory category, DisposalAsset disposalAsset, BigDecimal bookValue) {
        this.tangibleAssetId = asset.getId();
        this.assetCode = asset.getAssetCode();
        this.assetName = asset.getAssetName();
        this.categoryName = category != null ? category.getCategoryName() : null;
        this.lifeStatus = asset.getLifeStatus();
        this.lifeStatusChangedAt = asset.getLifeStatusChangedAt();
        this.acquisitionAmount = asset.getAcquisitionAmount();
        this.bookValue = bookValue;

        if (disposalAsset != null) {
            this.disposalReasonCode = disposalAsset.getDisposalReasonCode();
            this.disposalAmount = disposalAsset.getDisposalAmount();
            this.counterparty = disposalAsset.getCounterparty();
            this.disposalGainLoss = disposalAsset.disposalGainLoss();
            this.disposalMemo = disposalAsset.getMemo();
            this.disposedAt = disposalAsset.getCreateAt();
        } else {
            this.disposalReasonCode = null;
            this.disposalAmount = null;
            this.counterparty = null;
            this.disposalGainLoss = null;
            this.disposalMemo = null;
            this.disposedAt = null;
        }
    }
}
