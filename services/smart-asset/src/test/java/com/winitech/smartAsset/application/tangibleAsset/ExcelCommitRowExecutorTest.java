package com.winitech.smartAsset.application.tangibleAsset;

import com.winitech.common.domain.common.CommonUserReader;
import com.winitech.common.exception.InvalidParamException;
import com.winitech.smartAsset.domain.tangibleAsset.*;
import com.winitech.smartAsset.infrastructure.tangibleAsset.ExcelRowSignerImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.orm.ObjectOptimisticLockingFailureException;

import java.time.OffsetDateTime;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

/**
 * S-213 엑셀 확정 행(row) 1건 처리 - ExcelCommitRowExecutor.processInNewTransaction()의 재검증·
 * 반영 로직을 검증한다. 실제 HMAC 서명 계산까지 exercise하기 위해 ExcelRowSigner는 진짜 구현
 * (ExcelRowSignerImpl)을 쓰고, 그 외 리포지토리/서비스 경계는 Mockito로 모킹한 순수 단위 테스트다.
 * REQUIRES_NEW 트랜잭션 원자성 자체(자산 저장과 행 완료 기록이 함께 롤백되는지)는 실제 DB가 필요하므로
 * ExcelCommitRowExecutorAtomicityTest에서 별도로 검증한다.
 */
@ExtendWith(MockitoExtension.class)
class ExcelCommitRowExecutorTest {

    @Mock private ExcelCommitRowStore excelCommitRowStore;
    @Mock private CommonUserReader commonUserReader;
    @Mock private TangibleAssetReader tangibleAssetReader;
    @Mock private TangibleAssetService tangibleAssetService;

    private final ExcelRowSigner excelRowSigner = new ExcelRowSignerImpl("test-signing-key-for-unit-tests-only-do-not-reuse");

    private ExcelCommitRowExecutor executor;

    @BeforeEach
    void setUp() {
        executor = new ExcelCommitRowExecutor(excelCommitRowStore, excelRowSigner, commonUserReader, tangibleAssetReader, tangibleAssetService);
        lenient().when(excelCommitRowStore.claimRow(any(), any(), anyInt())).thenReturn(true);
    }

    private TangibleAssetExcelRow signedCreateRow(int rowNum) {
        TangibleAssetExcelRow row = TangibleAssetExcelRow.builder()
                .rowNum(rowNum)
                .action("CREATE")
                .assetName("노트북")
                .categoryId(UUID.randomUUID())
                .locationId(UUID.randomUUID())
                .lifeStatus("USE")
                .assignType("UNASSIGNED")
                .acquisitionDate("2026-01-01")
                .acquisitionAmount("1000000")
                .modelName("")
                .manufacturer("")
                .serialNo("")
                .memo("")
                .build();
        row.setSignature(excelRowSigner.sign(row));
        return row;
    }

    private TangibleAssetExcelRow signedUpdateRow(int rowNum, UUID tangibleAssetId, Long entityVersion) {
        TangibleAssetExcelRow row = TangibleAssetExcelRow.builder()
                .rowNum(rowNum)
                .action("UPDATE")
                .tangibleAssetId(tangibleAssetId)
                .assetName("노트북")
                .categoryId(UUID.randomUUID())
                .locationId(UUID.randomUUID())
                .lifeStatus("USE")
                .assignType("UNASSIGNED")
                .acquisitionDate("2026-01-01")
                .acquisitionAmount("1000000")
                .modelName("")
                .manufacturer("")
                .serialNo("")
                .memo("")
                .entityVersion(entityVersion)
                .build();
        row.setSignature(excelRowSigner.sign(row));
        return row;
    }

    @Test
    void 변조된_행은_서명_불일치로_거부된다() {
        TangibleAssetExcelRow row = signedCreateRow(1);
        row.setCategoryId(UUID.randomUUID()); // 서명 계산 이후 클라이언트가 카테고리를 몰래 바꿔치기

        assertThatThrownBy(() -> executor.processInNewTransaction(UUID.randomUUID(), "1-x", row, UUID.randomUUID()))
                .isInstanceOf(InvalidParamException.class)
                .hasMessageContaining("일치하지 않습니다");
        verify(tangibleAssetService, never()).createTangibleAsset(any(), any());
    }

