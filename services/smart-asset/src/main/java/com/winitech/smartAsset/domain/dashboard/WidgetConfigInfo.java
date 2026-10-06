package com.winitech.smartAsset.domain.dashboard;

import lombok.Getter;

/** S-701 응답 1행 - 설정 행이 없는 위젯은 기본값(표시함 + enum 선언 순서)으로 채워 돌려준다. */
@Getter
public class WidgetConfigInfo {

    private final DashboardWidgetKey widgetKey;
    private final boolean visible;
    private final int sortOrder;

    public WidgetConfigInfo(DashboardWidgetKey widgetKey, boolean visible, int sortOrder) {
        this.widgetKey = widgetKey;
        this.visible = visible;
        this.sortOrder = sortOrder;
    }
}
