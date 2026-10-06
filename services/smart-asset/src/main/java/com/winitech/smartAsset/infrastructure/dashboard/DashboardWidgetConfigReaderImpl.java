package com.winitech.smartAsset.infrastructure.dashboard;

import com.winitech.smartAsset.domain.dashboard.DashboardWidgetConfig;
import com.winitech.smartAsset.domain.dashboard.DashboardWidgetConfigReader;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
@RequiredArgsConstructor
public class DashboardWidgetConfigReaderImpl implements DashboardWidgetConfigReader {

    private final DashboardWidgetConfigRepository dashboardWidgetConfigRepository;

    @Override
    public List<DashboardWidgetConfig> findAllByMemberId(UUID memberId) {
        return dashboardWidgetConfigRepository.findAllByMemberId(memberId);
    }
}
