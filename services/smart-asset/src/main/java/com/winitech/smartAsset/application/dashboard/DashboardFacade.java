package com.winitech.smartAsset.application.dashboard;

import com.winitech.smartAsset.domain.dashboard.DashboardService;
import com.winitech.smartAsset.domain.dashboard.DashboardSummaryInfo;
import com.winitech.smartAsset.domain.dashboard.WidgetConfigCommand;
import com.winitech.smartAsset.domain.dashboard.WidgetConfigInfo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class DashboardFacade {

    private final DashboardService dashboardService;

    public DashboardSummaryInfo getSummary() {
        return dashboardService.loadSummary();
    }

    public List<WidgetConfigInfo> getWidgetConfig() {
        return dashboardService.loadWidgetConfig();
    }

    public void putWidgetConfig(List<WidgetConfigCommand> commands) {
        dashboardService.saveWidgetConfig(commands);
    }
}
