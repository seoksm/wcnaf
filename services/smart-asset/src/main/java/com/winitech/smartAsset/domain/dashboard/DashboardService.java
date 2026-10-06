package com.winitech.smartAsset.domain.dashboard;

import java.util.List;

public interface DashboardService {

    /** S-700 - 위젯 10종 데이터 전체를 계산해 돌려준다(캐시 없음, 매 요청 최신값). */
    DashboardSummaryInfo loadSummary();

    /** S-701 - 로그인 사용자의 위젯 설정. 저장된 행이 없으면 전체 기본값(표시함)으로 채워 돌려준다. */
    List<WidgetConfigInfo> loadWidgetConfig();

    /** S-701 저장 - 항상 전체 목록을 받아 통째로 교체한다. */
    void saveWidgetConfig(List<WidgetConfigCommand> commands);
}
