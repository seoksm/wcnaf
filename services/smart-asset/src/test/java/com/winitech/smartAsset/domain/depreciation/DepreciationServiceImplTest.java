package com.winitech.smartAsset.domain.depreciation;

import com.winitech.common.bean.LoginUserContext;
import com.winitech.common.exception.InvalidParamException;
import com.winitech.smartAsset.domain.assetCategory.AssetCategory;
import com.winitech.smartAsset.domain.assetCategory.AssetCategoryReader;
import com.winitech.smartAsset.domain.tangibleAsset.TangibleAsset;
import com.winitech.smartAsset.domain.tangibleAsset.TangibleAssetReader;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

/**
 * S-232 결산 확정 스냅샷 - 확정 후 과거 확정 결과가 현재 tangible_asset/asset_category 상태에 따라
 * 흔들리지 않아야 한다는 요구사항을 검증한다. 순수 Mockito 단위 테스트로, Reader/Store 경계는 모두
 * 모킹하고 TangibleAsset/AssetCategory/DepreciationSnapshot 엔티티 자체의 생성자·계산 로직은 실제로 수행한다.
 */
@ExtendWith(MockitoExtension.class)
class DepreciationServiceImplTest {

    @Mock private TangibleAssetReader tangibleAssetReader;
    @Mock private AssetCategoryReader assetCategoryReader;
    @Mock private DepreciationSnapshotReader snapshotReader;
    @Mock private DepreciationSnapshotStore snapshotStore;
    @Mock private DepreciationConfirmationReader confirmationReader;
    @Mock private DepreciationConfirmationStore confirmationStore;
    @Mock private LoginUserContext loginUserContext;

    private DepreciationServiceImpl service;

    @BeforeEach
    void setUp() {
        service = new DepreciationServiceImpl(tangibleAssetReader, assetCategoryReader, snapshotReader, snapshotStore,
                confirmationReader, confirmationStore, loginUserContext);
        lenient().when(loginUserContext.getUserId()).thenReturn(UUID.randomUUID());
    }

    private AssetCategory mockCategory(String name, Integer usefulLifeMonths) {
        AssetCategory category = mock(AssetCategory.class);
        lenient().when(category.getId()).thenReturn(UUID.randomUUID());
        lenient().when(category.getCategoryName()).thenReturn(name);
        lenient().when(category.getUsefulLifeMonths()).thenReturn(usefulLifeMonths);
        lenient().when(category.getResidualRate()).thenReturn(BigDecimal.ZERO);
        lenient().when(category.getMemorandumValue()).thenReturn(BigDecimal.valueOf(1000));
        return category;
    }

    private TangibleAsset asset(String code, String name, AssetCategory category, TangibleAsset.LifeStatus lifeStatus,
                                 LocalDate acquisitionDate, BigDecimal amount) {
        return TangibleAsset.builder()
                .assetCode(code)
                .assetName(name)
                .category(category)
                .lifeStatus(lifeStatus)
                .acquisitionDate(acquisitionDate)
                .acquisitionAmount(amount)
                .build();
    }

    private DepreciationSnapshot snapshot(TangibleAsset asset, int fiscalYear, DepreciationQuarter quarter, String assetName,
                                           BigDecimal opening, BigDecimal period, BigDecimal closing, BigDecimal bookValue) {
        return DepreciationSnapshot.builder()
                .tangibleAsset(asset)
                .fiscalYear(fiscalYear)
                .quarter(quarter)
                .assetCode(asset.getAssetCode())
                .assetName(assetName)
                .categoryName("노트북")
                .acquisitionDate(LocalDate.of(2024, 1, 1))
                .acquisitionAmount(asset.getAcquisitionAmount())
                .usefulLifeMonths(36)
                .openingAccumulated(opening)
                .periodDepreciation(period)
                .closingAccumulated(closing)
                .bookValue(bookValue)
                .build();
    }

