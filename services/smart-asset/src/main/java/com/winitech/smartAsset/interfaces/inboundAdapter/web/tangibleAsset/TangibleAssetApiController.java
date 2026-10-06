package com.winitech.smartAsset.interfaces.inboundAdapter.web.tangibleAsset;

import com.winitech.common.annotation.ForceDefaultTenant;
import com.winitech.common.exception.InvalidParamException;
import com.winitech.common.response.CommonResponse;
import com.winitech.smartAsset.application.tangibleAsset.TangibleAssetFacade;
import com.winitech.smartAsset.domain.assetAssignment.AssetAssignmentInfo;
import com.winitech.smartAsset.domain.assetHistory.AssetHistoryInfo;
import com.winitech.smartAsset.domain.disposalAsset.DisposalAssetRowInfo;
import com.winitech.smartAsset.domain.tangibleAsset.TangibleAssetCommand;
import com.winitech.smartAsset.domain.tangibleAsset.TangibleAssetDisposalCommand;
import com.winitech.smartAsset.domain.tangibleAsset.TangibleAssetInfo;
import com.winitech.smartAsset.interfaces.inboundAdapter.spec.*;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@RestController
@ForceDefaultTenant
@RequiredArgsConstructor
public class TangibleAssetApiController implements TangibleAssetApi {

    private final TangibleAssetFacade tangibleAssetFacade;

    @Override
    public CommonResponse<TangibleAssetIdResponseDto> registerTangibleAsset(TangibleAssetRegisterRequestDto tangibleAssetRegisterRequestDto) {
        TangibleAssetCommand command = TangibleAssetDtoMapper.INSTANCE.toRegisterRequestCommand(tangibleAssetRegisterRequestDto);

        UUID tangibleAssetId = tangibleAssetFacade.postTangibleAsset(command);
        return CommonResponse.success(TangibleAssetIdResponseDto.builder().tangibleAssetId(tangibleAssetId).build());
    }

    @Override
    public CommonResponse<TangibleAssetIdResponseDto> modifyTangibleAsset(UUID tangibleAssetId, TangibleAssetModifyRequestDto tangibleAssetModifyRequestDto) {
        TangibleAssetCommand.UpdateCommand updateCommand = TangibleAssetDtoMapper.INSTANCE.toModifyRequestCommand(tangibleAssetModifyRequestDto);

        updateCommand.setTangibleAssetId(tangibleAssetId);

        tangibleAssetFacade.reviseTangibleAsset(updateCommand);
        return CommonResponse.success(TangibleAssetIdResponseDto.builder().tangibleAssetId(tangibleAssetId).build());
    }

    @Override
    public CommonResponse<TangibleAssetPageResponseDto> searchAllTangibleAsset(String keyword, Integer page, Integer size, String sort) {
        Page<TangibleAssetInfo> tangibleAssetPage = tangibleAssetFacade.getTangibleAssetList(keyword, page, size, sort);

        List<TangibleAssetResponseDto> content = tangibleAssetPage.getContent().stream()
                .map(TangibleAssetDtoMapper.INSTANCE::toTangibleAssetResponseDto)
                .collect(Collectors.toList());

        TangibleAssetPageResponseDto res = TangibleAssetPageResponseDto.builder()
                .content(content)
                .currentPage(tangibleAssetPage.getNumber())
                .pageSize(tangibleAssetPage.getSize())
                .totalElements(tangibleAssetPage.getTotalElements())
                .totalPages(tangibleAssetPage.getTotalPages())
                .build();

        return CommonResponse.success(res);
    }

    @Override
    public CommonResponse<TangibleAssetResponseDto> searchTangibleAsset(UUID tangibleAssetId) {
        TangibleAssetInfo tangibleAsset = tangibleAssetFacade.getTangibleAsset(tangibleAssetId);

        return CommonResponse.success(TangibleAssetDtoMapper.INSTANCE.toTangibleAssetResponseDto(tangibleAsset));
    }

    @Override
    public CommonResponse<List<AssetAssignmentResponseDto>> searchTangibleAssetAssignmentHistory(UUID tangibleAssetId) {
        List<AssetAssignmentInfo> history = tangibleAssetFacade.getAssignmentHistory(tangibleAssetId);

        List<AssetAssignmentResponseDto> res = history.stream()
                .map(TangibleAssetDtoMapper.INSTANCE::toAssetAssignmentResponseDto)
                .collect(Collectors.toList());

        return CommonResponse.success(res);
    }

    @Override
    public CommonResponse<TangibleAssetIdResponseDto> releaseTangibleAssetAssignment(UUID tangibleAssetId) {
        tangibleAssetFacade.releaseAssignment(tangibleAssetId);

        return CommonResponse.success(TangibleAssetIdResponseDto.builder().tangibleAssetId(tangibleAssetId).build());
    }

