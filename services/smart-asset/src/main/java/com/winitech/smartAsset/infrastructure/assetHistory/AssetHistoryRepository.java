package com.winitech.smartAsset.infrastructure.assetHistory;

import com.winitech.smartAsset.domain.assetHistory.AssetHistory;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

public interface AssetHistoryRepository extends JpaRepository<AssetHistory, UUID> {

    @Query("select h from AssetHistory h " +
            "where h.tangibleAsset.id = :tangibleAssetId order by h.createAt desc")
    List<AssetHistory> findAllByTangibleAssetId(UUID tangibleAssetId);

    // PostgreSQL이 "is null"만 있는 위치의 파라미터 타입을 추론하지 못해 could-not-determine-data-type로
    // 실패하는 문제(Hibernate 5 + pgjdbc 조합에서 재현됨)를 피하기 위해, assetCode/assetName/fromDate/toDate는
    // "필터 없음"을 null 바인딩이 아니라 항상 매칭되는 안전한 기본값(빈 문자열 · 아주 넓은 날짜 범위)으로 대체해서
    // Java 쪽(AssetHistoryReaderImpl)에서 채운 뒤 넘긴다. historyType은 이 문제가 재현되지 않아 그대로 둔다.
    @Query("select h from AssetHistory h where " +
            "(:historyType is null or h.historyType = :historyType) and " +
            "h.tangibleAsset.assetCode like concat('%', :assetCode, '%') and " +
            "h.tangibleAsset.assetName like concat('%', :assetName, '%') and " +
            "h.createAt >= :fromDate and h.createAt <= :toDate " +
            "order by h.createAt desc")
    List<AssetHistory> findRecentByFilter(AssetHistory.HistoryType historyType, String assetCode, String assetName,
                                           OffsetDateTime fromDate, OffsetDateTime toDate, Pageable pageable);
}
