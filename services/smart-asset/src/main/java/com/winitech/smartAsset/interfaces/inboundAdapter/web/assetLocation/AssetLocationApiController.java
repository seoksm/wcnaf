package com.winitech.smartAsset.interfaces.inboundAdapter.web.assetLocation;

import com.winitech.common.annotation.ForceDefaultTenant;
import com.winitech.common.response.CommonResponse;
import com.winitech.smartAsset.application.assetLocation.AssetLocationFacade;
import com.winitech.smartAsset.domain.assetLocation.AssetLocationCommand;
import com.winitech.smartAsset.domain.assetLocation.AssetLocationInfo;
import com.winitech.smartAsset.interfaces.inboundAdapter.spec.*;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@RestController
@ForceDefaultTenant
@RequiredArgsConstructor
public class AssetLocationApiController implements AssetLocationApi {

    private final AssetLocationFacade assetLocationFacade;

    @Override
    public CommonResponse<AssetLocationIdResponseDto> registerAssetLocation(AssetLocationRegisterRequestDto assetLocationRegisterRequestDto) {
        AssetLocationCommand command = AssetLocationDtoMapper.INSTANCE.toRegisterRequestCommand(assetLocationRegisterRequestDto);

        UUID locationId = assetLocationFacade.postLocation(command);
        return CommonResponse.success(AssetLocationIdResponseDto.builder().locationId(locationId).build());
    }

    @Override
    public CommonResponse<AssetLocationIdResponseDto> modifyAssetLocation(UUID locationId, AssetLocationModifyRequestDto assetLocationModifyRequestDto) {
        AssetLocationCommand.UpdateCommand updateCommand = AssetLocationDtoMapper.INSTANCE.toModifyRequestCommand(assetLocationModifyRequestDto);

        updateCommand.setLocationId(locationId);

        assetLocationFacade.reviseLocation(updateCommand);
        return CommonResponse.success(AssetLocationIdResponseDto.builder().locationId(locationId).build());
    }

    @Override
    public CommonResponse<String> removeAssetLocation(UUID locationId) {
        assetLocationFacade.removeLocation(locationId);

        return CommonResponse.success("OK");
    }

    @Override
    public CommonResponse<List<AssetLocationResponseDto>> searchAllAssetLocation(String keyword) {
        List<AssetLocationInfo> locationList = assetLocationFacade.getLocationList(keyword);

        List<AssetLocationResponseDto> res = locationList.stream()
                .map(AssetLocationDtoMapper.INSTANCE::toAssetLocationResponseDto)
                .collect(Collectors.toList());

        return CommonResponse.success(res);
    }

    @Override
    public CommonResponse<AssetLocationResponseDto> searchAssetLocation(UUID locationId) {
        AssetLocationInfo location = assetLocationFacade.getLocation(locationId);

        return CommonResponse.success(AssetLocationDtoMapper.INSTANCE.toAssetLocationResponseDto(location));
    }
}
