package com.winitech.smartAsset.domain.intangibleAsset;

import com.winitech.common.bean.LoginUserContext;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.isNull;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * GET /intangible-asset?withinDays=90 회귀 테스트 - withinDays가 오늘 기준 며칠 뒤 만료일로
 * 정확히 변환되어 Reader에 전달되는지 검증한다(리포지토리 계층의 실제 SQL 수정은
 * IntangibleAssetRepositoryTest가 실제 Postgres로 검증한다). Reader/Store는 모두 모킹하는
 * 순수 Mockito 단위 테스트다 - TangibleAssetServiceImplTest와 동일한 스타일.
 */
@ExtendWith(MockitoExtension.class)
class IntangibleAssetServiceImplTest {

    @Mock private IntangibleAssetReader intangibleAssetReader;
    @Mock private IntangibleAssetStore intangibleAssetStore;
    @Mock private IntangibleAssetActionLogReader intangibleAssetActionLogReader;
    @Mock private IntangibleAssetActionLogStore intangibleAssetActionLogStore;
    @Mock private LoginUserContext loginUserContext;

    @Test
    void withinDays가_주어지면_오늘부터_그만큼_뒤_날짜를_expiryBefore로_전달한다() {
        IntangibleAssetServiceImpl service = new IntangibleAssetServiceImpl(
                intangibleAssetReader, intangibleAssetStore, intangibleAssetActionLogReader,
                intangibleAssetActionLogStore, loginUserContext);

        when(intangibleAssetReader.findAll(isNull(), any(LocalDate.class), any(Pageable.class)))
                .thenReturn(new PageImpl<>(java.util.List.of()));

        Page<IntangibleAssetInfo> result = service.loadList(null, 90, null, null);

        ArgumentCaptor<LocalDate> expiryBeforeCaptor = ArgumentCaptor.forClass(LocalDate.class);
        verify(intangibleAssetReader).findAll(isNull(), expiryBeforeCaptor.capture(), any(Pageable.class));

        assertThat(expiryBeforeCaptor.getValue()).isEqualTo(LocalDate.now().plusDays(90));
        assertThat(result.getContent()).isEmpty();
    }

    @Test
    void withinDays가_없으면_expiryBefore_없이_조회한다() {
        IntangibleAssetServiceImpl service = new IntangibleAssetServiceImpl(
                intangibleAssetReader, intangibleAssetStore, intangibleAssetActionLogReader,
                intangibleAssetActionLogStore, loginUserContext);

        when(intangibleAssetReader.findAll(eq("keyword"), isNull(), any(Pageable.class)))
                .thenReturn(new PageImpl<>(java.util.List.of()));

        service.loadList("keyword", null, null, null);

        verify(intangibleAssetReader).findAll(eq("keyword"), isNull(), any(Pageable.class));
    }
}
