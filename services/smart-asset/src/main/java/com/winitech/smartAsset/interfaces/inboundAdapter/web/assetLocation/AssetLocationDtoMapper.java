package com.winitech.smartAsset.interfaces.inboundAdapter.web.assetLocation;

import com.winitech.smartAsset.domain.assetLocation.AssetLocationCommand;
import com.winitech.smartAsset.domain.assetLocation.AssetLocationInfo;
import com.winitech.smartAsset.interfaces.inboundAdapter.spec.*;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;
import org.mapstruct.factory.Mappers;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface AssetLocationDtoMapper {
    AssetLocationDtoMapper INSTANCE = Mappers.getMapper(AssetLocationDtoMapper.class);

    AssetLocationResponseDto toAssetLocationResponseDto(AssetLocationInfo assetLocationInfo);

    AssetLocationCommand toRegisterRequestCommand(AssetLocationRegisterRequestDto assetLocationRegisterRequestDto);

    AssetLocationCommand.UpdateCommand toModifyRequestCommand(AssetLocationModifyRequestDto assetLocationModifyRequestDto);
}
