package com.winitech.smartAsset.infrastructure.dashboard;

import com.winitech.smartAsset.domain.dashboard.DashboardWidgetConfig;
import com.winitech.smartAsset.domain.dashboard.DashboardWidgetConfigStore;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Repository
@RequiredArgsConstructor
public class DashboardWidgetConfigStoreImpl implements DashboardWidgetConfigStore {

    private final DashboardWidgetConfigRepository dashboardWidgetConfigRepository;

    @Override
    @Transactional
    public void replaceAll(UUID memberId, List<DashboardWidgetConfig> configs) {
        dashboardWidgetConfigRepository.deleteAllByMemberId(memberId);
        dashboardWidgetConfigRepository.saveAll(configs);
    }
}
