package com.winitech.smartAsset.interfaces.inboundAdapter.web.inventory;

import com.winitech.common.annotation.ForceDefaultTenant;
import com.winitech.common.exception.InvalidParamException;
import com.winitech.common.response.CommonResponse;
import com.winitech.smartAsset.application.inventory.InventoryFacade;
import com.winitech.smartAsset.domain.inventory.InventoryAdminCreateCommand;
import com.winitech.smartAsset.domain.inventory.InventoryInfo;
import com.winitech.smartAsset.domain.inventory.InventoryMemberCreateCommand;
import com.winitech.smartAsset.domain.inventory.InventoryPreviewInfo;
import com.winitech.smartAsset.domain.inventory.InventoryResult;
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
public class InventoryApiController implements InventoryApi {

    private final InventoryFacade inventoryFacade;

    @Override
    public CommonResponse<InventoryPageResponseDto> searchAllInventory(Integer page, Integer size, String sort) {
        Page<InventoryInfo> inventoryPage = inventoryFacade.getInventoryList(page, size, sort);

        List<InventoryResponseDto> content = inventoryPage.getContent().stream()
                .map(InventoryDtoMapper.INSTANCE::toInventoryResponseDto)
                .collect(Collectors.toList());

        InventoryPageResponseDto res = InventoryPageResponseDto.builder()
                .content(content)
                .currentPage(inventoryPage.getNumber())
                .pageSize(inventoryPage.getSize())
                .totalElements(inventoryPage.getTotalElements())
                .totalPages(inventoryPage.getTotalPages())
                .build();

        return CommonResponse.success(res);
    }

    @Override
    public CommonResponse<InventoryResponseDto> searchInventory(UUID inventoryId) {
        InventoryInfo info = inventoryFacade.getInventory(inventoryId);
        return CommonResponse.success(InventoryDtoMapper.INSTANCE.toInventoryResponseDto(info));
    }

    @Override
    public CommonResponse<InventoryPreviewResponseDto> previewMemberInventory(InventoryMemberPreviewRequestDto dto) {
        List<UUID> excludedMemberIds = dto == null ? List.of() : dto.getExcludedMemberIds();
        InventoryPreviewInfo info = inventoryFacade.previewMemberInventory(excludedMemberIds);
        return CommonResponse.success(InventoryDtoMapper.INSTANCE.toInventoryPreviewResponseDto(info));
    }

    @Override
    public CommonResponse<InventoryIdResponseDto> registerMemberInventory(InventoryMemberCreateRequestDto dto) {
        InventoryMemberCreateCommand command = InventoryDtoMapper.INSTANCE.toMemberCreateCommand(dto);
        UUID inventoryId = inventoryFacade.registerMemberInventory(command);
        return CommonResponse.success(InventoryIdResponseDto.builder().inventoryId(inventoryId).build());
    }

    @Override
    public CommonResponse<InventoryPreviewResponseDto> previewAdminInventory() {
        InventoryPreviewInfo info = inventoryFacade.previewAdminInventory();
        return CommonResponse.success(InventoryDtoMapper.INSTANCE.toInventoryPreviewResponseDto(info));
    }

    @Override
    public CommonResponse<InventoryIdResponseDto> registerAdminInventory(InventoryAdminCreateRequestDto dto) {
        InventoryAdminCreateCommand command = InventoryDtoMapper.INSTANCE.toAdminCreateCommand(dto);
        UUID inventoryId = inventoryFacade.registerAdminInventory(command);
        return CommonResponse.success(InventoryIdResponseDto.builder().inventoryId(inventoryId).build());
    }

    @Override
    public CommonResponse<InventoryProgressResponseDto> searchInventoryProgress(UUID inventoryId) {
        return CommonResponse.success(InventoryDtoMapper.INSTANCE.toInventoryProgressResponseDto(inventoryFacade.getProgress(inventoryId)));
    }

    @Override
    public CommonResponse<List<InventoryResultRowResponseDto>> searchInventoryResults(UUID inventoryId, String status) {
        return CommonResponse.success(
                InventoryDtoMapper.INSTANCE.toInventoryResultRowResponseDtoList(inventoryFacade.getResultRows(inventoryId, status)));
    }

    @Override
    public CommonResponse<String> approveInventoryResult(UUID inventoryTargetId) {
        inventoryFacade.approveResult(inventoryTargetId);
        return CommonResponse.success("OK");
    }

    @Override
    public CommonResponse<String> rejectInventoryResult(UUID inventoryTargetId, InventoryResultRejectRequestDto dto) {
        inventoryFacade.rejectResult(inventoryTargetId, dto == null ? null : dto.getReason());
        return CommonResponse.success("OK");
    }