    @Test
    void 확정_이후_자산이_수정돼도_확정_결과는_그대로_유지된다() {
        int fiscalYear = 2026;
        DepreciationQuarter quarter = DepreciationQuarter.Q2;
        when(confirmationReader.findActiveByPeriod(fiscalYear, quarter)).thenReturn(Optional.of(
                DepreciationConfirmation.builder().fiscalYear(fiscalYear).quarter(quarter).confirmedBy(UUID.randomUUID()).build()));

        TangibleAsset fkAsset = asset("A-0001", "확정당시이름", mockCategory("노트북", 36), TangibleAsset.LifeStatus.USE,
                LocalDate.of(2024, 1, 1), BigDecimal.valueOf(3_000_000));
        DepreciationSnapshot snapshot = snapshot(fkAsset, fiscalYear, quarter, "확정당시이름",
                BigDecimal.valueOf(500_000), BigDecimal.valueOf(250_000), BigDecimal.valueOf(750_000), BigDecimal.valueOf(2_250_000));
        when(snapshotReader.findByPeriod(fiscalYear, quarter)).thenReturn(List.of(snapshot));

        DepreciationStatusInfo status = service.loadStatus(fiscalYear, quarter);

        assertThat(status.getRows()).hasSize(1);
        DepreciationRowInfo row = status.getRows().get(0);
        assertThat(row.getAssetName()).isEqualTo("확정당시이름");
        assertThat(row.getBookValue()).isEqualByComparingTo(BigDecimal.valueOf(2_250_000));
        assertThat(status.getSummary().isConfirmed()).isTrue();

        // 확정된 기간은 스냅샷값만 사용한다 - 이후 자산이 실제로 수정됐더라도 그 값을 다시 읽으러 가지 않는다.
        verify(tangibleAssetReader, never()).findAllByContainsKeyword(any(), any());
        verify(assetCategoryReader, never()).findAllByContainsKeyword(any());
    }

    @Test
    void 확정_이후_자산이_불용_처리돼도_확정_결과의_제외_여부는_바뀌지_않는다() {
        int fiscalYear = 2026;
        DepreciationQuarter quarter = DepreciationQuarter.Q1;
        when(confirmationReader.findActiveByPeriod(fiscalYear, quarter)).thenReturn(Optional.of(
                DepreciationConfirmation.builder().fiscalYear(fiscalYear).quarter(quarter).confirmedBy(UUID.randomUUID()).build()));

        // 확정 당시에는 상각 대상(제외 아님)이었던 자산 - 지금 이 자산이 불용 처리됐다고 해도
        // loadStatus()는 tangibleAssetReader를 전혀 건드리지 않으므로 그 사실을 알 방법이 없고, 그래서 안전하다.
        TangibleAsset fkAsset = asset("A-0002", "노트북", mockCategory("노트북", 36), TangibleAsset.LifeStatus.USE,
                LocalDate.of(2024, 1, 1), BigDecimal.valueOf(1_000_000));
        DepreciationSnapshot snapshot = snapshot(fkAsset, fiscalYear, quarter, "노트북",
                BigDecimal.ZERO, BigDecimal.valueOf(83_000), BigDecimal.valueOf(83_000), BigDecimal.valueOf(917_000));
        when(snapshotReader.findByPeriod(fiscalYear, quarter)).thenReturn(List.of(snapshot));

        DepreciationStatusInfo status = service.loadStatus(fiscalYear, quarter);

        assertThat(status.getRows()).hasSize(1);
        assertThat(status.getRows().get(0).getExcludedReason()).isNull();
        verify(tangibleAssetReader, never()).findAllByContainsKeyword(any(), any());
    }

    @Test
    void 확정_이후_등록된_신규_자산은_확정_결과에_포함되지_않는다() {
        int fiscalYear = 2026;
        DepreciationQuarter quarter = DepreciationQuarter.Q1;
        when(confirmationReader.findActiveByPeriod(fiscalYear, quarter)).thenReturn(Optional.of(
                DepreciationConfirmation.builder().fiscalYear(fiscalYear).quarter(quarter).confirmedBy(UUID.randomUUID()).build()));

        TangibleAsset fkAsset = asset("A-0001", "확정된자산", mockCategory("노트북", 36), TangibleAsset.LifeStatus.USE,
                LocalDate.of(2024, 1, 1), BigDecimal.valueOf(1_000_000));
        DepreciationSnapshot onlyConfirmedAsset = snapshot(fkAsset, fiscalYear, quarter, "확정된자산",
                BigDecimal.ZERO, BigDecimal.valueOf(83_000), BigDecimal.valueOf(83_000), BigDecimal.valueOf(917_000));
        when(snapshotReader.findByPeriod(fiscalYear, quarter)).thenReturn(List.of(onlyConfirmedAsset));

        DepreciationStatusInfo status = service.loadStatus(fiscalYear, quarter);

        // 확정 이후 등록된 신규 자산은 스냅샷 테이블에 행이 없으므로 결과에 자동으로 섞이지 않는다.
        assertThat(status.getRows()).hasSize(1);
        assertThat(status.getRows().get(0).getAssetName()).isEqualTo("확정된자산");
        verify(tangibleAssetReader, never()).findAllByContainsKeyword(any(), any());
    }

