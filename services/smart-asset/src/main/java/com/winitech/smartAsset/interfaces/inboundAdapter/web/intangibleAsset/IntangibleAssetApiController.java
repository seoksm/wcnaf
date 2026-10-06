package com.winitech.smartAsset.interfaces.inboundAdapter.web.intangibleAsset;

import com.winitech.common.annotation.ForceDefaultTenant;
import com.winitech.common.response.CommonResponse;
import com.winitech.smartAsset.application.intangibleAsset.IntangibleAssetFacade;
import com.winitech.smartAsset.domain.intangibleAsset.IntangibleAssetCommand;
import com.winitech.smartAsset.domain.intangibleAsset.IntangibleAssetInfo;
import com.winitech.smartAsset.interfaces.inboundAdapter.spec.*;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@RestController
@ForceDefaultTenant
@RequiredArgsConstructor
public class IntangibleAssetApiController implements IntangibleAssetApi {

    private final IntangibleAssetFacade intangibleAssetFacade;

    @Override
    public CommonResponse<IntangibleAssetIdResponseDto> registerIntangibleAsset(IntangibleAssetRegisterRequestDto dto) {
        IntangibleAssetCommand command = IntangibleAssetDtoMapper.INSTANCE.toRegisterRequestCommand(dto);
        UUID id = intangibleAssetFacade.postIntangibleAsset(command);
        return CommonResponse.success(IntangibleAssetIdResponseDto.builder().intangibleAssetId(id).build());
    }

    @Override
    public CommonResponse<IntangibleAssetIdResponseDto> modifyIntangibleAsset(UUID intangibleAssetId, IntangibleAssetModifyRequestDto dto) {
        IntangibleAssetCommand.UpdateCommand updateCommand = IntangibleAssetDtoMapper.INSTANCE.toModifyRequestCommand(dto);
        updateCommand.setIntangibleAssetId(intangibleAssetId);
        intangibleAssetFacade.reviseIntangibleAsset(updateCommand);
        return CommonResponse.success(IntangibleAssetIdResponseDto.builder().intangibleAssetId(intangibleAssetId).build());
    }

    @Override
    public CommonResponse<String> removeIntangibleAsset(UUID intangibleAssetId) {
        intangibleAssetFacade.removeIntangibleAsset(intangibleAssetId);
        return CommonResponse.success("OK");
    }

    @Override
    public CommonResponse<IntangibleAssetPageResponseDto> searchAllIntangibleAsset(String keyword, Integer withinDays, Integer page, Integer size) {
        Page<IntangibleAssetInfo> assetPage = intangibleAssetFacade.getList(keyword, withinDays, page, size);

        List<IntangibleAssetResponseDto> content = assetPage.getContent().stream()
                .map(IntangibleAssetDtoMapper.INSTANCE::toIntangibleAssetResponseDto)
                .collect(Collectors.toList());

        IntangibleAssetPageResponseDto res = IntangibleAssetPageResponseDto.builder()
                .content(content)
                .currentPage(assetPage.getNumber())
                .pageSize(assetPage.getSize())
                .totalElements(assetPage.getTotalElements())
                .totalPages(assetPage.getTotalPages())
                .build();
        return CommonResponse.success(res);
    }

    @Override
    public CommonResponse<IntangibleAssetResponseDto> searchIntangibleAsset(UUID intangibleAssetId) {
        return CommonResponse.success(IntangibleAssetDtoMapper.INSTANCE.toIntangibleAssetResponseDto(
                intangibleAssetFacade.getIntangibleAsset(intangibleAssetId)));
    }

    @Override
    public CommonResponse<List<IntangibleAssetActionLogResponseDto>> searchIntangibleAssetActionLog(UUID intangibleAssetId) {
        List<IntangibleAssetActionLogResponseDto> res = intangibleAssetFacade.getActionLog(intangibleAssetId).stream()
                .map(IntangibleAssetDtoMapper.INSTANCE::toActionLogResponseDto)
                .collect(Collectors.toList());
        return CommonResponse.success(res);
    }

    @Override
    public CommonResponse<String> renewIntangibleAsset(UUID intangibleAssetId, IntangibleAssetRenewRequestDto dto) {
        intangibleAssetFacade.renew(intangibleAssetId, dto.getNewExpiryDate().toLocalDate(), dto.getNote());
        return CommonResponse.success("OK");
    }
}
