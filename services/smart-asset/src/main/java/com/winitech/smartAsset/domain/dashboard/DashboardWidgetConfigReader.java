package com.winitech.smartAsset.domain.dashboard;

import java.util.List;
import java.util.UUID;

public interface DashboardWidgetConfigReader {

    List<DashboardWidgetConfig> findAllByMemberId(UUID memberId);
}
