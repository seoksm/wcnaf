package com.winitech.smartAsset.interfaces.inboundAdapter.web.assetCategory;

import com.winitech.common.annotation.ForceDefaultTenant;
import com.winitech.common.response.CommonResponse;
import com.winitech.smartAsset.application.assetCategory.AssetCategoryFacade;
import com.winitech.smartAsset.domain.assetCategory.AssetCategoryCommand;
import com.winitech.smartAsset.domain.assetCategory.AssetCategoryInfo;
import com.winitech.smartAsset.interfaces.inboundAdapter.spec.*;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@RestController
@ForceDefaultTenant
@RequiredArgsConstructor
public class AssetCategoryApiController implements AssetCategoryApi {

    private final AssetCategoryFacade assetCategoryFacade;

    @Override
    public CommonResponse<AssetCategoryIdResponseDto> registerAssetCategory(AssetCategoryRegisterRequestDto assetCategoryRegisterRequestDto) {
        AssetCategoryCommand command = AssetCategoryDtoMapper.INSTANCE.toRegisterRequestCommand(assetCategoryRegisterRequestDto);

        UUID categoryId = assetCategoryFacade.postCategory(command);
        return CommonResponse.success(AssetCategoryIdResponseDto.builder().categoryId(categoryId).build());
    }

    @Override
    public CommonResponse<AssetCategoryIdResponseDto> modifyAssetCategory(UUID categoryId, AssetCategoryModifyRequestDto assetCategoryModifyRequestDto) {
        AssetCategoryCommand.UpdateCommand updateCommand = AssetCategoryDtoMapper.INSTANCE.toModifyRequestCommand(assetCategoryModifyRequestDto);

        updateCommand.setCategoryId(categoryId);

        assetCategoryFacade.reviseCategory(updateCommand);
        return CommonResponse.success(AssetCategoryIdResponseDto.builder().categoryId(categoryId).build());
    }

    @Override
    public CommonResponse<String> removeAssetCategory(UUID categoryId) {
        assetCategoryFacade.removeCategory(categoryId);

        return CommonResponse.success("OK");
    }

    @Override
    public CommonResponse<List<AssetCategoryResponseDto>> searchAllAssetCategory(String keyword) {
        List<AssetCategoryInfo> categoryList = assetCategoryFacade.getCategoryList(keyword);

        List<AssetCategoryResponseDto> res = categoryList.stream()
                .map(AssetCategoryDtoMapper.INSTANCE::toAssetCategoryResponseDto)
                .collect(Collectors.toList());

        return CommonResponse.success(res);
    }

    @Override
    public CommonResponse<AssetCategoryResponseDto> searchAssetCategory(UUID categoryId) {
        AssetCategoryInfo category = assetCategoryFacade.getCategory(categoryId);

        return CommonResponse.success(AssetCategoryDtoMapper.INSTANCE.toAssetCategoryResponseDto(category));
    }
}
