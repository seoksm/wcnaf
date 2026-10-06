package com.winitech.smartAsset.application.tangibleAsset;

import com.winitech.smartAsset.domain.tangibleAsset.ExcelCommitRowResult;
import com.winitech.smartAsset.domain.tangibleAsset.ExcelCommitRowStore;
import com.winitech.smartAsset.domain.tangibleAsset.TangibleAssetExcelRow;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HexFormat;
import java.util.UUID;

/**
 * S-213 엑셀 확정의 행(row) 1건을 처리한다. 이 클래스 자체는 트랜잭션을 시작하지 않는다 -
 * 실제 트랜잭션(REQUIRES_NEW)은 별도 스프링 빈인 {@link ExcelCommitRowExecutor}에서 일어난다.
 * <p>
 * 이 클래스가 별도로 존재하는 이유: ExcelCommitRowExecutor.processInNewTransaction()가 예외를
 * 던지면(자산 저장 실패 등) 그 트랜잭션은 이미 롤백 대상으로 표시된 상태다 - 같은 트랜잭션 안에서
 * 실패를 기록하면 그 기록조차 롤백된다. 그래서 실패를 기록하는 recordFailure()는 반드시 "새로운"
 * 트랜잭션에서 별도 호출로 실행해야 하고, 이 조정(orchestration)을 트랜잭션이 없는 이 계층이 맡는다.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class ExcelCommitRowProcessor {

    private final ExcelCommitRowExecutor excelCommitRowExecutor;
    private final ExcelCommitRowStore excelCommitRowStore;

    public ExcelCommitRowResult processRow(UUID commitId, TangibleAssetExcelRow row, UUID batchId) {
        String rowKey = rowKeyOf(row);

        ExcelCommitRowResult finished = excelCommitRowStore.findFinished(commitId, rowKey).orElse(null);
        if (finished != null) {
            return finished;
        }

        try {
            return excelCommitRowExecutor.processInNewTransaction(commitId, rowKey, row, batchId);
        } catch (ObjectOptimisticLockingFailureException e) {
            // Hibernate의 기본 메시지("Object of class [...] with identifier [...]: optimistic
            // locking failed")는 기술적이라 그대로 보여주지 않고, 사용자가 무엇을 해야 하는지
            // 알 수 있는 문구로 바꾼다.
            String friendlyMessage = "다른 곳에서 이미 이 자산을 수정했습니다. 다시 미리보기해주세요.";
            log.warn("엑셀 확정 중 {}행 낙관적 잠금 충돌", row.getRowNum());
            excelCommitRowExecutor.recordFailure(commitId, rowKey, row.getRowNum(), friendlyMessage);
            return ExcelCommitRowResult.failure(friendlyMessage);
        } catch (Exception e) {
            log.warn("엑셀 확정 중 {}행 반영 실패: {}", row.getRowNum(), e.getMessage());
            excelCommitRowExecutor.recordFailure(commitId, rowKey, row.getRowNum(), e.getMessage());
            return ExcelCommitRowResult.failure(e.getMessage());
        }
    }

    /** rowNum만으로는 재요청 시 내용이 바뀐 행을 구분할 수 없으므로, 서명(이미 preview 시점 전체
     * 내용을 반영해 계산됨)의 해시를 함께 써서 "이 행이 정말 그때 그 행인지"까지 식별한다. */
    private String rowKeyOf(TangibleAssetExcelRow row) {
        return row.getRowNum() + "-" + shortHash(row.getSignature());
    }

    private String shortHash(String value) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest((value == null ? "" : value).getBytes(StandardCharsets.UTF_8));
            return HexFormat.of().formatHex(hash, 0, 8);
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException("행 식별자 계산 중 오류가 발생했습니다.", e);
        }
    }
}
