package com.winitech.smartAsset.domain.inventory;

import com.winitech.common.bean.LoginUserContext;
import com.winitech.common.exception.InvalidParamException;
import com.winitech.smartAsset.domain.assetCategory.AssetCategory;
import com.winitech.smartAsset.domain.depreciation.DepreciationCalculator;
import com.winitech.smartAsset.domain.tangibleAsset.TangibleAsset;
import com.winitech.smartAsset.domain.tangibleAsset.TangibleAssetReader;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Slf4j
@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class InventoryServiceImpl implements InventoryService {

    /** Q-32: 개인배정 자산만 임직원형 대상 - 공용·미배정(및 아직 Step6 미구현이라 실질 데이터가 없는 대여 관련 상태)은 전부 관리자형으로 */
    private static final Set<TangibleAsset.AssignType> MEMBER_TYPE_ASSIGN_TYPES = EnumSet.of(TangibleAsset.AssignType.PERSONAL);
    private static final Set<TangibleAsset.AssignType> ADMIN_TYPE_ASSIGN_TYPES = EnumSet.of(
            TangibleAsset.AssignType.UNASSIGNED, TangibleAsset.AssignType.SHARED,
            TangibleAsset.AssignType.LOANABLE, TangibleAsset.AssignType.ON_LOAN);

    private static final int DEFAULT_PAGE_SIZE = 20;
    private static final int MAX_PAGE_SIZE = 200;
    private static final Set<String> SORTABLE_FIELDS = Set.of("title", "createAt");
    private static final Sort DEFAULT_SORT = Sort.by(Sort.Direction.DESC, "createAt");

    private final InventoryReader inventoryReader;
    private final InventoryStore inventoryStore;
    private final InventoryTargetReader inventoryTargetReader;
    private final InventoryTargetStore inventoryTargetStore;
    private final InventoryResultReader inventoryResultReader;
    private final InventoryResultStore inventoryResultStore;
    private final InventoryInspectorReader inventoryInspectorReader;
    private final InventoryExcludedMemberReader inventoryExcludedMemberReader;
    private final TangibleAssetReader tangibleAssetReader;
    private final LoginUserContext loginUserContext;

    @Override
    public Page<InventoryInfo> loadInventoryList(Integer page, Integer size, String sort) {
        Pageable pageable = toPageable(page, size, sort);
        return inventoryReader.findAll(pageable)
                .map(inventory -> new InventoryInfo(inventory, inventoryTargetReader.countByInventoryId(inventory.getId())));
    }

    @Override
    public InventoryInfo loadInventory(UUID inventoryId) {
        Inventory inventory = inventoryReader.findById(inventoryId);
        return new InventoryInfo(inventory, inventoryTargetReader.countByInventoryId(inventoryId));
    }

    @Override
    public InventoryPreviewInfo previewMemberInventory(List<UUID> excludedMemberIds) {
        List<TangibleAsset> targets = computeMemberTargetAssets(excludedMemberIds);
        long participantCount = targets.stream().map(TangibleAsset::getCurrentMemberId).distinct().count();
        return new InventoryPreviewInfo(targets.size(), participantCount, findCarriedOver(Inventory.InventoryType.MEMBER, targets));
    }

    @Transactional
    @Override
    public UUID createMemberInventory(InventoryMemberCreateCommand command) {
        validateTitle(command.getTitle());
        List<UUID> excludedMemberIds = command.getExcludedMemberIds() == null ? List.of() : command.getExcludedMemberIds();

        List<TangibleAsset> targetAssets = computeMemberTargetAssets(excludedMemberIds);
        if (targetAssets.isEmpty()) {
            throw new InvalidParamException("대상이 될 개인배정 자산이 없습니다.");
        }

        Inventory inventory = Inventory.builder()
                .title(command.getTitle())
                .inventoryType(Inventory.InventoryType.MEMBER)
                .approvalRequired(command.getApprovalRequired())
                .allowNewAssetRegistration(command.getAllowNewAssetRegistration())
                .recurrenceRule(command.getRecurrenceRule())
                .createdBy(loginUserContext.getUserId())
                .build();
        inventoryStore.store(inventory);

        if (!excludedMemberIds.isEmpty()) {
            inventoryStore.storeExcludedMembers(excludedMemberIds.stream()
                    .map(memberId -> InventoryExcludedMember.builder().inventory(inventory).memberId(memberId).build())
                    .collect(Collectors.toList()));
        }

        createTargetsAndResults(inventory, targetAssets, TangibleAsset::getCurrentMemberId);

        return inventory.getId();
    }

    @Override
    public InventoryPreviewInfo previewAdminInventory() {
        List<TangibleAsset> targets = computeAdminTargetAssets();
        return new InventoryPreviewInfo(targets.size(), 0, findCarriedOver(Inventory.InventoryType.ADMIN, targets));
    }

    @Transactional
    @Override
    public UUID createAdminInventory(InventoryAdminCreateCommand command) {
        validateTitle(command.getTitle());
        List<UUID> inspectorMemberIds = command.getInspectorMemberIds() == null ? List.of() : command.getInspectorMemberIds();
        if (inspectorMemberIds.isEmpty()) {
            throw new InvalidParamException("검수자를 1명 이상 지정해주세요.");
        }

        List<TangibleAsset> targetAssets = computeAdminTargetAssets();
        if (targetAssets.isEmpty()) {
            throw new InvalidParamException("대상이 될 공용·미배정 자산이 없습니다.");
        }

        Inventory inventory = Inventory.builder()
                .title(command.getTitle())
                .inventoryType(Inventory.InventoryType.ADMIN)
                .approvalRequired(command.getApprovalRequired())
                .allowNewAssetRegistration(command.getAllowNewAssetRegistration())
                .recurrenceRule(command.getRecurrenceRule())
                .createdBy(loginUserContext.getUserId())
                .build();
        inventoryStore.store(inventory);

        inventoryStore.storeInspectors(inspectorMemberIds.stream()
                .map(memberId -> InventoryInspector.builder().inventory(inventory).memberId(memberId).build())
                .collect(Collectors.toList()));

        createTargetsAndResults(inventory, targetAssets, asset -> null);

        return inventory.getId();
    }

    @Override
    public InventoryProgressInfo loadProgress(UUID inventoryId) {
        List<InventoryResult> results = inventoryResultReader.findAllByInventoryId(inventoryId);

        long confirmed = countByStatus(results, InventoryResult.Status.CONFIRMED);
        long pending = countByStatus(results, InventoryResult.Status.PENDING_APPROVAL);
        long anomaly = countByStatus(results, InventoryResult.Status.ANOMALY);
        long unconfirmed = countByStatus(results, InventoryResult.Status.UNCONFIRMED);

        Map<UUID, List<InventoryResult>> byMember = results.stream()
                .filter(r -> r.getInventoryTarget().getMemberId() != null)
                .collect(Collectors.groupingBy(r -> r.getInventoryTarget().getMemberId()));

        List<InventoryProgressInfo.ParticipantSummary> participants = byMember.entrySet().stream()
                .map(entry -> {
                    List<InventoryResult> memberResults = entry.getValue();
                    long memberUnconfirmed = countByStatus(memberResults, InventoryResult.Status.UNCONFIRMED);
                    boolean participated = memberUnconfirmed < memberResults.size();
                    return new InventoryProgressInfo.ParticipantSummary(entry.getKey(), memberResults.size(), memberUnconfirmed, participated);
                })
                .collect(Collectors.toList());

        return new InventoryProgressInfo(confirmed, pending, anomaly, unconfirmed, results.size(), participants);
    }

    private long countByStatus(List<InventoryResult> results, InventoryResult.Status status) {
        return results.stream().filter(r -> r.getStatus() == status).count();
    }

    @Override
    public List<InventoryResultRowInfo> loadResultRows(UUID inventoryId, InventoryResult.Status statusFilter) {
        List<InventoryResult> results = statusFilter != null
                ? inventoryResultReader.findAllByInventoryIdAndStatus(inventoryId, statusFilter)
                : inventoryResultReader.findAllByInventoryId(inventoryId);

        return results.stream()
                .map(result -> new InventoryResultRowInfo(result, currentBookValue(result.getInventoryTarget().getTangibleAsset())))
                .collect(Collectors.toList());
    }

    @Transactional
    @Override
    public void approveResult(UUID inventoryTargetId) {
        InventoryResult result = inventoryResultReader.findByInventoryTargetId(inventoryTargetId);
        result.getInventoryTarget().getInventory().assertInProgress();
        result.approve(loginUserContext.getUserId());
        inventoryResultStore.store(result);
    }

    @Transactional
    @Override
    public void rejectResult(UUID inventoryTargetId, String reason) {
        InventoryResult result = inventoryResultReader.findByInventoryTargetId(inventoryTargetId);
        result.getInventoryTarget().getInventory().assertInProgress();
        result.reject(loginUserContext.getUserId(), reason);
        inventoryResultStore.store(result);
    }

    @Transactional
    @Override
    public void adminConfirmResult(UUID inventoryTargetId) {
        InventoryResult result = inventoryResultReader.findByInventoryTargetId(inventoryTargetId);
        Inventory inventory = result.getInventoryTarget().getInventory();
        inventory.assertInProgress();
        result.confirm(inventory.getApprovalRequired());
        inventoryResultStore.store(result);
    }

    @Transactional
    @Override
    public void adminReportAnomaly(UUID inventoryTargetId, InventoryResult.AnomalyType anomalyType, String note) {
        InventoryResult result = inventoryResultReader.findByInventoryTargetId(inventoryTargetId);
        result.getInventoryTarget().getInventory().assertInProgress();
        result.reportAnomaly(anomalyType, note);
        inventoryResultStore.store(result);
    }

    @Transactional
    @Override
    public void closeResult(UUID inventoryTargetId, InventoryResult.ClosureAction closureAction,
                             InventoryResult.ClosureReasonCode closureReasonCode, String note) {
        InventoryResult result = inventoryResultReader.findByInventoryTargetId(inventoryTargetId);
        result.getInventoryTarget().getInventory().assertInProgress();
        result.close(closureAction, closureReasonCode, note);
        inventoryResultStore.store(result);
    }

    @Transactional
    @Override
    public void closeResultsBulk(List<UUID> inventoryTargetIds, InventoryResult.ClosureAction closureAction,
                                  InventoryResult.ClosureReasonCode closureReasonCode, String note) {
        if (inventoryTargetIds == null || inventoryTargetIds.isEmpty()) {
            throw new InvalidParamException("종결 처리할 대상을 선택해주세요.");
        }
        // R6: 되돌리기 어려운 동작(분실 처리)은 일괄 처리를 허용하지 않는다
        if (closureAction == InventoryResult.ClosureAction.LOST) {
            throw new InvalidParamException("분실 처리는 한 건씩 개별로 진행해주세요.");
        }
        for (UUID inventoryTargetId : inventoryTargetIds) {
            closeResult(inventoryTargetId, closureAction, closureReasonCode, note);
        }
    }

    @Transactional
    @Override
    public void closeInventory(UUID inventoryId) {
        Inventory inventory = inventoryReader.findById(inventoryId);
        inventory.assertInProgress();

        List<InventoryResult> unconfirmed = inventoryResultReader.findAllByInventoryIdAndStatus(inventoryId, InventoryResult.Status.UNCONFIRMED);
        boolean hasUnprocessed = unconfirmed.stream().anyMatch(r -> r.getClosureAction() == null);
        if (hasUnprocessed) {
            throw new InvalidParamException("미확인 자산 중 종결 처리가 안 된 항목이 남아있습니다.");
        }

        inventory.close(loginUserContext.getUserId());
        inventoryStore.store(inventory);
    }

    /** 현재 시점(또는 불용/처분 상태라면 그 동결 시점) 기준 장부가 - disposal_asset 처리 시와 동일한 계산 방식 */
    private BigDecimal currentBookValue(TangibleAsset asset) {
        AssetCategory category = asset.getCategory();
        if (DepreciationCalculator.excludedReason(asset, category).isPresent()) {
            return asset.getAcquisitionAmount();
        }
        YearMonth now = YearMonth.now();
        DepreciationCalculator.Result result = DepreciationCalculator.calculate(asset, category, YearMonth.of(now.getYear(), 1), now);
        return result.getBookValue();
    }

    private void createTargetsAndResults(Inventory inventory, List<TangibleAsset> targetAssets,
                                          java.util.function.Function<TangibleAsset, UUID> memberIdResolver) {
        List<InventoryTarget> targets = targetAssets.stream()
                .map(asset -> InventoryTarget.builder()
                        .inventory(inventory)
                        .tangibleAsset(asset)
                        .memberId(memberIdResolver.apply(asset))
                        .expectedLocationId(asset.getLocation() != null ? asset.getLocation().getId() : null)
                        .expectedAssignType(asset.getAssignType())
                        .build())
                .collect(Collectors.toList());
        List<InventoryTarget> storedTargets = inventoryTargetStore.storeAll(targets);

        List<InventoryResult> results = storedTargets.stream()
                .map(target -> InventoryResult.builder().inventoryTarget(target).build())
                .collect(Collectors.toList());
        inventoryResultStore.storeAll(results);
    }

    private List<TangibleAsset> computeMemberTargetAssets(List<UUID> excludedMemberIds) {
        List<TangibleAsset> assets = tangibleAssetReader.findAllByAssignTypeInAndLifeStatus(
                MEMBER_TYPE_ASSIGN_TYPES, TangibleAsset.LifeStatus.USE);
        if (excludedMemberIds == null || excludedMemberIds.isEmpty()) {
            return assets;
        }
        Set<UUID> excluded = Set.copyOf(excludedMemberIds);
        return assets.stream()
                .filter(asset -> asset.getCurrentMemberId() == null || !excluded.contains(asset.getCurrentMemberId()))
                .collect(Collectors.toList());
    }

    private List<TangibleAsset> computeAdminTargetAssets() {
        return tangibleAssetReader.findAllByAssignTypeInAndLifeStatus(ADMIN_TYPE_ASSIGN_TYPES, TangibleAsset.LifeStatus.USE);
    }

    /** S-306 이월 추적 - 같은 유형의 직전 종료된 조사에서 이번 대상 자산 중 몇 건이 차기이월로 넘어왔는지 */
    private List<UUID> findCarriedOver(Inventory.InventoryType inventoryType, List<TangibleAsset> targetAssets) {
        Optional<Inventory> previous = inventoryReader.findMostRecentClosedByType(inventoryType);
        if (previous.isEmpty()) {
            return List.of();
        }
        UUID previousId = previous.get().getId();
        List<UUID> carriedOver = new ArrayList<>();
        for (TangibleAsset asset : targetAssets) {
            if (inventoryTargetReader.existsCarriedOverInPreviousInventory(previousId, asset.getId())) {
                carriedOver.add(asset.getId());
            }
        }
        return carriedOver;
    }

    @Override
    public InventoryReportInfo loadReport(UUID inventoryId) {
        Inventory inventory = inventoryReader.findById(inventoryId);
        List<InventoryResult> results = inventoryResultReader.findAllByInventoryId(inventoryId);

        long confirmed = countByStatus(results, InventoryResult.Status.CONFIRMED);
        long pending = countByStatus(results, InventoryResult.Status.PENDING_APPROVAL);
        long anomaly = countByStatus(results, InventoryResult.Status.ANOMALY);
        long unconfirmed = countByStatus(results, InventoryResult.Status.UNCONFIRMED);

        Map<UUID, List<InventoryResult>> byMember = results.stream()
                .filter(r -> r.getInventoryTarget().getMemberId() != null)
                .collect(Collectors.groupingBy(r -> r.getInventoryTarget().getMemberId()));
        List<InventoryProgressInfo.ParticipantSummary> participants = byMember.entrySet().stream()
                .map(entry -> {
                    List<InventoryResult> memberResults = entry.getValue();
                    long memberUnconfirmed = countByStatus(memberResults, InventoryResult.Status.UNCONFIRMED);
                    boolean participated = memberUnconfirmed < memberResults.size();
                    return new InventoryProgressInfo.ParticipantSummary(entry.getKey(), memberResults.size(), memberUnconfirmed, participated);
                })
                .collect(Collectors.toList());

        List<InventoryResult> closed = results.stream().filter(r -> r.getClosureAction() != null).collect(Collectors.toList());
        Map<String, Long> closureCounts = closed.stream()
                .collect(Collectors.groupingBy(
                        r -> r.getClosureAction().name() + ":" + r.getClosureReasonCode().name(),
                        Collectors.counting()));
        List<InventoryReportInfo.ClosureBreakdownItem> closureBreakdown = closed.stream()
                .map(r -> r.getClosureAction().name() + ":" + r.getClosureReasonCode().name())
                .distinct()
                .map(key -> {
                    String[] parts = key.split(":");
                    return new InventoryReportInfo.ClosureBreakdownItem(
                            InventoryResult.ClosureAction.valueOf(parts[0]),
                            InventoryResult.ClosureReasonCode.valueOf(parts[1]),
                            closureCounts.get(key));
                })
                .collect(Collectors.toList());

        List<InventoryReportInfo.LostItem> lostItems = closed.stream()
                .filter(r -> r.getClosureAction() == InventoryResult.ClosureAction.LOST)
                .map(r -> {
                    TangibleAsset asset = r.getInventoryTarget().getTangibleAsset();
                    return new InventoryReportInfo.LostItem(
                            asset.getId(), asset.getAssetCode(), asset.getAssetName(),
                            currentBookValue(asset), r.getClosureNote());
                })
                .collect(Collectors.toList());

        Map<InventoryResult.AnomalyType, Long> anomalyCounts = results.stream()
                .filter(r -> r.getStatus() == InventoryResult.Status.ANOMALY)
                .collect(Collectors.groupingBy(InventoryResult::getAnomalyType, Collectors.counting()));
        List<InventoryReportInfo.AnomalyBreakdownItem> anomalyBreakdown = anomalyCounts.entrySet().stream()
                .map(entry -> new InventoryReportInfo.AnomalyBreakdownItem(entry.getKey(), entry.getValue()))
                .collect(Collectors.toList());

        return new InventoryReportInfo(inventory, results.size(), confirmed, pending, anomaly, unconfirmed,
                participants, closureBreakdown, lostItems, anomalyBreakdown);
    }

    @Override
    public List<InventoryScheduleInfo> loadSchedules() {
        List<InventoryScheduleInfo> schedules = new ArrayList<>();
        for (Inventory.InventoryType type : Inventory.InventoryType.values()) {
            Optional<Inventory> lastClosed = inventoryReader.findMostRecentClosedByType(type);
            if (lastClosed.isEmpty() || lastClosed.get().getRecurrenceRule() == null) {
                continue;
            }
            Inventory inventory = lastClosed.get();
            boolean nextCycleInProgress = inventoryReader.existsInProgressByType(type);
            OffsetDateTime nextDueDate = inventory.getRecurrenceRule().nextDueDate(inventory.getClosedAt());
            schedules.add(new InventoryScheduleInfo(inventory, nextDueDate, nextCycleInProgress));
        }
        return schedules;
    }

    @Override
    public InventoryCloneTemplateInfo loadCloneTemplate(UUID inventoryId) {
        Inventory source = inventoryReader.findById(inventoryId);
        List<UUID> excludedMemberIds = source.getInventoryType() == Inventory.InventoryType.MEMBER
                ? inventoryExcludedMemberReader.findAllByInventoryId(inventoryId).stream()
                        .map(InventoryExcludedMember::getMemberId).collect(Collectors.toList())
                : List.of();
        List<UUID> inspectorMemberIds = source.getInventoryType() == Inventory.InventoryType.ADMIN
                ? inventoryInspectorReader.findAllByInventoryId(inventoryId).stream()
                        .map(InventoryInspector::getMemberId).collect(Collectors.toList())
                : List.of();
        return new InventoryCloneTemplateInfo(source, excludedMemberIds, inspectorMemberIds);
    }

    @Override
    public InventoryMyStatusInfo loadMyStatus() {
        UUID myId = loginUserContext.getUserId();
        List<InventoryResult> results = inventoryResultReader.findAllByMemberIdAndInventoryStatus(myId, Inventory.Status.IN_PROGRESS);
        if (results.isEmpty()) {
            return InventoryMyStatusInfo.none();
        }

        Inventory inventory = results.get(0).getInventoryTarget().getInventory();
        List<InventoryResultRowInfo> rows = results.stream()
                .map(result -> new InventoryResultRowInfo(result, currentBookValue(result.getInventoryTarget().getTangibleAsset())))
                .collect(Collectors.toList());
        return new InventoryMyStatusInfo(true, inventory.getId(), inventory.getTitle(), inventory.getApprovalRequired(), rows);
    }

    private InventoryResult findOwnResult(UUID inventoryTargetId) {
        InventoryResult result = inventoryResultReader.findByInventoryTargetId(inventoryTargetId);
        UUID myId = loginUserContext.getUserId();
        UUID ownerId = result.getInventoryTarget().getMemberId();
        if (ownerId == null || !ownerId.equals(myId)) {
            throw new InvalidParamException("본인에게 배정된 자산만 확인할 수 있습니다.");
        }
        result.getInventoryTarget().getInventory().assertInProgress();
        return result;
    }

    @Transactional
    @Override
    public void selfConfirmResult(UUID inventoryTargetId) {
        InventoryResult result = findOwnResult(inventoryTargetId);
        result.confirm(result.getInventoryTarget().getInventory().getApprovalRequired());
        inventoryResultStore.store(result);
    }

    @Transactional
    @Override
    public void selfConfirmResultWithoutScan(UUID inventoryTargetId, UUID photoFileId,
                                              java.time.OffsetDateTime capturedAt, java.time.OffsetDateTime uploadedAt) {
        InventoryResult result = findOwnResult(inventoryTargetId);
        result.confirmWithoutScan(photoFileId, capturedAt, uploadedAt);
        inventoryResultStore.store(result);
    }

    @Transactional
    @Override
    public void selfReportWrongHolder(UUID inventoryTargetId, String note) {
        InventoryResult result = findOwnResult(inventoryTargetId);
        result.reportAnomaly(InventoryResult.AnomalyType.WRONG_HOLDER, note);
        inventoryResultStore.store(result);
    }

    private void validateTitle(String title) {
        if (!StringUtils.hasText(title)) {
            throw new InvalidParamException("조사명은 필수입니다.");
        }
    }

    private Pageable toPageable(Integer page, Integer size, String sort) {
        int safePage = (page == null || page < 0) ? 0 : page;
        int safeSize = (size == null) ? DEFAULT_PAGE_SIZE : Math.max(1, Math.min(size, MAX_PAGE_SIZE));
        return PageRequest.of(safePage, safeSize, resolveSort(sort));
    }

    private Sort resolveSort(String sort) {
        if (!StringUtils.hasText(sort)) {
            return DEFAULT_SORT;
        }
        String[] parts = sort.split(",", 2);
        String field = parts[0].trim();
        if (!SORTABLE_FIELDS.contains(field)) {
            return DEFAULT_SORT;
        }
        Sort.Direction direction = (parts.length > 1 && "desc".equalsIgnoreCase(parts[1].trim()))
                ? Sort.Direction.DESC : Sort.Direction.ASC;
        return Sort.by(direction, field);
    }
}
