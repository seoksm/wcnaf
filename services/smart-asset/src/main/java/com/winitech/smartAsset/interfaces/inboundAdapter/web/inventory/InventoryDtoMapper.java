package com.winitech.smartAsset.interfaces.inboundAdapter.web.inventory;

import com.winitech.smartAsset.domain.inventory.InventoryAdminCreateCommand;
import com.winitech.smartAsset.domain.inventory.InventoryCloneTemplateInfo;
import com.winitech.smartAsset.domain.inventory.InventoryInfo;
import com.winitech.smartAsset.domain.inventory.InventoryMemberCreateCommand;
import com.winitech.smartAsset.domain.inventory.InventoryMyStatusInfo;
import com.winitech.smartAsset.domain.inventory.InventoryPreviewInfo;
import com.winitech.smartAsset.domain.inventory.InventoryProgressInfo;
import com.winitech.smartAsset.domain.inventory.InventoryReportInfo;
import com.winitech.smartAsset.domain.inventory.InventoryResultRowInfo;
import com.winitech.smartAsset.domain.inventory.InventoryScheduleInfo;
import com.winitech.smartAsset.interfaces.inboundAdapter.spec.*;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface InventoryDtoMapper {
    InventoryDtoMapper INSTANCE = Mappers.getMapper(InventoryDtoMapper.class);

    InventoryMemberCreateCommand toMemberCreateCommand(InventoryMemberCreateRequestDto dto);

    InventoryAdminCreateCommand toAdminCreateCommand(InventoryAdminCreateRequestDto dto);

    InventoryResponseDto toInventoryResponseDto(InventoryInfo info);

    InventoryPreviewResponseDto toInventoryPreviewResponseDto(InventoryPreviewInfo info);

    InventoryProgressResponseDto toInventoryProgressResponseDto(InventoryProgressInfo info);

    InventoryParticipantSummaryResponseDto toParticipantSummaryResponseDto(InventoryProgressInfo.ParticipantSummary summary);

    InventoryResultRowResponseDto toInventoryResultRowResponseDto(InventoryResultRowInfo info);

    List<InventoryResultRowResponseDto> toInventoryResultRowResponseDtoList(List<InventoryResultRowInfo> infoList);

    InventoryReportResponseDto toInventoryReportResponseDto(InventoryReportInfo info);

    InventoryClosureBreakdownItemResponseDto toClosureBreakdownItemResponseDto(InventoryReportInfo.ClosureBreakdownItem item);

    InventoryLostItemResponseDto toLostItemResponseDto(InventoryReportInfo.LostItem item);

    InventoryAnomalyBreakdownItemResponseDto toAnomalyBreakdownItemResponseDto(InventoryReportInfo.AnomalyBreakdownItem item);

    InventoryScheduleResponseDto toScheduleResponseDto(InventoryScheduleInfo info);

    List<InventoryScheduleResponseDto> toScheduleResponseDtoList(List<InventoryScheduleInfo> infoList);

    InventoryCloneTemplateResponseDto toCloneTemplateResponseDto(InventoryCloneTemplateInfo info);

    InventoryMyStatusResponseDto toMyStatusResponseDto(InventoryMyStatusInfo info);
}
