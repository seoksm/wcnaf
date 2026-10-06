package com.winitech.smartAsset.domain.dashboard;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class WidgetConfigCommand {
    private DashboardWidgetKey widgetKey;
    private Boolean visible;
    private Integer sortOrder;
}
