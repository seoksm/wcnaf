package com.winitech.smartAsset.domain.tangibleAsset;

import java.util.Optional;
import java.util.UUID;

/**
 * S-213 엑셀 확정의 행(row) 단위 멱등성 저장소 포트. commitId + rowKey(행번호+서명 해시)로 각 행의
 * 처리 결과를 독립적으로 기록해, 같은 commitId를 재시도해도 이미 끝난 행(성공이든 실패든)은 다시
 * 반영하지 않고 그 결과를 그대로 재사용할 수 있게 한다.
 */
public interface ExcelCommitRowStore {

    /** 이미 COMPLETED 또는 FAILED로 끝난 행이면 그 결과를, 아직 끝나지 않았으면 empty를 반환한다 */
    Optional<ExcelCommitRowResult> findFinished(UUID commitId, String rowKey);

    /**
     * 이 행 처리를 시작할 수 있는지 원자적으로 판단한다 - 처음 보는 행이거나, 이전 시도가 죽어
     * IN_PROGRESS로 남아 있는 행이면 true(이번 호출자가 처리), 이미 COMPLETED/FAILED로 끝난
     * 행이면 false(재처리하지 않음 - 호출자는 findFinished로 그 결과를 가져와야 한다).
     */
    boolean claimRow(UUID commitId, String rowKey, int rowNum);

    void completeRow(UUID commitId, String rowKey, ExcelCommitRowResult result);

    void failRow(UUID commitId, String rowKey, int rowNum, String errorMessage);
}
