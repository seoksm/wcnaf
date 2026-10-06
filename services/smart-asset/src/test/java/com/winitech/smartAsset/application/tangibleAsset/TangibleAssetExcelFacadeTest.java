package com.winitech.smartAsset.application.tangibleAsset;

import com.winitech.common.bean.LoginUserContext;
import com.winitech.common.domain.common.CommonUserReader;
import com.winitech.common.exception.InvalidParamException;
import com.winitech.smartAsset.domain.assetCategory.AssetCategoryReader;
import com.winitech.smartAsset.domain.assetLocation.AssetLocationReader;
import com.winitech.smartAsset.domain.tangibleAsset.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.ByteArrayInputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * S-213 엑셀 업서트 확정(commit)의 요청 레벨 로직 검증 - 행 검증(null/빈 목록/중복 rowNum/최대 건수),
 * 요청 레벨 멱등성(ExcelCommitClaim 분기), 행별 처리 결과 집계를 다룬다. 행 하나하나의 재검증·반영
 * 로직(서명/버전/사용자 존재 확인)은 ExcelCommitRowExecutorTest에서 별도로 검증하므로, 여기서는
 * ExcelCommitRowProcessor를 모킹해 파사드 자체의 조율 로직만 순수 단위 테스트로 확인한다.
 */
@ExtendWith(MockitoExtension.class)
class TangibleAssetExcelFacadeTest {

    @Mock private AssetCategoryReader assetCategoryReader;
    @Mock private AssetLocationReader assetLocationReader;
    @Mock private CommonUserReader commonUserReader;
    @Mock private TangibleAssetReader tangibleAssetReader;
    @Mock private ExcelRowSigner excelRowSigner;
    @Mock private ExcelCommitIdempotencyStore idempotencyStore;
    @Mock private ExcelCommitRowProcessor excelCommitRowProcessor;
    @Mock private LoginUserContext loginUserContext;

    private TangibleAssetExcelFacade facade;

    @BeforeEach
    void setUp() {
        facade = new TangibleAssetExcelFacade(
                assetCategoryReader, assetLocationReader, commonUserReader, tangibleAssetReader,
                excelRowSigner, idempotencyStore, excelCommitRowProcessor, loginUserContext);

        lenient().when(idempotencyStore.claim(any(), any(), any())).thenReturn(ExcelCommitClaim.claimed());
    }

    private TangibleAssetExcelRow row(int rowNum, String action) {
        return TangibleAssetExcelRow.builder()
                .rowNum(rowNum)
                .action(action)
                .assetName("노트북")
                .signature("sig-" + rowNum)
                .build();
    }

    @Test
    void rows가_null이면_거부한다() {
        assertThatThrownBy(() -> facade.commit(UUID.randomUUID(), null))
                .isInstanceOf(InvalidParamException.class);
        verify(idempotencyStore, never()).claim(any(), any(), any());
    }

    @Test
    void rows가_비어있으면_거부한다() {
        assertThatThrownBy(() -> facade.commit(UUID.randomUUID(), List.of()))
                .isInstanceOf(InvalidParamException.class);
    }

    @Test
    void rows에_null_행이_포함되면_거부한다() {
        List<TangibleAssetExcelRow> rows = new ArrayList<>();
        rows.add(row(1, "CREATE"));
        rows.add(null);

        assertThatThrownBy(() -> facade.commit(UUID.randomUUID(), rows))
                .isInstanceOf(InvalidParamException.class);
    }

    @Test
    void 중복된_행_번호는_거부한다() {
        List<TangibleAssetExcelRow> rows = List.of(row(1, "CREATE"), row(1, "UPDATE"));

        assertThatThrownBy(() -> facade.commit(UUID.randomUUID(), rows))
                .isInstanceOf(InvalidParamException.class);
    }

    @Test
    void 확정_행_개수가_최대치를_넘으면_처리하지_않고_거부한다() {
        List<TangibleAssetExcelRow> tooMany = new ArrayList<>();
        for (int i = 0; i < 2001; i++) {
            tooMany.add(row(i, "CREATE"));
        }

        assertThatThrownBy(() -> facade.commit(UUID.randomUUID(), tooMany))
                .isInstanceOf(InvalidParamException.class);
        verify(idempotencyStore, never()).claim(any(), any(), any());
    }

    @Test
    void 이미_완료된_요청이면_저장된_결과를_그대로_반환한다() {
        UUID commitId = UUID.randomUUID();
        TangibleAssetExcelCommitResult stored = TangibleAssetExcelCommitResult.builder()
                .createdCount(3).updatedCount(1).skippedCount(0).failedRows(new ArrayList<>()).build();
        when(idempotencyStore.claim(any(), any(), any())).thenReturn(ExcelCommitClaim.alreadyCompleted(stored));

        TangibleAssetExcelCommitResult result = facade.commit(commitId, List.of(row(1, "CREATE")));

        assertThat(result).isEqualTo(stored);
        verify(excelCommitRowProcessor, never()).processRow(any(), any(), any());
    }

