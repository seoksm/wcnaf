package com.winitech.smartAsset.domain.tangibleAsset;

import com.winitech.smartAsset.domain.disposalAsset.DisposalAsset;
import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;

/** S-242 처분 처리 요청 */
@Getter
@Builder
public class TangibleAssetDisposalCommand {
    private DisposalAsset.DisposalReason disposalReasonCode;
    private BigDecimal disposalAmount;
    private String counterparty;
    private String memo;
}
