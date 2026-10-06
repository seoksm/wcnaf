package com.winitech.smartAsset.application.tangibleAsset;

import com.winitech.common.exception.InvalidParamException;
import com.winitech.smartAsset.domain.assetAssignment.AssetAssignmentInfo;
import com.winitech.smartAsset.domain.assetHistory.AssetHistory;
import com.winitech.smartAsset.domain.assetHistory.AssetHistoryFilter;
import com.winitech.smartAsset.domain.assetHistory.AssetHistoryInfo;
import com.winitech.smartAsset.domain.disposalAsset.DisposalAssetRowInfo;
import com.winitech.smartAsset.domain.tangibleAsset.TangibleAsset;
import com.winitech.smartAsset.domain.tangibleAsset.TangibleAssetCommand;
import com.winitech.smartAsset.domain.tangibleAsset.TangibleAssetDisposalCommand;
import com.winitech.smartAsset.domain.tangibleAsset.TangibleAssetInfo;
import com.winitech.smartAsset.domain.tangibleAsset.TangibleAssetService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;
import java.util.function.Supplier;

@Slf4j
@Service
@RequiredArgsConstructor
public class TangibleAssetFacade {

    private final TangibleAssetService tangibleAssetService;

    public UUID postTangibleAsset(TangibleAssetCommand command) {
        return tangibleAssetService.createTangibleAsset(command);
    }

    public void reviseTangibleAsset(TangibleAssetCommand.UpdateCommand updateCommand) {
        withConflictTranslation(() -> tangibleAssetService.updateTangibleAsset(updateCommand));
    }

    public Page<TangibleAssetInfo> getTangibleAssetList(String keyword, Integer page, Integer size, String sort) {
        return tangibleAssetService.loadTangibleAssetList(keyword, page, size, sort);
    }

    public TangibleAssetInfo getTangibleAsset(UUID tangibleAssetId) {
        return tangibleAssetService.loadTangibleAsset(tangibleAssetId);
    }

    public List<AssetAssignmentInfo> getAssignmentHistory(UUID tangibleAssetId) {
        return tangibleAssetService.loadAssignmentHistory(tangibleAssetId);
    }

    public void releaseAssignment(UUID tangibleAssetId) {
        withConflictTranslation(() -> tangibleAssetService.releaseAssignment(tangibleAssetId));
    }

    public void batchModifyTangibleAsset(TangibleAssetCommand.BatchUpdateCommand batchUpdateCommand) {
        withConflictTranslation(() -> tangibleAssetService.batchUpdateTangibleAsset(batchUpdateCommand));
    }

    public List<UUID> duplicateTangibleAsset(UUID tangibleAssetId, int count) {
        return tangibleAssetService.duplicateTangibleAsset(tangibleAssetId, count);
    }

    public List<AssetHistoryInfo> getHistory(UUID tangibleAssetId) {
        return tangibleAssetService.loadHistory(tangibleAssetId);
    }

    /** S-221: 이력유형·자산명·자산코드·로그발생시각(기간) 조건 + page(0부터, 페이지당 200건) */
    public List<AssetHistoryInfo> getActivityLog(String historyType, String assetCode, String assetName,
                                                  OffsetDateTime fromDate, OffsetDateTime toDate, Integer page) {
        AssetHistory.HistoryType type = StringUtils.hasText(historyType) ? AssetHistory.HistoryType.valueOf(historyType) : null;

        AssetHistoryFilter filter = AssetHistoryFilter.builder()
                .historyType(type)
                .assetCode(StringUtils.hasText(assetCode) ? assetCode : null)
                .assetName(StringUtils.hasText(assetName) ? assetName : null)
                .fromDate(fromDate)
                .toDate(toDate)
                .build();

        return tangibleAssetService.loadActivityLog(filter, page == null ? 0 : page);
    }

    /** S-241: 불용 처리 */
    public void disuseTangibleAsset(UUID tangibleAssetId, String reason) {
        withConflictTranslation(() -> tangibleAssetService.disuseTangibleAsset(tangibleAssetId, reason));
    }

    /** S-240: 불용 → 사용 복귀 */
    public void restoreTangibleAsset(UUID tangibleAssetId) {
        withConflictTranslation(() -> tangibleAssetService.restoreTangibleAsset(tangibleAssetId));
    }

    /** S-242: 처분 처리 - 생성된 disposal_asset ID를 반환한다 */
    public UUID disposeTangibleAsset(UUID tangibleAssetId, TangibleAssetDisposalCommand command) {
        return withConflictTranslation(() -> tangibleAssetService.disposeTangibleAsset(tangibleAssetId, command));
    }

    /** S-240: 불용자산 목록 - lifeStatus가 비어있으면 불용+처분완료 전체 */
    public Page<DisposalAssetRowInfo> getDisposalList(String lifeStatus, Integer page, Integer size, String sort) {
        TangibleAsset.LifeStatus filter = StringUtils.hasText(lifeStatus) ? TangibleAsset.LifeStatus.valueOf(lifeStatus) : null;
        return tangibleAssetService.loadDisposalList(filter, page, size, sort);
    }

    /**
     * 배정 변경/회수는 동일 자산을 동시에 수정하는 다른 요청과 경쟁할 수 있다(Q-16, D8) - 낙관적
     * 잠금(TangibleAsset.version) 충돌은 {@link ObjectOptimisticLockingFailureException}으로,
     * 자산당 활성 배정 1건 제약(유니크 인덱스) 위반은 {@link DataIntegrityViolationException}으로
     * 나타난다. 두 예외 모두 Spring이 트랜잭션 커밋 시점에 던지므로(도메인 서비스 메서드 본문이
     * 아니라 그 메서드를 호출하는 지점, 즉 여기서 잡힌다) 이 파사드 계층에서 한 곳에 모아 사용자가
     * 이해할 수 있는 메시지로 변환한다.
     */
    private void withConflictTranslation(Runnable action) {
        withConflictTranslation(() -> {
            action.run();
            return null;
        });
    }

    private <T> T withConflictTranslation(Supplier<T> action) {
        try {
            return action.get();
        } catch (ObjectOptimisticLockingFailureException | DataIntegrityViolationException e) {
            throw new InvalidParamException("다른 곳에서 이미 이 자산 정보를 변경했습니다. 새로고침 후 다시 시도해주세요.");
        }
    }
}