    @Test
    void 유효하게_처리중인_요청이면_거부한다() {
        when(idempotencyStore.claim(any(), any(), any())).thenReturn(ExcelCommitClaim.inProgressElsewhere());

        assertThatThrownBy(() -> facade.commit(UUID.randomUUID(), List.of(row(1, "CREATE"))))
                .isInstanceOf(InvalidParamException.class)
                .hasMessageContaining("처리 중");
        verify(excelCommitRowProcessor, never()).processRow(any(), any(), any());
    }

    @Test
    void 같은_commitId에_다른_내용이면_거부한다() {
        when(idempotencyStore.claim(any(), any(), any())).thenReturn(ExcelCommitClaim.requestMismatch());

        assertThatThrownBy(() -> facade.commit(UUID.randomUUID(), List.of(row(1, "CREATE"))))
                .isInstanceOf(InvalidParamException.class)
                .hasMessageContaining("다른 내용");
        verify(excelCommitRowProcessor, never()).processRow(any(), any(), any());
    }

    @Test
    void 행별_처리_결과가_생성_수정_실패로_올바르게_집계된다() {
        TangibleAssetExcelRow createRow = row(1, "CREATE");
        TangibleAssetExcelRow updateRow = row(2, "UPDATE");
        TangibleAssetExcelRow errorRow = row(3, "CREATE");
        TangibleAssetExcelRow skipRow = row(4, "ERROR");

        when(excelCommitRowProcessor.processRow(any(), eq(createRow), any()))
                .thenReturn(ExcelCommitRowResult.success("CREATED", UUID.randomUUID()));
        when(excelCommitRowProcessor.processRow(any(), eq(updateRow), any()))
                .thenReturn(ExcelCommitRowResult.success("UPDATED", UUID.randomUUID()));
        when(excelCommitRowProcessor.processRow(any(), eq(errorRow), any()))
                .thenReturn(ExcelCommitRowResult.failure("카테고리를 찾을 수 없습니다"));

        TangibleAssetExcelCommitResult result = facade.commit(UUID.randomUUID(),
                Arrays.asList(createRow, updateRow, errorRow, skipRow));

        assertThat(result.getCreatedCount()).isEqualTo(1);
        assertThat(result.getUpdatedCount()).isEqualTo(1);
        assertThat(result.getSkippedCount()).isEqualTo(1);
        assertThat(result.getFailedRows()).hasSize(1);
        assertThat(result.getFailedRows().get(0).getRowNum()).isEqualTo(3);
        assertThat(result.getFailedRows().get(0).getErrorMessage()).isEqualTo("카테고리를 찾을 수 없습니다");
        verify(excelCommitRowProcessor, never()).processRow(any(), eq(skipRow), any());
        verify(idempotencyStore).complete(any(), eq(result));
    }

    @Test
    void 요청_루프_자체가_예상치_못하게_실패하면_FAILED로_기록하고_예외를_전파한다() {
        TangibleAssetExcelRow createRow = row(1, "CREATE");
        when(excelCommitRowProcessor.processRow(any(), eq(createRow), any()))
                .thenThrow(new RuntimeException("예상 못한 오류"));

        UUID commitId = UUID.randomUUID();
        assertThatThrownBy(() -> facade.commit(commitId, List.of(createRow)))
                .isInstanceOf(RuntimeException.class);

        verify(idempotencyStore).fail(eq(commitId), any());
        verify(idempotencyStore, never()).complete(any(), any());
    }

    @Test
    void 엑셀이_아닌_파일은_미리보기에서_거부된다() {
        assertThatThrownBy(() -> facade.preview(new ByteArrayInputStream("이건 엑셀 파일이 아닙니다".getBytes())))
                .isInstanceOf(InvalidParamException.class);
    }

    @Test
    void 양식_예시행의_종류_위치는_실제_마스터데이터를_반영한다() {
        com.winitech.smartAsset.domain.assetCategory.AssetCategory category =
                com.winitech.smartAsset.domain.assetCategory.AssetCategory.builder()
                        .categoryCode("SERVER").categoryName("서버").build();
        com.winitech.smartAsset.domain.assetLocation.AssetLocation location =
                com.winitech.smartAsset.domain.assetLocation.AssetLocation.builder()
                        .locationName("판교오피스").build();
        when(assetCategoryReader.findAllByContainsKeyword(null)).thenReturn(List.of(category));
        when(assetLocationReader.findAllByContainsKeyword(null)).thenReturn(List.of(location));

        assertThat(facade.exampleCategoryName()).isEqualTo("서버");
        assertThat(facade.exampleLocationName()).isEqualTo("판교오피스");
    }

    @Test
    void 마스터데이터가_전혀_없으면_안내용_기본값으로_대체한다() {
        when(assetCategoryReader.findAllByContainsKeyword(null)).thenReturn(List.of());
        when(assetLocationReader.findAllByContainsKeyword(null)).thenReturn(List.of());

        assertThat(facade.exampleCategoryName()).isEqualTo("노트북");
        assertThat(facade.exampleLocationName()).isEqualTo("본사");
    }
}
