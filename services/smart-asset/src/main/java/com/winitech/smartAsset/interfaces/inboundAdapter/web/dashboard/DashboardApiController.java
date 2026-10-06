package com.winitech.smartAsset.interfaces.inboundAdapter.web.dashboard;

import com.winitech.common.annotation.ForceDefaultTenant;
import com.winitech.common.response.CommonResponse;
import com.winitech.smartAsset.application.dashboard.DashboardFacade;
import com.winitech.smartAsset.interfaces.inboundAdapter.spec.*;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@ForceDefaultTenant
@RequiredArgsConstructor
public class DashboardApiController implements DashboardApi {

    private final DashboardFacade dashboardFacade;

    @Override
    public CommonResponse<DashboardSummaryResponseDto> searchDashboardSummary() {
        return CommonResponse.success(DashboardDtoMapper.INSTANCE.toSummaryResponseDto(dashboardFacade.getSummary()));
    }

    @Override
    public CommonResponse<List<DashboardWidgetConfigDto>> searchDashboardWidgetConfig() {
        return CommonResponse.success(DashboardDtoMapper.INSTANCE.toWidgetConfigDtoList(dashboardFacade.getWidgetConfig()));
    }

    @Override
    public CommonResponse<String> modifyDashboardWidgetConfig(DashboardWidgetConfigListRequestDto dto) {
        dashboardFacade.putWidgetConfig(DashboardDtoMapper.INSTANCE.toWidgetConfigCommandList(dto.getConfigs()));
        return CommonResponse.success("OK");
    }
}
