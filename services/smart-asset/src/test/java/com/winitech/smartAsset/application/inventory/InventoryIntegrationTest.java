package com.winitech.smartAsset.application.inventory;

import com.winitech.common.bean.LoginUserContext;
import com.winitech.common.exception.InvalidParamException;
import com.winitech.smartAsset.domain.assetCategory.AssetCategory;
import com.winitech.smartAsset.domain.inventory.Inventory;
import com.winitech.smartAsset.domain.inventory.InventoryAdminCreateCommand;
import com.winitech.smartAsset.domain.inventory.InventoryCloneTemplateInfo;
import com.winitech.smartAsset.domain.inventory.InventoryInfo;
import com.winitech.smartAsset.domain.inventory.InventoryMemberCreateCommand;
import com.winitech.smartAsset.domain.inventory.InventoryMyStatusInfo;
import com.winitech.smartAsset.domain.inventory.InventoryPreviewInfo;
import com.winitech.smartAsset.domain.inventory.InventoryProgressInfo;
import com.winitech.smartAsset.domain.inventory.InventoryReportInfo;
import com.winitech.smartAsset.domain.inventory.InventoryResult;
import com.winitech.smartAsset.domain.inventory.InventoryResultRowInfo;
import com.winitech.smartAsset.domain.inventory.InventoryScheduleInfo;
import com.winitech.smartAsset.domain.assetLocation.AssetLocation;
import com.winitech.smartAsset.domain.tangibleAsset.TangibleAsset;
import com.winitech.smartAsset.infrastructure.assetCategory.AssetCategoryRepository;
import com.winitech.smartAsset.infrastructure.assetLocation.AssetLocationRepository;
import com.winitech.smartAsset.infrastructure.tangibleAsset.TangibleAssetRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * S-300~302(생성·목록) · S-303(진행 현황) · S-304(검수 승인/반려 - 관리자 대체 확인 경유) ·
 * S-308(종결 처리) - facade → service → 실제 DB 전 구간을 검증한다.
 */
@SpringBootTest
class InventoryIntegrationTest {

    @Autowired
    private InventoryFacade inventoryFacade;
    @Autowired
    private TangibleAssetRepository tangibleAssetRepository;
    @Autowired
    private AssetCategoryRepository assetCategoryRepository;
    @Autowired
    private AssetLocationRepository assetLocationRepository;
    @Autowired
    private JdbcTemplate jdbcTemplate;
    @Autowired
    private LoginUserContext loginUserContext;

    private AssetCategory testCategory;
    private AssetLocation testLocation;
    private final List<UUID> createdAssetIds = new java.util.ArrayList<>();
    private final List<UUID> createdInventoryIds = new java.util.ArrayList<>();

    @BeforeEach
    void setUp() {
        RequestContextHolder.setRequestAttributes(new ServletRequestAttributes(new MockHttpServletRequest()));
        loginUserContext.setUserId(UUID.randomUUID());
        testCategory = assetCategoryRepository.save(AssetCategory.builder()
                .categoryCode("INV-IT-CAT").categoryName("실사통합테스트카테고리").usefulLifeMonths(36).build());
        testLocation = assetLocationRepository.save(AssetLocation.builder().locationName("실사통합테스트위치").build());
        createdAssetIds.clear();
        createdInventoryIds.clear();
    }

    @AfterEach
    void cleanUp() {
        for (UUID inventoryId : createdInventoryIds) {
            jdbcTemplate.update("DELETE FROM inventory_result WHERE inventory_target_id IN " +
                    "(SELECT inventory_target_id FROM inventory_target WHERE inventory_id = ?)", inventoryId);
            jdbcTemplate.update("DELETE FROM inventory_target WHERE inventory_id = ?", inventoryId);
            jdbcTemplate.update("DELETE FROM inventory_inspector WHERE inventory_id = ?", inventoryId);
            jdbcTemplate.update("DELETE FROM inventory_excluded_member WHERE inventory_id = ?", inventoryId);
            jdbcTemplate.update("DELETE FROM inventory WHERE inventory_id = ?", inventoryId);
        }
        for (UUID assetId : createdAssetIds) {
            jdbcTemplate.update("DELETE FROM tangible_asset WHERE tangible_asset_id = ?", assetId);
        }
        jdbcTemplate.update("DELETE FROM asset_category WHERE asset_category_id = ?", testCategory.getId());
        jdbcTemplate.update("DELETE FROM asset_location WHERE asset_location_id = ?", testLocation.getId());
        RequestContextHolder.resetRequestAttributes();
    }

