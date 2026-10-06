package com.winitech.smartAsset.domain.dashboard;

import java.util.List;
import java.util.UUID;

public interface DashboardWidgetConfigStore {

    /** 이 회원의 기존 설정 전체를 새 목록으로 교체한다(S-701 저장은 항상 전체 목록을 보낸다). */
    void replaceAll(UUID memberId, List<DashboardWidgetConfig> configs);
}