    @Override
    public CommonResponse<String> batchModifyTangibleAsset(TangibleAssetBatchModifyRequestDto tangibleAssetBatchModifyRequestDto) {
        TangibleAssetCommand.BatchUpdateCommand batchUpdateCommand =
                TangibleAssetDtoMapper.INSTANCE.toBatchUpdateCommand(tangibleAssetBatchModifyRequestDto);

        tangibleAssetFacade.batchModifyTangibleAsset(batchUpdateCommand);
        return CommonResponse.success("OK");
    }

    @Override
    public CommonResponse<TangibleAssetIdsResponseDto> duplicateTangibleAsset(UUID tangibleAssetId, TangibleAssetDuplicateRequestDto tangibleAssetDuplicateRequestDto) {
        List<UUID> createdIds = tangibleAssetFacade.duplicateTangibleAsset(tangibleAssetId, tangibleAssetDuplicateRequestDto.getCount());

        return CommonResponse.success(TangibleAssetIdsResponseDto.builder().tangibleAssetIds(createdIds).build());
    }

    @Override
    public CommonResponse<List<AssetHistoryResponseDto>> searchTangibleAssetHistory(UUID tangibleAssetId) {
        List<AssetHistoryInfo> history = tangibleAssetFacade.getHistory(tangibleAssetId);

        List<AssetHistoryResponseDto> res = history.stream()
                .map(TangibleAssetDtoMapper.INSTANCE::toAssetHistoryResponseDto)
                .collect(Collectors.toList());

        return CommonResponse.success(res);
    }

    @Override
    public CommonResponse<List<AssetHistoryResponseDto>> searchTangibleAssetActivityLog(
            String historyType, String assetCode, String assetName,
            OffsetDateTime fromDate, OffsetDateTime toDate, Integer page) {
        List<AssetHistoryInfo> log = tangibleAssetFacade.getActivityLog(historyType, assetCode, assetName, fromDate, toDate, page);

        List<AssetHistoryResponseDto> res = log.stream()
                .map(TangibleAssetDtoMapper.INSTANCE::toAssetHistoryResponseDto)
                .collect(Collectors.toList());

        return CommonResponse.success(res);
    }

    @Override
    public CommonResponse<TangibleAssetIdResponseDto> disuseTangibleAsset(UUID tangibleAssetId, TangibleAssetDisuseRequestDto tangibleAssetDisuseRequestDto) {
        String reason = tangibleAssetDisuseRequestDto == null ? null : tangibleAssetDisuseRequestDto.getReason();
        tangibleAssetFacade.disuseTangibleAsset(tangibleAssetId, reason);

        return CommonResponse.success(TangibleAssetIdResponseDto.builder().tangibleAssetId(tangibleAssetId).build());
    }

    @Override
    public CommonResponse<TangibleAssetIdResponseDto> restoreTangibleAsset(UUID tangibleAssetId) {
        tangibleAssetFacade.restoreTangibleAsset(tangibleAssetId);

        return CommonResponse.success(TangibleAssetIdResponseDto.builder().tangibleAssetId(tangibleAssetId).build());
    }

    @Override
    public CommonResponse<TangibleAssetIdResponseDto> disposeTangibleAsset(UUID tangibleAssetId, TangibleAssetDisposeRequestDto tangibleAssetDisposeRequestDto) {
        if (tangibleAssetDisposeRequestDto == null || tangibleAssetDisposeRequestDto.getDisposalReasonCode() == null) {
            throw new InvalidParamException("처분 사유는 필수입니다.");
        }
        TangibleAssetDisposalCommand command = TangibleAssetDtoMapper.INSTANCE.toDisposalCommand(tangibleAssetDisposeRequestDto);

        tangibleAssetFacade.disposeTangibleAsset(tangibleAssetId, command);
        return CommonResponse.success(TangibleAssetIdResponseDto.builder().tangibleAssetId(tangibleAssetId).build());
    }

    @Override
    public CommonResponse<DisposalAssetPageResponseDto> searchDisposalAssetList(String lifeStatus, Integer page, Integer size, String sort) {
        Page<DisposalAssetRowInfo> disposalPage = tangibleAssetFacade.getDisposalList(lifeStatus, page, size, sort);

        List<DisposalAssetRowResponseDto> content = disposalPage.getContent().stream()
                .map(TangibleAssetDtoMapper.INSTANCE::toDisposalAssetRowResponseDto)
                .collect(Collectors.toList());

        DisposalAssetPageResponseDto res = DisposalAssetPageResponseDto.builder()
                .content(content)
                .currentPage(disposalPage.getNumber())
                .pageSize(disposalPage.getSize())
                .totalElements(disposalPage.getTotalElements())
                .totalPages(disposalPage.getTotalPages())
                .build();

        return CommonResponse.success(res);
    }
}