    private UUID createAsset(TangibleAsset.AssignType assignType, UUID memberId, String assetCodeSuffix) {
        TangibleAsset asset = tangibleAssetRepository.save(TangibleAsset.builder()
                .assetCode("AST-9301-" + assetCodeSuffix)
                .assetName("실사통합테스트자산")
                .category(testCategory)
                .location(testLocation)
                .assignType(assignType)
                .currentMemberId(memberId)
                .acquisitionDate(LocalDate.of(2026, 1, 1))
                .acquisitionAmount(BigDecimal.valueOf(1_000_000))
                .build());
        createdAssetIds.add(asset.getId());
        return asset.getId();
    }

    @Test
    void 임직원형_생성은_개인배정_자산만_대상으로_스냅샷을_만든다() {
        UUID memberA = UUID.randomUUID();
        UUID memberB = UUID.randomUUID();
        createAsset(TangibleAsset.AssignType.PERSONAL, memberA, "0001");
        UUID excludedAssetId = createAsset(TangibleAsset.AssignType.PERSONAL, memberB, "0002");
        UUID sharedAssetId = createAsset(TangibleAsset.AssignType.SHARED, null, "0003");

        InventoryPreviewInfo preview = inventoryFacade.previewMemberInventory(List.of(memberB));
        assertThat(preview.getTargetAssetCount()).isGreaterThanOrEqualTo(1);

        InventoryMemberCreateCommand command = InventoryMemberCreateCommand.builder()
                .title("실사통합테스트 임직원형")
                .approvalRequired(false)
                .excludedMemberIds(List.of(memberB))
                .build();

        UUID inventoryId = inventoryFacade.registerMemberInventory(command);
        createdInventoryIds.add(inventoryId);

        List<UUID> targetAssetIds = jdbcTemplate.queryForList(
                "SELECT tangible_asset_id FROM inventory_target WHERE inventory_id = ?", UUID.class, inventoryId);
        assertThat(targetAssetIds).doesNotContain(excludedAssetId, sharedAssetId);

        Integer resultCount = jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM inventory_result r JOIN inventory_target t ON t.inventory_target_id = r.inventory_target_id " +
                        "WHERE t.inventory_id = ? AND r.status = 'UNCONFIRMED'", Integer.class, inventoryId);
        assertThat(resultCount).isEqualTo(targetAssetIds.size());