    @Test
    void 제외_자산도_제외_사유와_함께_스냅샷으로_보존된다() {
        int fiscalYear = 2026;
        DepreciationQuarter quarter = DepreciationQuarter.Q4;
        when(confirmationReader.findActiveByPeriod(fiscalYear, quarter)).thenReturn(Optional.empty());

        AssetCategory eligibleCategory = mockCategory("노트북", 36);
        AssetCategory noBasisCategory = mockCategory("기타", null);
        TangibleAsset eligibleAsset = asset("A-0001", "노트북1", eligibleCategory, TangibleAsset.LifeStatus.USE,
                LocalDate.of(2024, 1, 1), BigDecimal.valueOf(3_000_000));
        TangibleAsset excludedAsset = asset("A-0002", "구형PC", noBasisCategory, TangibleAsset.LifeStatus.USE,
                LocalDate.of(2023, 1, 1), BigDecimal.valueOf(1_000_000));

        when(assetCategoryReader.findAllByContainsKeyword(null)).thenReturn(List.of(eligibleCategory, noBasisCategory));
        when(tangibleAssetReader.findAllByContainsKeyword(eq(null), any(Pageable.class)))
                .thenReturn(new PageImpl<>(List.of(eligibleAsset, excludedAsset)));

        service.confirm(fiscalYear, quarter);

        @SuppressWarnings("unchecked")
        ArgumentCaptor<List<DepreciationSnapshot>> captor = ArgumentCaptor.forClass(List.class);
        verify(snapshotStore).storeAll(captor.capture());
        List<DepreciationSnapshot> saved = captor.getValue();
        assertThat(saved).hasSize(2);

        DepreciationSnapshot excludedSnapshot = saved.stream()
                .filter(s -> "A-0002".equals(s.getAssetCode())).findFirst().orElseThrow();
        assertThat(excludedSnapshot.getExcludedReason()).isEqualTo(DepreciationCalculator.ExcludedReason.NO_BASIS);
        assertThat(excludedSnapshot.getOpeningAccumulated()).isNull();
        assertThat(excludedSnapshot.getPeriodDepreciation()).isNull();
        assertThat(excludedSnapshot.getClosingAccumulated()).isNull();
        assertThat(excludedSnapshot.getBookValue()).isEqualByComparingTo(BigDecimal.valueOf(1_000_000));

        DepreciationSnapshot includedSnapshot = saved.stream()
                .filter(s -> "A-0001".equals(s.getAssetCode())).findFirst().orElseThrow();
        assertThat(includedSnapshot.getExcludedReason()).isNull();
        assertThat(includedSnapshot.getOpeningAccumulated()).isNotNull();

        verify(confirmationStore).store(any());
    }

    @Test
    void 동일_기간을_중복으로_확정하면_거부된다() {
        int fiscalYear = 2026;
        DepreciationQuarter quarter = DepreciationQuarter.Q1;
        when(confirmationReader.findActiveByPeriod(fiscalYear, quarter)).thenReturn(Optional.of(
                DepreciationConfirmation.builder().fiscalYear(fiscalYear).quarter(quarter).confirmedBy(UUID.randomUUID()).build()));

        assertThatThrownBy(() -> service.confirm(fiscalYear, quarter)).isInstanceOf(InvalidParamException.class);

        verify(snapshotStore, never()).storeAll(any());
        verify(confirmationStore, never()).store(any());
    }

    @Test
    void 동시_확정_요청은_DB_유니크_제약_위반을_통해_거부된다() {
        int fiscalYear = 2026;
        DepreciationQuarter quarter = DepreciationQuarter.Q1;
        // 사전 체크 시점에는 아직 확정된 기간이 아니었지만(경쟁 상태), 실제 저장 시점에 부분 유니크
        // 인덱스 위반이 발생하는 상황을 흉내낸다.
        when(confirmationReader.findActiveByPeriod(fiscalYear, quarter)).thenReturn(Optional.empty());
        when(assetCategoryReader.findAllByContainsKeyword(null)).thenReturn(List.of());
        when(tangibleAssetReader.findAllByContainsKeyword(eq(null), any(Pageable.class))).thenReturn(new PageImpl<>(List.of()));
        doThrow(new DataIntegrityViolationException("duplicate key")).when(confirmationStore).store(any());

        assertThatThrownBy(() -> service.confirm(fiscalYear, quarter)).isInstanceOf(InvalidParamException.class);
    }

