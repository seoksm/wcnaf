package com.winitech.smartAsset.domain.tangibleAsset;

import com.winitech.smartAsset.domain.assetCategory.AssetCategory;
import com.winitech.smartAsset.domain.assetLocation.AssetLocation;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Getter
@Builder
@ToString
public class TangibleAssetCommand {

    private String assetName;
    private UUID categoryId;
    private UUID locationId;
    private TangibleAsset.LifeStatus lifeStatus;
    private TangibleAsset.AssignType assignType;
    private LocalDate acquisitionDate;
    private BigDecimal acquisitionAmount;
    private String modelName;
    private String manufacturer;
    private String serialNo;
    private UUID currentMemberId;
    private String memo;

    public TangibleAsset toEntity(String assetCode, AssetCategory category, AssetLocation location) {
        return TangibleAsset.builder()
                .assetCode(assetCode)
                .assetName(assetName)
                .category(category)
                .location(location)
                .lifeStatus(lifeStatus)
                .assignType(assignType)
                .acquisitionDate(acquisitionDate)
                .acquisitionAmount(acquisitionAmount)
                .modelName(modelName)
                .manufacturer(manufacturer)
                .serialNo(serialNo)
                .currentMemberId(currentMemberId)
                .memo(memo)
                .build();
    }

    @Getter
    @Builder
    public static class UpdateCommand {
        @Setter
        private UUID tangibleAssetId;
        private String assetName;
        private UUID categoryId;
        private UUID locationId;
        private TangibleAsset.LifeStatus lifeStatus;
        private TangibleAsset.AssignType assignType;
        private LocalDate acquisitionDate;
        private BigDecimal acquisitionAmount;
        private String modelName;
        private String manufacturer;
        private String serialNo;
        private UUID currentMemberId;
        private String memo;
        /** 이 값을 읽은 시점(예: 엑셀 preview) 이후 자산이 바뀌지 않았을 것으로 기대하는 버전.
         * null이면 검사하지 않는다(예: 일반 화면 수정/일괄 변경). 값이 있는데 현재 버전과 다르면
         * ObjectOptimisticLockingFailureException을 던져 stale 반영을 막는다. */
        private Long expectedVersion;
    }

    /** S-215: 지정된 필드만 일괄 적용 (null인 필드는 각 자산의 기존 값 유지) */
    @Getter
    @Builder
    public static class BatchUpdateCommand {
        private List<UUID> tangibleAssetIds;
        private UUID categoryId;
        private UUID locationId;
        private TangibleAsset.LifeStatus lifeStatus;
        private TangibleAsset.AssignType assignType;
        private UUID currentMemberId;
    }
}