    @Test
    void 배정_사용자가_더이상_존재하지_않으면_거부된다() {
        TangibleAssetExcelRow row = TangibleAssetExcelRow.builder()
                .rowNum(1)
                .action("CREATE")
                .assetName("노트북")
                .categoryId(UUID.randomUUID())
                .locationId(UUID.randomUUID())
                .lifeStatus("USE")
                .assignType("PERSONAL")
                .currentMemberId(UUID.randomUUID())
                .acquisitionDate("2026-01-01")
                .acquisitionAmount("1000000")
                .build();
        row.setSignature(excelRowSigner.sign(row));

        when(commonUserReader.isExistCommonUserById(row.getCurrentMemberId())).thenReturn(false);

        assertThatThrownBy(() -> executor.processInNewTransaction(UUID.randomUUID(), "1-x", row, UUID.randomUUID()))
                .isInstanceOf(InvalidParamException.class)
                .hasMessageContaining("사용자를 찾을 수 없습니다");
        verify(tangibleAssetService, never()).createTangibleAsset(any(), any());
    }

    @Test
    void 카테고리가_그사이_삭제됐으면_예외가_전파된다() {
        TangibleAssetExcelRow row = signedCreateRow(1);
        doThrow(new java.util.NoSuchElementException()).when(tangibleAssetService).createTangibleAsset(any(), any());

        assertThatThrownBy(() -> executor.processInNewTransaction(UUID.randomUUID(), "1-x", row, UUID.randomUUID()))
                .isInstanceOf(java.util.NoSuchElementException.class);
    }

    @Test
    void preview_이후_자산이_변경됐으면_버전_불일치로_감지된다() {
        UUID assetId = UUID.randomUUID();
        TangibleAssetExcelRow row = signedUpdateRow(1, assetId, 3L);

        // TangibleAssetService.updateTangibleAsset이 자산을 다시 읽었을 때 이미 다른 곳에서 수정되어
        // 버전이 올라간 상황(4)을 흉내낸다 - 실제 버전 비교는 TangibleAssetServiceImpl.applyUpdate()
        // 안에서 일어나므로, 여기서는 그 호출이 ObjectOptimisticLockingFailureException을 던지는
        // 상황을 그대로 재현해 executor가 이를 그대로(사용자 메시지로 변환하지 않고) 전파하는지 본다 -
        // 사용자 메시지 변환은 TangibleAssetExcelFacade의 행별 catch에서 일어난다.
        doThrow(new ObjectOptimisticLockingFailureException(
                com.winitech.smartAsset.domain.tangibleAsset.TangibleAsset.class, assetId))
                .when(tangibleAssetService).updateTangibleAsset(any(), any());

        assertThatThrownBy(() -> executor.processInNewTransaction(UUID.randomUUID(), "1-x", row, UUID.randomUUID()))
                .isInstanceOf(ObjectOptimisticLockingFailureException.class);
    }

    @Test
    void 이미_다른_시도가_이_행을_끝냈으면_재처리하지_않고_저장된_결과를_반환한다() {
        TangibleAssetExcelRow row = signedCreateRow(1);
        UUID commitId = UUID.randomUUID();
        ExcelCommitRowResult stored = ExcelCommitRowResult.success("CREATED", UUID.randomUUID());

        when(excelCommitRowStore.claimRow(eq(commitId), eq("1-x"), eq(1))).thenReturn(false);
        when(excelCommitRowStore.findFinished(commitId, "1-x")).thenReturn(Optional.of(stored));

        ExcelCommitRowResult result = executor.processInNewTransaction(commitId, "1-x", row, UUID.randomUUID());

        assertThat(result).isEqualTo(stored);
        verify(tangibleAssetService, never()).createTangibleAsset(any(), any());
    }

    @Test
    void 등록_성공_시_행_완료가_기록된다() {
        TangibleAssetExcelRow row = signedCreateRow(1);
        UUID createdId = UUID.randomUUID();
        when(tangibleAssetService.createTangibleAsset(any(), any())).thenReturn(createdId);

        ExcelCommitRowResult result = executor.processInNewTransaction(UUID.randomUUID(), "1-x", row, UUID.randomUUID());

        assertThat(result.isSuccess()).isTrue();
        assertThat(result.getResultAction()).isEqualTo("CREATED");
        assertThat(result.getTangibleAssetId()).isEqualTo(createdId);
        verify(excelCommitRowStore).completeRow(any(), eq("1-x"), any());
    }

    @Test
    void 수정_성공_시_expectedVersion이_함께_전달된다() {
        UUID assetId = UUID.randomUUID();
        TangibleAssetExcelRow row = signedUpdateRow(1, assetId, 7L);

        executor.processInNewTransaction(UUID.randomUUID(), "1-x", row, UUID.randomUUID());

        org.mockito.ArgumentCaptor<TangibleAssetCommand.UpdateCommand> captor =
                org.mockito.ArgumentCaptor.forClass(TangibleAssetCommand.UpdateCommand.class);
        verify(tangibleAssetService).updateTangibleAsset(captor.capture(), any());
        assertThat(captor.getValue().getExpectedVersion()).isEqualTo(7L);
    }
}