        InventoryInfo info = inventoryFacade.getInventory(inventoryId);
        assertThat(info.getInventoryType()).isEqualTo(Inventory.InventoryType.MEMBER);
        assertThat(info.getStatus()).isEqualTo(Inventory.Status.IN_PROGRESS);
        assertThat(info.getTargetCount()).isEqualTo(targetAssetIds.size());
    }

    @Test
    void 관리자형_생성은_공용_미배정_자산을_대상으로_검수자_풀을_저장한다() {
        createAsset(TangibleAsset.AssignType.UNASSIGNED, null, "0004");
        UUID inspectorId = UUID.randomUUID();

        InventoryAdminCreateCommand command = InventoryAdminCreateCommand.builder()
                .title("실사통합테스트 관리자형")
                .inspectorMemberIds(List.of(inspectorId))
                .build();

        UUID inventoryId = inventoryFacade.registerAdminInventory(command);
        createdInventoryIds.add(inventoryId);

        List<UUID> inspectorIds = jdbcTemplate.queryForList(
                "SELECT member_id FROM inventory_inspector WHERE inventory_id = ?", UUID.class, inventoryId);
        assertThat(inspectorIds).containsExactly(inspectorId);

        Integer targetsWithMember = jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM inventory_target WHERE inventory_id = ? AND member_id IS NOT NULL", Integer.class, inventoryId);
        assertThat(targetsWithMember).isZero();
    }

    @Test
    void 목록_조회는_생성된_조사와_대상건수를_함께_보여준다() {
        createAsset(TangibleAsset.AssignType.PERSONAL, UUID.randomUUID(), "0005");
        UUID inventoryId = inventoryFacade.registerMemberInventory(
                InventoryMemberCreateCommand.builder().title("실사통합테스트 목록조회용").build());
        createdInventoryIds.add(inventoryId);

        Page<InventoryInfo> page = inventoryFacade.getInventoryList(0, 50, null);

        assertThat(page.getContent()).anySatisfy(row -> {
            if (row.getInventoryId().equals(inventoryId)) {
                assertThat(row.getTargetCount()).isGreaterThanOrEqualTo(1);
            }
        });
        assertThat(page.getContent().stream().map(InventoryInfo::getInventoryId)).contains(inventoryId);
    }

    private UUID targetIdForAsset(UUID inventoryId, UUID tangibleAssetId) {
        return jdbcTemplate.queryForObject(
                "SELECT inventory_target_id FROM inventory_target WHERE inventory_id = ? AND tangible_asset_id = ?",
                UUID.class, inventoryId, tangibleAssetId);
    }

    /**
     * 임직원형 대상 산정은 이 DB에 실재하는 모든 개인배정(PERSONAL)·사용중(USE) 자산을 그대로
     * 쓸어담는다(실제 기능이 의도한 동작) - 이 테스트가 만든 자산 외에 이미 존재하는 자산도 매번
     * 같은 조사에 함께 들어온다. 그런 "내가 모르는" 잔여 대상까지 전부 일괄로 마무리 처리해야
     * closeInventory()가 실제로 성공한다 - 실제 관리자가 조사를 마감할 때도 마찬가지다.
     */
    private void closeAllRemainingUnconfirmed(UUID inventoryId) {
        List<UUID> unconfirmedTargetIds = jdbcTemplate.queryForList(
                "SELECT r.inventory_target_id FROM inventory_result r " +
                        "JOIN inventory_target t ON t.inventory_target_id = r.inventory_target_id " +
                        "WHERE t.inventory_id = ? AND r.status = 'UNCONFIRMED' AND r.closure_action IS NULL",
                UUID.class, inventoryId);
        if (!unconfirmedTargetIds.isEmpty()) {
            inventoryFacade.closeResultsBulk(unconfirmedTargetIds, InventoryResult.ClosureAction.MANUAL_VERIFY,
                    InventoryResult.ClosureReasonCode.LOCATION_UNKNOWN, "테스트 잔여 대상 일괄 처리");
        }
    }

    @Test
    void 확인처리와_이상보고로_처리한_대상은_진행현황에_반영되고_잔여_대상까지_마무리하면_종료된다() {
        UUID assetA = createAsset(TangibleAsset.AssignType.PERSONAL, UUID.randomUUID(), "0006");
        UUID assetB = createAsset(TangibleAsset.AssignType.PERSONAL, UUID.randomUUID(), "0007");
        UUID inventoryId = inventoryFacade.registerMemberInventory(
                InventoryMemberCreateCommand.builder().title("실사통합테스트 검수흐름").approvalRequired(true).build());
        createdInventoryIds.add(inventoryId);

        UUID targetA = targetIdForAsset(inventoryId, assetA);
        UUID targetB = targetIdForAsset(inventoryId, assetB);

        inventoryFacade.adminConfirmResult(targetA); // approvalRequired=true -> PENDING_APPROVAL
        inventoryFacade.approveResult(targetA); // -> CONFIRMED
        inventoryFacade.adminReportAnomaly(targetB, InventoryResult.AnomalyType.WRONG_HOLDER, "실제 보유자가 다릅니다");

        InventoryProgressInfo progress = inventoryFacade.getProgress(inventoryId);
        assertThat(progress.getConfirmedCount()).isGreaterThanOrEqualTo(1);
        assertThat(progress.getAnomalyCount()).isGreaterThanOrEqualTo(1);

        closeAllRemainingUnconfirmed(inventoryId); // 이 DB에 이미 있던 다른 개인배정 자산까지 마무리
        inventoryFacade.closeInventory(inventoryId);

        InventoryInfo info = inventoryFacade.getInventory(inventoryId);
        assertThat(info.getStatus()).isEqualTo(Inventory.Status.CLOSED);
    }

    @Test
    void 미확인_항목이_남아있으면_종료가_막히고_종결처리후에는_종료된다() {
        UUID assetId = createAsset(TangibleAsset.AssignType.PERSONAL, UUID.randomUUID(), "0008");
        UUID inventoryId = inventoryFacade.registerMemberInventory(
                InventoryMemberCreateCommand.builder().title("실사통합테스트 종결흐름").build());
        createdInventoryIds.add(inventoryId);

        UUID targetId = targetIdForAsset(inventoryId, assetId);

        assertThatThrownBy(() -> inventoryFacade.closeInventory(inventoryId)).isInstanceOf(InvalidParamException.class);

        inventoryFacade.closeResult(targetId, InventoryResult.ClosureAction.MANUAL_VERIFY,
                InventoryResult.ClosureReasonCode.LOCATION_UNKNOWN, "창고 재확인 예정");
        closeAllRemainingUnconfirmed(inventoryId); // 이 DB에 이미 있던 다른 개인배정 자산까지 마무리

        inventoryFacade.closeInventory(inventoryId);

        InventoryInfo info = inventoryFacade.getInventory(inventoryId);
        assertThat(info.getStatus()).isEqualTo(Inventory.Status.CLOSED);
    }

    @Test
    void 리포트는_분실목록과_종결내역을_실제_DB_기준으로_계산한다() {
        UUID lostAssetId = createAsset(TangibleAsset.AssignType.PERSONAL, UUID.randomUUID(), "0009");
        UUID inventoryId = inventoryFacade.registerMemberInventory(
                InventoryMemberCreateCommand.builder().title("실사통합테스트 리포트").build());
        createdInventoryIds.add(inventoryId);

        UUID lostTargetId = targetIdForAsset(inventoryId, lostAssetId);
        inventoryFacade.closeResult(lostTargetId, InventoryResult.ClosureAction.LOST,
                InventoryResult.ClosureReasonCode.LOCATION_UNKNOWN, "창고 전체 수색에도 발견되지 않음");
        closeAllRemainingUnconfirmed(inventoryId);
        inventoryFacade.closeInventory(inventoryId);

        InventoryReportInfo report = inventoryFacade.getReport(inventoryId);

        assertThat(report.getStatus()).isEqualTo(Inventory.Status.CLOSED);
        assertThat(report.getLostItems()).anySatisfy(item -> assertThat(item.getTangibleAssetId()).isEqualTo(lostAssetId));
        assertThat(report.getClosureBreakdown()).isNotEmpty();
    }

    @Test
    void 템플릿_복제용_조회는_임직원형은_제외인원을_관리자형은_검수자를_그대로_돌려준다() {
        UUID excludedMemberId = UUID.randomUUID();
        UUID memberInventoryId = inventoryFacade.registerMemberInventory(
                InventoryMemberCreateCommand.builder().title("실사통합테스트 템플릿-임직원형")
                        .excludedMemberIds(List.of(excludedMemberId)).build());
        createdInventoryIds.add(memberInventoryId);

        InventoryCloneTemplateInfo memberTemplate = inventoryFacade.getCloneTemplate(memberInventoryId);
        assertThat(memberTemplate.getInventoryType()).isEqualTo(Inventory.InventoryType.MEMBER);
        assertThat(memberTemplate.getExcludedMemberIds()).containsExactly(excludedMemberId);
        assertThat(memberTemplate.getInspectorMemberIds()).isEmpty();

        createAsset(TangibleAsset.AssignType.UNASSIGNED, null, "0010");
        UUID inspectorId = UUID.randomUUID();
        UUID adminInventoryId = inventoryFacade.registerAdminInventory(
                InventoryAdminCreateCommand.builder().title("실사통합테스트 템플릿-관리자형")
                        .inspectorMemberIds(List.of(inspectorId)).build());
        createdInventoryIds.add(adminInventoryId);

        InventoryCloneTemplateInfo adminTemplate = inventoryFacade.getCloneTemplate(adminInventoryId);
        assertThat(adminTemplate.getInventoryType()).isEqualTo(Inventory.InventoryType.ADMIN);
        assertThat(adminTemplate.getInspectorMemberIds()).containsExactly(inspectorId);
        assertThat(adminTemplate.getExcludedMemberIds()).isEmpty();
    }

    @Test
    void 반복시행_스케줄은_주기가_설정된_종료조사_기준_다음예정일을_계산한다() {
        UUID inventoryId = inventoryFacade.registerMemberInventory(
                InventoryMemberCreateCommand.builder().title("실사통합테스트 스케줄")
                        .recurrenceRule(Inventory.RecurrenceRule.QUARTERLY).build());
        createdInventoryIds.add(inventoryId);
        closeAllRemainingUnconfirmed(inventoryId);
        inventoryFacade.closeInventory(inventoryId);

        // nextCycleInProgress는 이 DB에 실재하는 "다른" MEMBER형 진행중 조사 여부까지 함께 보는
        // org-wide 판정이라(Q-32 대상 산정과 동일한 성격) 이 테스트가 통제할 수 없다 - 단언하지 않는다.
        List<InventoryScheduleInfo> schedules = inventoryFacade.getSchedules();

        InventoryScheduleInfo memberSchedule = schedules.stream()
                .filter(s -> s.getInventoryType() == Inventory.InventoryType.MEMBER)
                .findFirst().orElseThrow();
        assertThat(memberSchedule.getLastClosedInventoryId()).isEqualTo(inventoryId);
        assertThat(memberSchedule.getNextDueDate()).isEqualTo(memberSchedule.getLastClosedAt().plusMonths(3));
    }

    @Test
    void 내_전수조사_조회와_본인_셀프서비스_확인은_실제_DB_기준으로_소유권을_검사한다() {
        UUID myAssetId = createAsset(TangibleAsset.AssignType.PERSONAL, loginUserContext.getUserId(), "0011");
        UUID inventoryId = inventoryFacade.registerMemberInventory(
                InventoryMemberCreateCommand.builder().title("실사통합테스트 셀프서비스").build());
        createdInventoryIds.add(inventoryId);

        InventoryMyStatusInfo myStatus = inventoryFacade.getMyStatus();
        assertThat(myStatus.isHasActiveInventory()).isTrue();
        assertThat(myStatus.getInventoryId()).isEqualTo(inventoryId);
        assertThat(myStatus.getResults()).anySatisfy(row -> assertThat(row.getTangibleAssetId()).isEqualTo(myAssetId));

        UUID myTargetId = targetIdForAsset(inventoryId, myAssetId);
        inventoryFacade.selfConfirmResult(myTargetId);

        List<InventoryResultRowInfo> rows = inventoryFacade.getResultRows(inventoryId, "CONFIRMED");
        assertThat(rows).anySatisfy(row -> assertThat(row.getTangibleAssetId()).isEqualTo(myAssetId));

        closeAllRemainingUnconfirmed(inventoryId);
        inventoryFacade.closeInventory(inventoryId);
    }

    @Test
    void 셀프서비스_확인은_본인_소유가_아닌_대상이면_거부한다() {
        UUID othersAssetId = createAsset(TangibleAsset.AssignType.PERSONAL, UUID.randomUUID(), "0012");
        UUID inventoryId = inventoryFacade.registerMemberInventory(
                InventoryMemberCreateCommand.builder().title("실사통합테스트 셀프서비스 거부").build());
        createdInventoryIds.add(inventoryId);

        UUID othersTargetId = targetIdForAsset(inventoryId, othersAssetId);

        assertThatThrownBy(() -> inventoryFacade.selfConfirmResult(othersTargetId)).isInstanceOf(InvalidParamException.class);

        closeAllRemainingUnconfirmed(inventoryId);
        inventoryFacade.closeInventory(inventoryId);
    }

    @Test
    void 라벨없음_예외_경로는_사진과_함께_항상_승인대기로_전환된다() {
        UUID myAssetId = createAsset(TangibleAsset.AssignType.PERSONAL, loginUserContext.getUserId(), "0013");
        UUID inventoryId = inventoryFacade.registerMemberInventory(
                InventoryMemberCreateCommand.builder().title("실사통합테스트 라벨예외").approvalRequired(false).build());
        createdInventoryIds.add(inventoryId);
        UUID myTargetId = targetIdForAsset(inventoryId, myAssetId);

        inventoryFacade.selfConfirmResultWithoutScan(myTargetId, UUID.randomUUID(),
                java.time.OffsetDateTime.now().minusSeconds(5), java.time.OffsetDateTime.now());

        List<InventoryResultRowInfo> pending = inventoryFacade.getResultRows(inventoryId, "PENDING_APPROVAL");
        assertThat(pending).anySatisfy(row -> assertThat(row.getTangibleAssetId()).isEqualTo(myAssetId));

        inventoryFacade.approveResult(myTargetId);
        closeAllRemainingUnconfirmed(inventoryId);
        inventoryFacade.closeInventory(inventoryId);
    }
}