    @Override
    public CommonResponse<String> adminConfirmInventoryResult(UUID inventoryTargetId) {
        inventoryFacade.adminConfirmResult(inventoryTargetId);
        return CommonResponse.success("OK");
    }

    @Override
    public CommonResponse<String> adminReportInventoryAnomaly(UUID inventoryTargetId, InventoryResultAnomalyRequestDto dto) {
        if (dto == null || dto.getAnomalyType() == null) {
            throw new InvalidParamException("이상 유형은 필수입니다.");
        }
        InventoryResult.AnomalyType anomalyType = InventoryResult.AnomalyType.valueOf(dto.getAnomalyType().name());
        inventoryFacade.adminReportAnomaly(inventoryTargetId, anomalyType, dto.getNote());
        return CommonResponse.success("OK");
    }

    @Override
    public CommonResponse<String> closeInventoryResult(UUID inventoryTargetId, InventoryResultCloseRequestDto dto) {
        if (dto == null || dto.getClosureAction() == null || dto.getClosureReasonCode() == null) {
            throw new InvalidParamException("종결 처리 방법과 사유는 필수입니다.");
        }
        InventoryResult.ClosureAction closureAction = InventoryResult.ClosureAction.valueOf(dto.getClosureAction().name());
        InventoryResult.ClosureReasonCode closureReasonCode = InventoryResult.ClosureReasonCode.valueOf(dto.getClosureReasonCode().name());
        inventoryFacade.closeResult(inventoryTargetId, closureAction, closureReasonCode, dto.getNote());
        return CommonResponse.success("OK");
    }

    @Override
    public CommonResponse<String> closeInventoryResultsBulk(InventoryResultCloseBatchRequestDto dto) {
        if (dto == null || dto.getInventoryTargetIds() == null || dto.getInventoryTargetIds().isEmpty()
                || dto.getClosureAction() == null || dto.getClosureReasonCode() == null) {
            throw new InvalidParamException("종결 처리할 대상과 방법·사유는 필수입니다.");
        }
        InventoryResult.ClosureAction closureAction = InventoryResult.ClosureAction.valueOf(dto.getClosureAction().name());
        InventoryResult.ClosureReasonCode closureReasonCode = InventoryResult.ClosureReasonCode.valueOf(dto.getClosureReasonCode().name());
        inventoryFacade.closeResultsBulk(dto.getInventoryTargetIds(), closureAction, closureReasonCode, dto.getNote());
        return CommonResponse.success("OK");
    }

    @Override
    public CommonResponse<String> closeInventory(UUID inventoryId) {
        inventoryFacade.closeInventory(inventoryId);
        return CommonResponse.success("OK");
    }

    @Override
    public CommonResponse<InventoryReportResponseDto> searchInventoryReport(UUID inventoryId) {
        return CommonResponse.success(InventoryDtoMapper.INSTANCE.toInventoryReportResponseDto(inventoryFacade.getReport(inventoryId)));
    }

    @Override
    public CommonResponse<List<InventoryScheduleResponseDto>> searchInventorySchedules() {
        return CommonResponse.success(InventoryDtoMapper.INSTANCE.toScheduleResponseDtoList(inventoryFacade.getSchedules()));
    }

    @Override
    public CommonResponse<InventoryCloneTemplateResponseDto> searchInventoryCloneTemplate(UUID inventoryId) {
        return CommonResponse.success(InventoryDtoMapper.INSTANCE.toCloneTemplateResponseDto(inventoryFacade.getCloneTemplate(inventoryId)));
    }

    @Override
    public CommonResponse<InventoryMyStatusResponseDto> searchMyInventoryStatus() {
        return CommonResponse.success(InventoryDtoMapper.INSTANCE.toMyStatusResponseDto(inventoryFacade.getMyStatus()));
    }

    @Override
    public CommonResponse<String> selfConfirmInventoryResult(UUID inventoryTargetId) {
        inventoryFacade.selfConfirmResult(inventoryTargetId);
        return CommonResponse.success("OK");
    }

    @Override
    public CommonResponse<String> selfConfirmInventoryResultWithoutScan(UUID inventoryTargetId, InventorySelfConfirmPhotoRequestDto dto) {
        if (dto == null || dto.getPhotoFileId() == null) {
            throw new InvalidParamException("사진 촬영 후 다시 시도해주세요.");
        }
        inventoryFacade.selfConfirmResultWithoutScan(inventoryTargetId, dto.getPhotoFileId(), dto.getCapturedAt(), dto.getUploadedAt());
        return CommonResponse.success("OK");
    }

    @Override
    public CommonResponse<String> selfReportWrongHolder(UUID inventoryTargetId, InventoryResultAnomalyNoteRequestDto dto) {
        inventoryFacade.selfReportWrongHolder(inventoryTargetId, dto == null ? null : dto.getNote());
        return CommonResponse.success("OK");
    }
}
