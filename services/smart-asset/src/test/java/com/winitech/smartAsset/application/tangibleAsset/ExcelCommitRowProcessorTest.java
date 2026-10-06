package com.winitech.smartAsset.application.tangibleAsset;

import com.winitech.smartAsset.domain.tangibleAsset.ExcelCommitRowResult;
import com.winitech.smartAsset.domain.tangibleAsset.ExcelCommitRowStore;
import com.winitech.smartAsset.domain.tangibleAsset.TangibleAsset;
import com.winitech.smartAsset.domain.tangibleAsset.TangibleAssetExcelRow;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.orm.ObjectOptimisticLockingFailureException;

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * ExcelCommitRowProcessor - 이미 끝난 행 재사용, 낙관적 잠금 충돌의 사용자 메시지 변환(요구사항 A-7),
 * 그 외 실패의 실패 기록 위임을 검증하는 순수 단위 테스트.
 */
@ExtendWith(MockitoExtension.class)
class ExcelCommitRowProcessorTest {

    @Mock private ExcelCommitRowExecutor excelCommitRowExecutor;
    @Mock private ExcelCommitRowStore excelCommitRowStore;

    private ExcelCommitRowProcessor processor;

    @BeforeEach
    void setUp() {
        processor = new ExcelCommitRowProcessor(excelCommitRowExecutor, excelCommitRowStore);
        lenient().when(excelCommitRowStore.findFinished(any(), any())).thenReturn(Optional.empty());
    }

    private TangibleAssetExcelRow row(int rowNum) {
        return TangibleAssetExcelRow.builder().rowNum(rowNum).action("UPDATE").signature("sig").build();
    }

    @Test
    void 이미_끝난_행이면_실행하지_않고_저장된_결과를_반환한다() {
        UUID commitId = UUID.randomUUID();
        TangibleAssetExcelRow row = row(1);
        ExcelCommitRowResult stored = ExcelCommitRowResult.success("UPDATED", UUID.randomUUID());
        when(excelCommitRowStore.findFinished(eq(commitId), any())).thenReturn(Optional.of(stored));

        ExcelCommitRowResult result = processor.processRow(commitId, row, UUID.randomUUID());

        assertThat(result).isEqualTo(stored);
        verify(excelCommitRowExecutor, never()).processInNewTransaction(any(), any(), any(), any());
    }

    @Test
    void 낙관적_잠금_충돌은_사용자가_이해할_수_있는_메시지로_변환된다() {
        UUID commitId = UUID.randomUUID();
        TangibleAssetExcelRow row = row(1);
        UUID assetId = UUID.randomUUID();
        doThrow(new ObjectOptimisticLockingFailureException(TangibleAsset.class, assetId))
                .when(excelCommitRowExecutor).processInNewTransaction(any(), any(), any(), any());

        ExcelCommitRowResult result = processor.processRow(commitId, row, UUID.randomUUID());

        assertThat(result.isSuccess()).isFalse();
        assertThat(result.getErrorMessage())
                .isEqualTo("다른 곳에서 이미 이 자산을 수정했습니다. 다시 미리보기해주세요.")
                .doesNotContain("ObjectOptimisticLockingFailureException", "identifier");
        verify(excelCommitRowExecutor).recordFailure(eq(commitId), any(), eq(1),
                eq("다른 곳에서 이미 이 자산을 수정했습니다. 다시 미리보기해주세요."));
    }

    @Test
    void 그_외_예외는_원래_메시지_그대로_실패로_기록된다() {
        UUID commitId = UUID.randomUUID();
        TangibleAssetExcelRow row = row(1);
        doThrow(new IllegalStateException("카테고리를 찾을 수 없습니다"))
                .when(excelCommitRowExecutor).processInNewTransaction(any(), any(), any(), any());

        ExcelCommitRowResult result = processor.processRow(commitId, row, UUID.randomUUID());

        assertThat(result.isSuccess()).isFalse();
        assertThat(result.getErrorMessage()).isEqualTo("카테고리를 찾을 수 없습니다");
    }
}
