package com.winitech.smartAsset.interfaces.inboundAdapter.web.assetCategory;

import com.winitech.smartAsset.domain.assetCategory.AssetCategoryCommand;
import com.winitech.smartAsset.domain.assetCategory.AssetCategoryInfo;
import com.winitech.smartAsset.interfaces.inboundAdapter.spec.*;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;
import org.mapstruct.factory.Mappers;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface AssetCategoryDtoMapper {
    AssetCategoryDtoMapper INSTANCE = Mappers.getMapper(AssetCategoryDtoMapper.class);

    AssetCategoryResponseDto toAssetCategoryResponseDto(AssetCategoryInfo assetCategoryInfo);

    AssetCategoryCommand toRegisterRequestCommand(AssetCategoryRegisterRequestDto assetCategoryRegisterRequestDto);

    AssetCategoryCommand.UpdateCommand toModifyRequestCommand(AssetCategoryModifyRequestDto assetCategoryModifyRequestDto);
}
