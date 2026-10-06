package com.winitech.smartAsset.domain.assetHistory;

import lombok.Builder;
import lombok.Getter;

import java.time.OffsetDateTime;

/** S-221 전체 활동 로그 조회 조건 - 이력유형·자산명·자산코드·로그발생시각(기간) */
@Getter
@Builder
public class AssetHistoryFilter {
    private AssetHistory.HistoryType historyType;
    private String assetCode;
    private String assetName;
    private OffsetDateTime fromDate;
    private OffsetDateTime toDate;
}
