package com.winitech.smartAsset.domain.tangibleAsset;

import com.winitech.smartAsset.domain.assetAssignment.AssetAssignmentInfo;
import com.winitech.smartAsset.domain.assetHistory.AssetHistoryFilter;
import com.winitech.smartAsset.domain.assetHistory.AssetHistoryInfo;
import com.winitech.smartAsset.domain.disposalAsset.DisposalAssetRowInfo;
import org.springframework.data.domain.Page;

import java.util.List;
import java.util.UUID;

public interface TangibleAssetService {

    UUID createTangibleAsset(TangibleAssetCommand command);

    /** batchId를 지정하면 히스토리에 같은 실행 건으로 묶인다 (엑셀 업서트 전용) */
    UUID createTangibleAsset(TangibleAssetCommand command, UUID batchId);

    void updateTangibleAsset(TangibleAssetCommand.UpdateCommand updateCommand);

    void updateTangibleAsset(TangibleAssetCommand.UpdateCommand updateCommand, UUID batchId);

    /**
     * 유형자산 목록 - page는 0부터, size는 서비스 내부에서 기본값/최댓값으로 clamp된다.
     * sort는 "필드,방향"(예: assetName,asc) 형식이며 허용되지 않은 필드는 기본 정렬(createAt desc)로 대체된다.
     */
    Page<TangibleAssetInfo> loadTangibleAssetList(String keyword, Integer page, Integer size, String sort);

    TangibleAssetInfo loadTangibleAsset(UUID tangibleAssetId);

    List<AssetAssignmentInfo> loadAssignmentHistory(UUID tangibleAssetId);

    void releaseAssignment(UUID tangibleAssetId);

    void batchUpdateTangibleAsset(TangibleAssetCommand.BatchUpdateCommand batchUpdateCommand);

    List<UUID> duplicateTangibleAsset(UUID tangibleAssetId, int count);

    /** S-220: 자산 1건의 변경 이력 (최신순) */
    List<AssetHistoryInfo> loadHistory(UUID tangibleAssetId);

    /** S-221: 전체 활동 로그 - filter의 각 필드가 null이면 해당 조건 미적용, page는 0부터(페이지당 200건) */
    List<AssetHistoryInfo> loadActivityLog(AssetHistoryFilter filter, int page);

    /** S-241: 불용 처리 - 사용/보관/수리중에서만 가능, 배정 강제 해제(A1). reason은 이력에만 남기고 자산 메모는 건드리지 않는다 */
    void disuseTangibleAsset(UUID tangibleAssetId, String reason);

    /** S-240: 불용 → 사용 복귀 (Q-28 - 처분대기(불용) 상태만 복귀 허용) */
    void restoreTangibleAsset(UUID tangibleAssetId);

    /** S-242: 처분 처리 - 불용 상태에서만 가능하고, 이후로는 되돌릴 수 없다(Q-28). disposal_asset ID를 반환한다 */
    UUID disposeTangibleAsset(UUID tangibleAssetId, TangibleAssetDisposalCommand command);

    /**
     * S-240: 불용자산 목록 - lifeStatusFilter가 null이면 불용+처분완료 전체를 대상으로 한다.
     * page/size/sort는 loadTangibleAssetList와 동일한 규칙으로 clamp된다.
     */
    Page<DisposalAssetRowInfo> loadDisposalList(TangibleAsset.LifeStatus lifeStatusFilter, Integer page, Integer size, String sort);
}
