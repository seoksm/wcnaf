package com.winitech.smartAsset.domain.tangibleAsset;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.winitech.common.bean.LoginUserContext;
import com.winitech.common.exception.InvalidParamException;
import com.winitech.smartAsset.domain.assetAssignment.AssetAssignment;
import com.winitech.smartAsset.domain.assetAssignment.AssetAssignmentInfo;
import com.winitech.smartAsset.domain.assetAssignment.AssetAssignmentReader;
import com.winitech.smartAsset.domain.assetAssignment.AssetAssignmentStore;
import com.winitech.smartAsset.domain.assetCategory.AssetCategory;
import com.winitech.smartAsset.domain.assetCategory.AssetCategoryReader;
import com.winitech.smartAsset.domain.assetHistory.AssetHistory;
import com.winitech.smartAsset.domain.assetHistory.AssetHistoryFilter;
import com.winitech.smartAsset.domain.assetHistory.AssetHistoryInfo;
import com.winitech.smartAsset.domain.assetHistory.AssetHistoryReader;
import com.winitech.smartAsset.domain.assetHistory.AssetHistoryStore;
import com.winitech.smartAsset.domain.assetLocation.AssetLocation;
import com.winitech.smartAsset.domain.assetLocation.AssetLocationReader;
import com.winitech.smartAsset.domain.depreciation.DepreciationCalculator;
import com.winitech.smartAsset.domain.disposalAsset.DisposalAsset;
import com.winitech.smartAsset.domain.disposalAsset.DisposalAssetReader;
import com.winitech.smartAsset.domain.disposalAsset.DisposalAssetRowInfo;
import com.winitech.smartAsset.domain.disposalAsset.DisposalAssetStore;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.egovframe.rte.fdl.cmmn.EgovAbstractServiceImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Page;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Slf4j
@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class TangibleAssetServiceImpl extends EgovAbstractServiceImpl implements TangibleAssetService {

    private final TangibleAssetReader tangibleAssetReader;
    private final TangibleAssetStore tangibleAssetStore;
    private final AssetCategoryReader assetCategoryReader;
    private final AssetLocationReader assetLocationReader;
    private final AssetAssignmentReader assetAssignmentReader;
    private final AssetAssignmentStore assetAssignmentStore;
    private final AssetHistoryReader assetHistoryReader;
    private final AssetHistoryStore assetHistoryStore;
    private final LoginUserContext loginUserContext;
    private final ObjectMapper objectMapper;
    private final AssetCodeGenerator assetCodeGenerator;
    private final DisposalAssetReader disposalAssetReader;
    private final DisposalAssetStore disposalAssetStore;

    @Transactional
    @Override
    public UUID createTangibleAsset(TangibleAssetCommand command) {
        return createTangibleAsset(command, null);
    }

    @Transactional
    @Override
    public UUID createTangibleAsset(TangibleAssetCommand command, UUID batchId) {

        AssetCategory category = assetCategoryReader.findById(command.getCategoryId());
        AssetLocation location = assetLocationReader.findById(command.getLocationId());

        String assetCode = assetCodeGenerator.generate();

        TangibleAsset tangibleAsset = command.toEntity(assetCode, category, location);
        UUID tangibleAssetId = tangibleAssetStore.store(tangibleAsset);

        if (TangibleAsset.requiresMember(tangibleAsset.getAssignType()) && tangibleAsset.getCurrentMemberId() != null) {
            openAssignment(tangibleAsset, tangibleAsset.getCurrentMemberId(), tangibleAsset.getAssignType());
        }

        recordRegisterHistory(tangibleAsset, batchId);

        return tangibleAssetId;
    }

    @Transactional
    @Override
    public void updateTangibleAsset(TangibleAssetCommand.UpdateCommand updateCommand) {
        updateTangibleAsset(updateCommand, null);
    }

    @Transactional
    @Override
    public void updateTangibleAsset(TangibleAssetCommand.UpdateCommand updateCommand, UUID batchId) {

        TangibleAsset tangibleAsset = tangibleAssetReader.findById(updateCommand.getTangibleAssetId());
        AssetCategory category = assetCategoryReader.findById(updateCommand.getCategoryId());
        AssetLocation location = assetLocationReader.findById(updateCommand.getLocationId());

        applyUpdate(tangibleAsset, updateCommand, category, location, batchId);
    }

    /** 단일/일괄 수정이 공유하는 배정 이력·변경 이력 갱신 로직 (Q-16, §2) */
    private void applyUpdate(TangibleAsset tangibleAsset, TangibleAssetCommand.UpdateCommand updateCommand,
                              AssetCategory category, AssetLocation location, UUID batchId) {
        // preview(예: 엑셀 업서트) 시점 이후 이 자산이 이미 바뀌었는지 여기서, 즉 실제로 반영하기
        // 직전에 다시 읽은 tangibleAsset 기준으로 확인한다. preview~commit 사이(TOCTOU)에 값이
        // 바뀐 경우를 여기서 잡고, 이 확인 직후부터 실제 UPDATE까지의 아주 짧은 구간은 TangibleAsset의
        // @Version 낙관적 잠금이 그대로 커버한다(다른 요청이 먼저 커밋하면 이후 flush에서 자동 충돌).
        Long expectedVersion = updateCommand.getExpectedVersion();
        if (expectedVersion != null && !expectedVersion.equals(tangibleAsset.getVersion())) {
            throw new ObjectOptimisticLockingFailureException(TangibleAsset.class, tangibleAsset.getId());
        }

        Map<String, String> before = tangibleAsset.diffableFields();
        boolean lifeStatusChanged = tangibleAsset.getLifeStatus() != updateCommand.getLifeStatus();

        boolean assignmentChanged = tangibleAssetStore.modify(tangibleAsset, updateCommand, category, location);

        if (assignmentChanged) {
            assetAssignmentReader.findCurrentByTangibleAssetId(tangibleAsset.getId())
                    .ifPresent(assetAssignmentStore::release);

            if (TangibleAsset.requiresMember(tangibleAsset.getAssignType()) && tangibleAsset.getCurrentMemberId() != null) {
                openAssignment(tangibleAsset, tangibleAsset.getCurrentMemberId(), tangibleAsset.getAssignType());
            }
        }

        recordModifyHistory(tangibleAsset, before, lifeStatusChanged, assignmentChanged, batchId);
    }

    private void openAssignment(TangibleAsset tangibleAsset, UUID memberId, TangibleAsset.AssignType assignType) {
        AssetAssignment assetAssignment = AssetAssignment.builder()
                .tangibleAsset(tangibleAsset)
                .memberId(memberId)
                .assignType(assignType.name())
                .assignedBy(loginUserContext.getUserId())
                .build();

        assetAssignmentStore.store(assetAssignment);
    }

    @Override
    public Page<TangibleAssetInfo> loadTangibleAssetList(String keyword, Integer page, Integer size, String sort) {
        Pageable pageable = toPageable(page, size, sort);
        return tangibleAssetReader.findAllByContainsKeyword(keyword, pageable).map(TangibleAssetInfo::new);
    }

    private static final int DEFAULT_PAGE_SIZE = 20;
    private static final int MAX_PAGE_SIZE = 200;
    /** JPA 프로퍼티 경로 조작(PropertyReferenceException)이나 임의 정렬을 막기 위한 허용 필드 화이트리스트 */
    private static final Set<String> SORTABLE_FIELDS = Set.of(
            "assetCode", "assetName", "acquisitionDate", "acquisitionAmount", "createAt");
    private static final Sort DEFAULT_SORT = Sort.by(Sort.Direction.DESC, "createAt");

    /**
     * page/size/sort 요청 파라미터를 안전한 Pageable로 변환한다.
     * - page: 음수면 0으로 보정
     * - size: 비어있으면 기본값(20), 1 미만이거나 최댓값(200) 초과면 각각 1/200으로 clamp
     * - sort: "필드,방향" 형식만 허용하고, 화이트리스트에 없는 필드나 형식이 잘못된 값은 기본 정렬로 대체
     */
    private Pageable toPageable(Integer page, Integer size, String sort) {
        int safePage = (page == null || page < 0) ? 0 : page;

        int safeSize = (size == null) ? DEFAULT_PAGE_SIZE : size;
        safeSize = Math.max(1, Math.min(safeSize, MAX_PAGE_SIZE));

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

    @Override
    public TangibleAssetInfo loadTangibleAsset(UUID tangibleAssetId) {
        return new TangibleAssetInfo(tangibleAssetReader.findById(tangibleAssetId));
    }

    @Override
    public List<AssetAssignmentInfo> loadAssignmentHistory(UUID tangibleAssetId) {
        return assetAssignmentReader.findAllByTangibleAssetId(tangibleAssetId).stream()
                .map(AssetAssignmentInfo::new)
                .collect(Collectors.toList());
    }

    @Transactional
    @Override
    public void releaseAssignment(UUID tangibleAssetId) {
        TangibleAsset tangibleAsset = tangibleAssetReader.findById(tangibleAssetId);
        Map<String, String> before = tangibleAsset.diffableFields();

        tangibleAsset.releaseAssignment();

        AssetAssignment current = assetAssignmentReader.findCurrentByTangibleAssetId(tangibleAssetId)
                .orElseThrow(() -> new InvalidParamException("현재 배정된 이력을 찾을 수 없습니다."));
        assetAssignmentStore.release(current);

        recordModifyHistory(tangibleAsset, before, false, true, null);
    }

    /** S-215: 지정한 필드만 여러 자산에 한 번에 적용 (전체 성공 또는 전체 롤백) */
    @Transactional
    @Override
    public void batchUpdateTangibleAsset(TangibleAssetCommand.BatchUpdateCommand batchUpdateCommand) {
        if (batchUpdateCommand.getTangibleAssetIds() == null || batchUpdateCommand.getTangibleAssetIds().isEmpty()) {
            throw new InvalidParamException("일괄 변경할 자산을 선택해주세요.");
        }

        UUID batchId = UUID.randomUUID();

        for (UUID tangibleAssetId : batchUpdateCommand.getTangibleAssetIds()) {
            TangibleAsset tangibleAsset = tangibleAssetReader.findById(tangibleAssetId);

            UUID categoryId = batchUpdateCommand.getCategoryId() != null
                    ? batchUpdateCommand.getCategoryId() : tangibleAsset.getCategory().getId();
            UUID locationId = batchUpdateCommand.getLocationId() != null
                    ? batchUpdateCommand.getLocationId() : tangibleAsset.getLocation().getId();
            AssetCategory category = assetCategoryReader.findById(categoryId);
            AssetLocation location = assetLocationReader.findById(locationId);

            TangibleAsset.LifeStatus lifeStatus = batchUpdateCommand.getLifeStatus() != null
                    ? batchUpdateCommand.getLifeStatus() : tangibleAsset.getLifeStatus();
            TangibleAsset.AssignType assignType = batchUpdateCommand.getAssignType() != null
                    ? batchUpdateCommand.getAssignType() : tangibleAsset.getAssignType();
            UUID currentMemberId = batchUpdateCommand.getCurrentMemberId() != null
                    ? batchUpdateCommand.getCurrentMemberId() : tangibleAsset.getCurrentMemberId();

            TangibleAssetCommand.UpdateCommand updateCommand = TangibleAssetCommand.UpdateCommand.builder()
                    .tangibleAssetId(tangibleAssetId)
                    .assetName(tangibleAsset.getAssetName())
                    .categoryId(categoryId)
                    .locationId(locationId)
                    .lifeStatus(lifeStatus)
                    .assignType(assignType)
                    .acquisitionDate(tangibleAsset.getAcquisitionDate())
                    .acquisitionAmount(tangibleAsset.getAcquisitionAmount())
                    .modelName(tangibleAsset.getModelName())
                    .manufacturer(tangibleAsset.getManufacturer())
                    .serialNo(tangibleAsset.getSerialNo())
                    .currentMemberId(currentMemberId)
                    .memo(tangibleAsset.getMemo())
                    .build();

            applyUpdate(tangibleAsset, updateCommand, category, location, batchId);
        }
    }

    /** S-214: 자산을 최대 100건까지 복제, 복제본은 배정 이력을 물려받지 않고 미배정으로 시작 */
    @Transactional
    @Override
    public List<UUID> duplicateTangibleAsset(UUID tangibleAssetId, int count) {
        if (count < 1 || count > 100) {
            throw new InvalidParamException("복제 개수는 1~100건이어야 합니다.");
        }

        TangibleAsset source = tangibleAssetReader.findById(tangibleAssetId);
        UUID batchId = UUID.randomUUID();
        List<UUID> createdIds = new ArrayList<>();

        for (int i = 0; i < count; i++) {
            TangibleAsset copy = TangibleAsset.builder()
                    .assetCode(assetCodeGenerator.generate())
                    .assetName(source.getAssetName())
                    .category(source.getCategory())
                    .location(source.getLocation())
                    .lifeStatus(source.getLifeStatus())
                    .assignType(TangibleAsset.AssignType.UNASSIGNED)
                    .acquisitionDate(source.getAcquisitionDate())
                    .acquisitionAmount(source.getAcquisitionAmount())
                    .modelName(source.getModelName())
                    .manufacturer(source.getManufacturer())
                    .serialNo(null)
                    .memo(source.getMemo())
                    .build();

            tangibleAssetStore.store(copy);
            recordRegisterHistory(copy, batchId);
            createdIds.add(copy.getId());
        }

        return createdIds;
    }

    @Override
    public List<AssetHistoryInfo> loadHistory(UUID tangibleAssetId) {
        return assetHistoryReader.findAllByTangibleAssetId(tangibleAssetId).stream()
                .map(AssetHistoryInfo::new)
                .collect(Collectors.toList());
    }

    @Override
    public List<AssetHistoryInfo> loadActivityLog(AssetHistoryFilter filter, int page) {
        return assetHistoryReader.findRecent(filter, page).stream()
                .map(AssetHistoryInfo::new)
                .collect(Collectors.toList());
    }

    /** S-241: 불용 처리. reason은 자산 자체(memo)는 건드리지 않고 이력에만 남긴다 - 기존 메모를
     * 실수로 덮어쓰지 않기 위함이다. */
    @Transactional
    @Override
    public void disuseTangibleAsset(UUID tangibleAssetId, String reason) {
        TangibleAsset tangibleAsset = tangibleAssetReader.findById(tangibleAssetId);
        boolean assignmentChanged = tangibleAsset.disuse();

        if (assignmentChanged) {
            assetAssignmentReader.findCurrentByTangibleAssetId(tangibleAssetId).ifPresent(assetAssignmentStore::release);
        }

        List<Map<String, String>> changes = new ArrayList<>();
        changes.add(change("생애상태", null, TangibleAsset.LifeStatus.DISUSE.getDescription()));
        if (StringUtils.hasText(reason)) {
            changes.add(change("불용사유", null, reason));
        }
        storeHistory(tangibleAsset, AssetHistory.HistoryType.DISUSE, changes, tangibleAsset.diffableFields(), null);
    }

    /** S-240: 불용 → 사용 복귀 (Q-28) */
    @Transactional
    @Override
    public void restoreTangibleAsset(UUID tangibleAssetId) {
        TangibleAsset tangibleAsset = tangibleAssetReader.findById(tangibleAssetId);
        tangibleAsset.restoreFromDisuse();

        List<Map<String, String>> changes = List.of(
                change("생애상태", TangibleAsset.LifeStatus.DISUSE.getDescription(), TangibleAsset.LifeStatus.USE.getDescription()));
        storeHistory(tangibleAsset, AssetHistory.HistoryType.STATUS_CHANGE, changes, tangibleAsset.diffableFields(), null);
    }

    /**
     * S-242: 처분 처리. 처분 시점 장부가는 DepreciationCalculator로 계산한다 - 자산이 이미 불용
     * 상태라 그 시점(lifeStatusChangedAt)에 동결돼 있으므로, "지금" 계산해도 불용 시점 값과 같다.
     */
    @Transactional
    @Override
    public UUID disposeTangibleAsset(UUID tangibleAssetId, TangibleAssetDisposalCommand command) {
        TangibleAsset tangibleAsset = tangibleAssetReader.findById(tangibleAssetId);
        AssetCategory category = tangibleAsset.getCategory() != null
                ? assetCategoryReader.findById(tangibleAsset.getCategory().getId()) : null;
        BigDecimal bookValue = currentBookValue(tangibleAsset, category);

        tangibleAsset.dispose();

        DisposalAsset disposalAsset = DisposalAsset.builder()
                .tangibleAsset(tangibleAsset)
                .bookValueAtDisposal(bookValue)
                .disposalReasonCode(command.getDisposalReasonCode())
                .disposalAmount(command.getDisposalAmount())
                .counterparty(command.getCounterparty())
                .memo(command.getMemo())
                .disposedBy(loginUserContext.getUserId())
                .build();
        disposalAssetStore.store(disposalAsset);

        List<Map<String, String>> changes = List.of(
                change("생애상태", TangibleAsset.LifeStatus.DISUSE.getDescription(), TangibleAsset.LifeStatus.DISPOSED.getDescription()),
                change("처분사유", null, command.getDisposalReasonCode().getDescription()));
        storeHistory(tangibleAsset, AssetHistory.HistoryType.DISPOSAL, changes, tangibleAsset.diffableFields(), null);

        return disposalAsset.getId();
    }

    /** S-240: 불용자산 목록. filter가 null이면 불용+처분완료 전체 */
    @Override
    public Page<DisposalAssetRowInfo> loadDisposalList(TangibleAsset.LifeStatus lifeStatusFilter, Integer page, Integer size, String sort) {
        List<TangibleAsset.LifeStatus> lifeStatuses = lifeStatusFilter != null
                ? List.of(lifeStatusFilter)
                : List.of(TangibleAsset.LifeStatus.DISUSE, TangibleAsset.LifeStatus.DISPOSED);

        Pageable pageable = toPageable(page, size, sort);
        Map<UUID, AssetCategory> categoryMap = loadCategoryMap();

        return tangibleAssetReader.findByLifeStatusIn(lifeStatuses, pageable).map(asset -> {
            AssetCategory category = asset.getCategory() != null ? categoryMap.get(asset.getCategory().getId()) : null;
            DisposalAsset disposalAsset = disposalAssetReader.findByTangibleAssetId(asset.getId()).orElse(null);
            BigDecimal bookValue = disposalAsset != null
                    ? disposalAsset.getBookValueAtDisposal()
                    : currentBookValue(asset, category);
            return new DisposalAssetRowInfo(asset, category, disposalAsset, bookValue);
        });
    }

    private Map<UUID, AssetCategory> loadCategoryMap() {
        return assetCategoryReader.findAllByContainsKeyword(null).stream()
                .collect(Collectors.toMap(AssetCategory::getId, c -> c));
    }

    /** 현재 시점(또는 불용/처분 상태라면 그 동결 시점) 기준 장부가 - S-240 목록·처분 처리(장부가 고정)에서 함께 쓴다 */
    private BigDecimal currentBookValue(TangibleAsset asset, AssetCategory category) {
        if (DepreciationCalculator.excludedReason(asset, category).isPresent()) {
            return asset.getAcquisitionAmount();
        }
        YearMonth now = YearMonth.now();
        DepreciationCalculator.Result result = DepreciationCalculator.calculate(asset, category, YearMonth.of(now.getYear(), 1), now);
        return result.getBookValue();
    }

    /** 신규 등록은 모든 필드를 before=null → after=값 형태의 변경분으로 기록한다 */
    private void recordRegisterHistory(TangibleAsset tangibleAsset, UUID batchId) {
        Map<String, String> after = tangibleAsset.diffableFields();
        List<Map<String, String>> changes = new ArrayList<>();
        for (Map.Entry<String, String> entry : after.entrySet()) {
            changes.add(change(entry.getKey(), null, entry.getValue()));
        }
        storeHistory(tangibleAsset, AssetHistory.HistoryType.REGISTER, changes, after, batchId);
    }

    /** 실제로 바뀐 필드만 골라 정보수정/상태변경/배정 중 하나로 분류해 기록한다 (§2) */
    private void recordModifyHistory(TangibleAsset tangibleAsset, Map<String, String> before,
                                      boolean lifeStatusChanged, boolean assignmentChanged, UUID batchId) {
        Map<String, String> after = tangibleAsset.diffableFields();
        List<Map<String, String>> changes = new ArrayList<>();
        for (String field : after.keySet()) {
            String beforeValue = before.get(field);
            String afterValue = after.get(field);
            if (!Objects.equals(beforeValue, afterValue)) {
                changes.add(change(field, beforeValue, afterValue));
            }
        }
        if (changes.isEmpty()) {
            return;
        }

        AssetHistory.HistoryType historyType;
        if (lifeStatusChanged) {
            // 어떤 경로로든(일반 수정·일괄변경·엑셀 업서트·복귀) 생애상태가 불용/처분완료로 바뀌면
            // 일반 상태변경이 아니라 그 사유를 정확히 남긴다(불용/처분 자산 목록·감사 추적용).
            historyType = tangibleAsset.getLifeStatus() == TangibleAsset.LifeStatus.DISUSE ? AssetHistory.HistoryType.DISUSE
                    : tangibleAsset.getLifeStatus() == TangibleAsset.LifeStatus.DISPOSED ? AssetHistory.HistoryType.DISPOSAL
                    : AssetHistory.HistoryType.STATUS_CHANGE;
        } else {
            historyType = assignmentChanged ? AssetHistory.HistoryType.ASSIGNMENT : AssetHistory.HistoryType.MODIFY;
        }

        // 불용·처분·배정은 감사 시 그 시점 전체 상태가 필요하므로 스냅샷을 함께 남긴다 (Q-23)
        boolean withSnapshot = historyType == AssetHistory.HistoryType.STATUS_CHANGE
                || historyType == AssetHistory.HistoryType.ASSIGNMENT;

        storeHistory(tangibleAsset, historyType, changes, withSnapshot ? after : null, batchId);
    }

    private Map<String, String> change(String field, String before, String after) {
        Map<String, String> change = new LinkedHashMap<>();
        change.put("field", field);
        change.put("before", before);
        change.put("after", after);
        return change;
    }

    /**
     * 자산 변경 이력을 함께 저장한다. 이 메서드는 항상 자산 등록/수정과 같은 트랜잭션 안에서
     * 호출되므로(§ 클래스 상단 @Transactional 메서드 참고), 여기서 던진 예외는 그 트랜잭션 전체를
     * 롤백시켜 "자산은 바뀌었는데 이력만 없는" 상태를 방지한다 - 예전에는 직렬화 실패를 로그만
     * 남기고 삼켜서 이력 없이 자산 변경만 조용히 커밋되는 문제가 있었다.
     */
    private void storeHistory(TangibleAsset tangibleAsset, AssetHistory.HistoryType historyType,
                               List<Map<String, String>> changes, Map<String, String> snapshot, UUID batchId) {
        String changedFieldsJson;
        String snapshotJson;
        try {
            changedFieldsJson = objectMapper.writeValueAsString(changes);
            snapshotJson = snapshot != null ? objectMapper.writeValueAsString(snapshot) : null;
        } catch (JsonProcessingException e) {
            // 실제 변경 내용(changes/snapshot)에는 자산명·메모 등 개인정보성 값이 섞여 있을 수 있어
            // 로그에 남기지 않는다. 추적에 필요한 식별자만 남긴다.
            log.error("자산 히스토리 직렬화 실패 (assetId={}, historyType={}, batchId={}): {}",
                    tangibleAsset.getId(), historyType, batchId, e.getMessage());
            throw new RuntimeException("자산 변경 이력을 생성하는 중 오류가 발생했습니다.", e);
        }

        AssetHistory history = AssetHistory.builder()
                .tangibleAsset(tangibleAsset)
                .historyType(historyType)
                .changedFields(changedFieldsJson)
                .snapshot(snapshotJson)
                .batchId(batchId)
                .createdBy(loginUserContext.getUserId())
                .build();
        assetHistoryStore.store(history);
    }
}
