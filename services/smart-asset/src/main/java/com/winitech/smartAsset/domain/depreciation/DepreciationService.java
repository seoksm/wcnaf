package com.winitech.smartAsset.domain.depreciation;

import java.util.List;
import java.util.UUID;

public interface DepreciationService {

    /** S-230: 특정 회계연도·분기의 자산별 상각 현황 + 총액법 요약 */
    DepreciationStatusInfo loadStatus(int fiscalYear, DepreciationQuarter quarter);

    /** S-231: 자산 1건의 해당 연도 4개 분기 상각 스케줄 */
    List<DepreciationScheduleRowInfo> loadSchedule(UUID tangibleAssetId, int fiscalYear);

    /** S-232: 결산 확정 - 그 시점 산출 결과를 스냅샷으로 고정 */
    void confirm(int fiscalYear, DepreciationQuarter quarter);

    /** S-232: 확정 해제 - 스냅샷을 삭제하고 다시 실시간 산출로 되돌린다 */
    void release(int fiscalYear, DepreciationQuarter quarter, String reason);

    /** S-232: 확정/해제 이력 (최신순) */
    List<DepreciationConfirmationInfo> loadConfirmationLog();
}
