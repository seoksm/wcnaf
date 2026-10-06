package com.winitech.smartAsset.domain.depreciation;

import com.winitech.common.bean.LoginUserContext;
import com.winitech.common.exception.InvalidParamException;
import com.winitech.smartAsset.domain.assetCategory.AssetCategory;
import com.winitech.smartAsset.domain.assetCategory.AssetCategoryReader;
import com.winitech.smartAsset.domain.tangibleAsset.TangibleAsset;
import com.winitech.smartAsset.domain.tangibleAsset.TangibleAssetReader;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.YearMonth;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Consumer;
import java.util.stream.Collectors;

@Slf4j
@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class DepreciationServiceImpl implements DepreciationService {

    private final TangibleAssetReader tangibleAssetReader;
    private final AssetCategoryReader assetCategoryReader;
    private final DepreciationSnapshotReader snapshotReader;
    private final DepreciationSnapshotStore snapshotStore;
    private final DepreciationConfirmationReader confirmationReader;
    private final DepreciationConfirmationStore confirmationStore;
    private final LoginUserContext loginUserContext;

    /** 전 자산을 한 번에 List로 올리면 자산이 많을 때 메모리를 크게 잡아먹으므로, 정렬 기준(생성순)으로
     * 고정된 페이지 단위로 끊어 읽으며 그때그때 처리한다 - 끝까지 읽어도 한 번에 메모리에 남는 건
     * 이 배치 크기만큼의 엔티티뿐이다. */
    private static final int BATCH_SIZE = 500;

    private void forEachAssetInBatches(Consumer<TangibleAsset> consumer) {
        Pageable pageable = PageRequest.of(0, BATCH_SIZE, Sort.by(Sort.Direction.ASC, "createAt"));
        Page<TangibleAsset> assetPage;
        do {
            assetPage = tangibleAssetReader.findAllByContainsKeyword(null, pageable);
            assetPage.getContent().forEach(consumer);
            pageable = pageable.next();
        } while (assetPage.hasNext());
    }

    @Override
    public DepreciationStatusInfo loadStatus(int fiscalYear, DepreciationQuarter quarter) {
        Optional<DepreciationConfirmation> activeConfirmation = confirmationReader.findActiveByPeriod(fiscalYear, quarter);

        // 확정된 기간은 그 시점에 저장해 둔 스냅샷 행만 그대로 돌려준다 - 현재 tangible_asset/asset_category를
        // 다시 조회하지 않으므로(요구사항 1) 이후 자산 변경·불용 처리·분류 기준 변경·신규 자산 등록이 결과에
        // 전혀 영향을 주지 않는다(요구사항 4/5). 제외 자산도 확정 당시 스냅샷 행으로 남아 있으므로 그대로 포함된다.
        if (activeConfirmation.isPresent()) {
            List<DepreciationRowInfo> rows = snapshotReader.findByPeriod(fiscalYear, quarter).stream()
                    .map(DepreciationRowInfo::fromSnapshot)
                    .collect(Collectors.toList());
            DepreciationSummaryInfo summary = new DepreciationSummaryInfo(rows, true, activeConfirmation.get().getConfirmedAt());
            return new DepreciationStatusInfo(rows, summary);
        }

        // 미확정 기간은 지금까지와 동일하게 현재 자산을 배치 단위로 훑으며 실시간으로 계산한다.
        Map<UUID, AssetCategory> categoryMap = loadCategoryMap();
        YearMonth periodStart = YearMonth.of(fiscalYear, 1);
        YearMonth periodEnd = YearMonth.of(fiscalYear, quarter.getEndMonth());

        List<DepreciationRowInfo> rows = new ArrayList<>();
        forEachAssetInBatches(asset -> {
            AssetCategory category = categoryMap.get(asset.getCategory().getId());
            Optional<DepreciationCalculator.ExcludedReason> excluded = DepreciationCalculator.excludedReason(asset, category);

            if (excluded.isPresent()) {
                rows.add(DepreciationRowInfo.excluded(asset, category, excluded.get()));
            } else {
                DepreciationCalculator.Result result = DepreciationCalculator.calculate(asset, category, periodStart, periodEnd);
                rows.add(DepreciationRowInfo.fromCalculation(asset, category, result));
            }
        });

        DepreciationSummaryInfo summary = new DepreciationSummaryInfo(rows, false, null);
        return new DepreciationStatusInfo(rows, summary);
    }

    @Override
    public List<DepreciationScheduleRowInfo> loadSchedule(UUID tangibleAssetId, int fiscalYear) {
        TangibleAsset asset = tangibleAssetReader.findById(tangibleAssetId);
        AssetCategory category = assetCategoryReader.findById(asset.getCategory().getId());

        Map<DepreciationQuarter, DepreciationSnapshot> snapshotByQuarter = snapshotReader.findByAssetAndYear(tangibleAssetId, fiscalYear)
                .stream()
                .collect(Collectors.toMap(DepreciationSnapshot::getQuarter, s -> s));

        // 분기별로 "그 분기가 확정 상태인지"를 각각 판단한다 - 자산이 현재 불용/처분 등으로 바뀌어 지금은
        // 상각 제외 대상이더라도, 이미 확정된 과거 분기는 확정 당시 스냅샷(제외 여부 포함) 그대로 보여줘야
        // 하기 때문에 확정 여부와 무관하게 현재 상태만으로 전체 분기를 제외 처리하면 안 된다.
        List<DepreciationScheduleRowInfo> rows = new ArrayList<>();
        for (DepreciationQuarter quarter : DepreciationQuarter.values()) {
            DepreciationSnapshot snapshot = snapshotByQuarter.get(quarter);
            boolean isActiveConfirmed = snapshot != null && confirmationReader.findActiveByPeriod(fiscalYear, quarter).isPresent();

            if (isActiveConfirmed) {
                rows.add(snapshot.getExcludedReason() != null
                        ? DepreciationScheduleRowInfo.excludedFromSnapshot(fiscalYear, quarter)
                        : DepreciationScheduleRowInfo.fromSnapshot(fiscalYear, quarter, snapshot));
                continue;
            }

            Optional<DepreciationCalculator.ExcludedReason> excluded = DepreciationCalculator.excludedReason(asset, category);
            if (excluded.isPresent()) {
                rows.add(DepreciationScheduleRowInfo.excluded(fiscalYear, quarter));
            } else {
                YearMonth periodStart = YearMonth.of(fiscalYear, 1);
                YearMonth periodEnd = YearMonth.of(fiscalYear, quarter.getEndMonth());
                DepreciationCalculator.Result result = DepreciationCalculator.calculate(asset, category, periodStart, periodEnd);
                rows.add(DepreciationScheduleRowInfo.fromCalculation(fiscalYear, quarter, result));
            }
        }
        return rows;
    }

    /**
     * S-232: 결산 확정 - 그 시점 전체 자산(상각 대상 + 제외 자산 모두, 요구사항 2)의 표시값과 산출 결과를
     * 스냅샷으로 고정한다. 확정 이후 등록되는 자산은 이 배치에 포함되지 않으므로 자연히 확정 결과에서
     * 제외된다(요구사항 4).
     */
    @Transactional
    @Override
    public void confirm(int fiscalYear, DepreciationQuarter quarter) {
        if (confirmationReader.findActiveByPeriod(fiscalYear, quarter).isPresent()) {
            throw new InvalidParamException("이미 확정된 기간입니다. 다시 확정하려면 먼저 해제해주세요.");
        }

        Map<UUID, AssetCategory> categoryMap = loadCategoryMap();

        YearMonth periodStart = YearMonth.of(fiscalYear, 1);
        YearMonth periodEnd = YearMonth.of(fiscalYear, quarter.getEndMonth());

        List<DepreciationSnapshot> snapshots = new ArrayList<>();
        forEachAssetInBatches(asset -> {
            AssetCategory category = categoryMap.get(asset.getCategory().getId());
            Optional<DepreciationCalculator.ExcludedReason> excluded = DepreciationCalculator.excludedReason(asset, category);

            DepreciationSnapshot.DepreciationSnapshotBuilder snapshot = DepreciationSnapshot.builder()
                    .tangibleAsset(asset)
                    .fiscalYear(fiscalYear)
                    .quarter(quarter)
                    .assetCode(asset.getAssetCode())
                    .assetName(asset.getAssetName())
                    .categoryName(category != null ? category.getCategoryName() : null)
                    .acquisitionDate(asset.getAcquisitionDate())
                    .acquisitionAmount(asset.getAcquisitionAmount())
                    .usefulLifeMonths(category != null ? category.getUsefulLifeMonths() : null);

            if (excluded.isPresent()) {
                snapshots.add(snapshot.excludedReason(excluded.get()).bookValue(asset.getAcquisitionAmount()).build());
            } else {
                DepreciationCalculator.Result result = DepreciationCalculator.calculate(asset, category, periodStart, periodEnd);
                snapshots.add(snapshot
                        .openingAccumulated(result.getOpeningAccumulated())
                        .periodDepreciation(result.getPeriodDepreciation())
                        .closingAccumulated(result.getClosingAccumulated())
                        .bookValue(result.getBookValue())
                        .build());
            }
        });

        snapshotStore.storeAll(snapshots);
        try {
            confirmationStore.store(DepreciationConfirmation.builder()
                    .fiscalYear(fiscalYear)
                    .quarter(quarter)
                    .confirmedBy(loginUserContext.getUserId())
                    .build());
        } catch (DataIntegrityViolationException e) {
            // findActiveByPeriod 확인과 저장 사이에 동시에 확정 요청이 들어온 경우 - DB의 부분 유니크
            // 인덱스(fiscal_year, quarter WHERE released_at IS NULL)가 최종 방어선이 되어 중복 확정을 막는다.
            throw new InvalidParamException("이미 확정된 기간입니다. 다시 확정하려면 먼저 해제해주세요.");
        }
    }

    @Transactional
    @Override
    public void release(int fiscalYear, DepreciationQuarter quarter, String reason) {
        DepreciationConfirmation confirmation = confirmationReader.findActiveByPeriod(fiscalYear, quarter)
                .orElseThrow(() -> new InvalidParamException("확정된 기간이 아닙니다."));

        confirmation.release(loginUserContext.getUserId(), reason);
        snapshotStore.deleteByPeriod(fiscalYear, quarter);
    }

    @Override
    public List<DepreciationConfirmationInfo> loadConfirmationLog() {
        return confirmationReader.findAll().stream()
                .map(DepreciationConfirmationInfo::new)
                .collect(Collectors.toList());
    }

    private Map<UUID, AssetCategory> loadCategoryMap() {
        return assetCategoryReader.findAllByContainsKeyword(null).stream()
                .collect(Collectors.toMap(AssetCategory::getId, c -> c));
    }
}