    @Test
    void 확정_해제_후에는_실시간_계산으로_돌아간다() {
        int fiscalYear = 2026;
        DepreciationQuarter quarter = DepreciationQuarter.Q1;
        DepreciationConfirmation confirmation = DepreciationConfirmation.builder()
                .fiscalYear(fiscalYear).quarter(quarter).confirmedBy(UUID.randomUUID()).build();
        when(confirmationReader.findActiveByPeriod(fiscalYear, quarter)).thenReturn(Optional.of(confirmation));

        service.release(fiscalYear, quarter, "확정 오류 정정");

        assertThat(confirmation.getReleasedAt()).isNotNull();
        verify(snapshotStore).deleteByPeriod(fiscalYear, quarter);

        // 해제 이후에는 활성 확정이 더 이상 없다 - loadStatus()가 실시간 계산 경로를 타야 한다.
        when(confirmationReader.findActiveByPeriod(fiscalYear, quarter)).thenReturn(Optional.empty());
        AssetCategory category = mockCategory("노트북", 36);
        TangibleAsset asset = asset("A-0001", "노트북", category, TangibleAsset.LifeStatus.USE,
                LocalDate.of(2024, 1, 1), BigDecimal.valueOf(3_000_000));
        when(assetCategoryReader.findAllByContainsKeyword(null)).thenReturn(List.of(category));
        when(tangibleAssetReader.findAllByContainsKeyword(eq(null), any(Pageable.class)))
                .thenReturn(new PageImpl<>(List.of(asset)));

        DepreciationStatusInfo status = service.loadStatus(fiscalYear, quarter);

        assertThat(status.getSummary().isConfirmed()).isFalse();
        assertThat(status.getRows()).hasSize(1);
        verify(tangibleAssetReader).findAllByContainsKeyword(eq(null), any(Pageable.class));
    }

    @Test
    void 확정된_분기의_제외_상태는_확정_당시_스냅샷_기준으로_유지된다() {
        UUID assetId = UUID.randomUUID();
        int fiscalYear = 2026;

        AssetCategory category = mockCategory("노트북", 36);
        // 지금은 불용 상태로 바뀐 자산 - 미확정 분기라면 이 상태 때문에 제외로 나와야 한다.
        TangibleAsset asset = asset("A-0001", "노트북", category, TangibleAsset.LifeStatus.DISUSE,
                LocalDate.of(2024, 1, 1), BigDecimal.valueOf(3_000_000));
        when(tangibleAssetReader.findById(assetId)).thenReturn(asset);
        when(assetCategoryReader.findById(any())).thenReturn(category);

        DepreciationSnapshot q1Snapshot = DepreciationSnapshot.builder()
                .tangibleAsset(asset).fiscalYear(fiscalYear).quarter(DepreciationQuarter.Q1)
                .assetCode("A-0001").assetName("노트북").categoryName("노트북")
                .acquisitionDate(LocalDate.of(2024, 1, 1)).acquisitionAmount(BigDecimal.valueOf(3_000_000))
                .usefulLifeMonths(36)
                .openingAccumulated(BigDecimal.ZERO).periodDepreciation(BigDecimal.valueOf(250_000))
                .closingAccumulated(BigDecimal.valueOf(250_000)).bookValue(BigDecimal.valueOf(2_750_000))
                .build();
        when(snapshotReader.findByAssetAndYear(assetId, fiscalYear)).thenReturn(List.of(q1Snapshot));
        // Q2/Q3/Q4는 스냅샷이 없어 isActiveConfirmed 판단이 단락 평가로 끝나므로 confirmationReader까지
        // 조회하지 않는다 - Q1만 스텁하면 된다.
        when(confirmationReader.findActiveByPeriod(fiscalYear, DepreciationQuarter.Q1)).thenReturn(Optional.of(
                DepreciationConfirmation.builder().fiscalYear(fiscalYear).quarter(DepreciationQuarter.Q1).confirmedBy(UUID.randomUUID()).build()));

        List<DepreciationScheduleRowInfo> schedule = service.loadSchedule(assetId, fiscalYear);

        DepreciationScheduleRowInfo q1 = schedule.stream().filter(r -> "Q1".equals(r.getQuarter())).findFirst().orElseThrow();
        assertThat(q1.isConfirmed()).isTrue();
        // 확정 당시에는 제외 대상이 아니었으므로, 지금 자산이 불용으로 바뀌어도 Q1은 그대로 유지된다.
        assertThat(q1.isExcluded()).isFalse();

        DepreciationScheduleRowInfo q2 = schedule.stream().filter(r -> "Q2".equals(r.getQuarter())).findFirst().orElseThrow();
        assertThat(q2.isConfirmed()).isFalse();
        // 자산을 빌드한 시점(lifeStatusChangedAt=now, 테스트 실행 시각)이 Q2(1~6월) 이후이므로
        // Q2는 아직 동결되지 않은 정상 상각 구간이다 - 더 이상 불용/처분완료라고 무조건 제외하지
        // 않는다(D4: 불용 처리한 "달까지" 상각하고 그 다음 달부터 동결). 동결 로직 자체(과거 시점에
        // 불용된 자산이 그 이후 분기에서 상각액 0으로 고정되는지)는 DepreciationCalculatorTest에서
        // lifeStatusChangedAt을 직접 과거로 지정해 별도로 검증한다.
        assertThat(q2.isExcluded()).isFalse();
        assertThat(q2.getBookValue()).isNotNull();
    }
}
