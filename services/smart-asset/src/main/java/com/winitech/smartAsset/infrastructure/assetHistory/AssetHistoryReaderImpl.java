package com.winitech.smartAsset.infrastructure.assetHistory;

import com.winitech.smartAsset.domain.assetHistory.AssetHistory;
import com.winitech.smartAsset.domain.assetHistory.AssetHistoryFilter;
import com.winitech.smartAsset.domain.assetHistory.AssetHistoryReader;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Repository;

import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.List;
import java.util.UUID;

@Repository
@RequiredArgsConstructor
public class AssetHistoryReaderImpl implements AssetHistoryReader {

    /** 한 번에 조회하는 건수 - 스크롤다운 시 page를 늘려 다음 200건을 이어서 불러온다 */
    private static final int PAGE_SIZE = 200;

    /** fromDate/toDate 미지정 시 "필터 없음"을 의미하는 안전한 전체 범위 (Postgres 표현 범위 내) */
    private static final OffsetDateTime MIN_DATE = OffsetDateTime.of(1970, 1, 1, 0, 0, 0, 0, ZoneOffset.UTC);
    private static final OffsetDateTime MAX_DATE = OffsetDateTime.of(2999, 12, 31, 23, 59, 59, 0, ZoneOffset.UTC);

    private final AssetHistoryRepository assetHistoryRepository;

    @Override
    public List<AssetHistory> findAllByTangibleAssetId(UUID tangibleAssetId) {
        return assetHistoryRepository.findAllByTangibleAssetId(tangibleAssetId);
    }

    @Override
    public List<AssetHistory> findRecent(AssetHistoryFilter filter, int page) {
        int safePage = Math.max(page, 0);

        // assetCode/assetName/fromDate/toDate는 null 바인딩 대신 "전체 매칭" 기본값으로 채운다 (위 @Query 주석 참조)
        String assetCode = filter.getAssetCode() != null ? filter.getAssetCode() : "";
        String assetName = filter.getAssetName() != null ? filter.getAssetName() : "";
        OffsetDateTime fromDate = filter.getFromDate() != null ? filter.getFromDate() : MIN_DATE;
        OffsetDateTime toDate = filter.getToDate() != null ? filter.getToDate() : MAX_DATE;

        return assetHistoryRepository.findRecentByFilter(
                filter.getHistoryType(), assetCode, assetName, fromDate, toDate,
                PageRequest.of(safePage, PAGE_SIZE));
    }
}
