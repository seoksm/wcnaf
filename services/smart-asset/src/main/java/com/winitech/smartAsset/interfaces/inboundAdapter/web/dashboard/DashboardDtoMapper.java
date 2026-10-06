package com.winitech.smartAsset.interfaces.inboundAdapter.web.dashboard;

import com.winitech.smartAsset.domain.dashboard.DashboardSummaryInfo;
import com.winitech.smartAsset.domain.dashboard.DashboardWidgetKey;
import com.winitech.smartAsset.domain.dashboard.WidgetConfigCommand;
import com.winitech.smartAsset.domain.dashboard.WidgetConfigInfo;
import com.winitech.smartAsset.interfaces.inboundAdapter.spec.*;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;
import org.mapstruct.factory.Mappers;

import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * format: date로 선언한 필드도 OpenAPI 생성기가 항상 OffsetDateTime DTO 필드를 만드는
 * 프로젝트 전역 버릇(다른 DtoMapper와 동일) - month/expiryDate 변환에 아래 map() 쌍을 쓴다.
 */
@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface DashboardDtoMapper {

    DashboardDtoMapper INSTANCE = Mappers.getMapper(DashboardDtoMapper.class);

    default OffsetDateTime map(LocalDate value) {
        return value == null ? null : value.atTime(12, 0).atOffset(java.time.ZoneOffset.UTC);
    }

    default LocalDate map(OffsetDateTime value) {
        return value == null ? null : value.toLocalDate();
    }

    DashboardSummaryResponseDto toSummaryResponseDto(DashboardSummaryInfo info);

    DashboardAssetSummaryDto toAssetSummaryDto(DashboardSummaryInfo.AssetSummary info);

    DashboardNameCountDto toNameCountDto(DashboardSummaryInfo.NameCount info);

    DashboardMonthlyCostDto toMonthlyCostDto(DashboardSummaryInfo.MonthlyCost info);

    DashboardExpiringSoonDto toExpiringSoonDto(DashboardSummaryInfo.ExpiringSoon info);

    DashboardExpiringSoonItemDto toExpiringSoonItemDto(DashboardSummaryInfo.ExpiringSoon.Item info);

    DashboardTicketStatusDto toTicketStatusDto(DashboardSummaryInfo.TicketStatus info);

    DashboardLoanStatusDto toLoanStatusDto(DashboardSummaryInfo.LoanStatus info);

    DashboardInventoryProgressDto toInventoryProgressDto(DashboardSummaryInfo.InventoryProgress info);

    DashboardLicenseConsistencyDto toLicenseConsistencyDto(DashboardSummaryInfo.LicenseConsistency info);

    @Mapping(target = "widgetKey", expression = "java(com.winitech.smartAsset.interfaces.inboundAdapter.spec.DashboardWidgetConfigDto.WidgetKeyEnum.valueOf(info.getWidgetKey().name()))")
    DashboardWidgetConfigDto toWidgetConfigDto(WidgetConfigInfo info);

    default List<DashboardWidgetConfigDto> toWidgetConfigDtoList(List<WidgetConfigInfo> infos) {
        return infos.stream().map(this::toWidgetConfigDto).collect(Collectors.toList());
    }

    default WidgetConfigCommand toWidgetConfigCommand(DashboardWidgetConfigDto dto) {
        WidgetConfigCommand command = new WidgetConfigCommand();
        command.setWidgetKey(DashboardWidgetKey.valueOf(dto.getWidgetKey().name()));
        command.setVisible(dto.getVisible());
        command.setSortOrder(dto.getSortOrder());
        return command;
    }

    default List<WidgetConfigCommand> toWidgetConfigCommandList(List<DashboardWidgetConfigDto> dtos) {
        return dtos.stream().map(this::toWidgetConfigCommand).collect(Collectors.toList());
    }
}
