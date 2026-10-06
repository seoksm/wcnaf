package com.winitech.smartAsset.domain.inventory;

import com.winitech.common.bean.LoginUserContext;
import com.winitech.common.exception.InvalidParamException;
import com.winitech.smartAsset.domain.tangibleAsset.TangibleAsset;
import com.winitech.smartAsset.domain.tangibleAsset.TangibleAssetReader;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * S-301/302 전수조사 생성·미리보기 - 대상 자산 산정(배정형태 기준, Q-32)과 스냅샷(targets+results)
 * 생성이 올바른지 검증한다. 실제 대상 목록 조회/저장 쿼리 자체는 InventoryRepositoryTest(통합
 * 테스트)가 다룬다.
 */
@ExtendWith(MockitoExtension.class)
class InventoryServiceImplTest {

    @Mock private InventoryReader inventoryReader;
    @Mock private InventoryStore inventoryStore;
    @Mock private InventoryTargetReader inventoryTargetReader;
    @Mock private InventoryTargetStore inventoryTargetStore;
    @Mock private InventoryResultReader inventoryResultReader;
    @Mock private InventoryResultStore inventoryResultStore;
    @Mock private InventoryInspectorReader inventoryInspectorReader;
    @Mock private InventoryExcludedMemberReader inventoryExcludedMemberReader;
    @Mock private TangibleAssetReader tangibleAssetReader;
    @Mock private LoginUserContext loginUserContext;

    private InventoryServiceImpl service;

    @BeforeEach
    void setUp() {
        service = new InventoryServiceImpl(inventoryReader, inventoryStore, inventoryTargetReader,
                inventoryTargetStore, inventoryResultReader, inventoryResultStore,
                inventoryInspectorReader, inventoryExcludedMemberReader, tangibleAssetReader, loginUserContext);

        // store()들은 실제 구현처럼 인자를 그대로 돌려준다 - saveAll의 "저장된 엔티티 반환" 동작을 흉내낸다
        lenient().when(inventoryTargetStore.storeAll(any())).thenAnswer(inv -> inv.getArgument(0));
        lenient().when(inventoryResultStore.storeAll(any())).thenAnswer(inv -> inv.getArgument(0));
        lenient().when(inventoryReader.findMostRecentClosedByType(any())).thenReturn(Optional.empty());
    }

    private TangibleAsset asset(TangibleAsset.AssignType assignType, UUID memberId) {
        return TangibleAsset.builder()
                .assetCode("AST-2026-" + UUID.randomUUID().toString().substring(0, 4))
                .assetName("노트북")
                .lifeStatus(TangibleAsset.LifeStatus.USE)
                .assignType(assignType)
                .currentMemberId(memberId)
                .acquisitionDate(LocalDate.of(2026, 1, 1))
                .acquisitionAmount(new BigDecimal("1000000"))
                .build();
    }

    @Test
    void previewMemberInventory는_개인배정_자산만_대상으로_삼고_제외인원을_뺀다() {
        UUID memberA = UUID.randomUUID();
        UUID memberB = UUID.randomUUID();
        when(tangibleAssetReader.findAllByAssignTypeInAndLifeStatus(any(), eq(TangibleAsset.LifeStatus.USE)))
                .thenReturn(List.of(asset(TangibleAsset.AssignType.PERSONAL, memberA), asset(TangibleAsset.AssignType.PERSONAL, memberB)));

        InventoryPreviewInfo preview = service.previewMemberInventory(List.of(memberB));

        assertThat(preview.getTargetAssetCount()).isEqualTo(1);
        assertThat(preview.getParticipantCount()).isEqualTo(1);
    }

    @Test
    void createMemberInventory_대상이_없으면_예외() {
        when(tangibleAssetReader.findAllByAssignTypeInAndLifeStatus(any(), any())).thenReturn(List.of());

        InventoryMemberCreateCommand command = InventoryMemberCreateCommand.builder()
                .title("2026 하반기 실사")
                .build();

        assertThatThrownBy(() -> service.createMemberInventory(command)).isInstanceOf(InvalidParamException.class);
        verify(inventoryStore, never()).store(any());
    }

    @Test
    void createMemberInventory_제목이_없으면_예외() {
        InventoryMemberCreateCommand command = InventoryMemberCreateCommand.builder().title("  ").build();

        assertThatThrownBy(() -> service.createMemberInventory(command)).isInstanceOf(InvalidParamException.class);
        verifyNoInteractions(tangibleAssetReader);
    }

    @Test
    void createMemberInventory_성공하면_대상만큼_target과_result가_생성되고_제외인원이_저장된다() {
        UUID memberId = UUID.randomUUID();
        UUID excludedMemberId = UUID.randomUUID();
        UUID createdBy = UUID.randomUUID();
        when(loginUserContext.getUserId()).thenReturn(createdBy);
        when(tangibleAssetReader.findAllByAssignTypeInAndLifeStatus(any(), eq(TangibleAsset.LifeStatus.USE)))
                .thenReturn(List.of(asset(TangibleAsset.AssignType.PERSONAL, memberId)));

        InventoryMemberCreateCommand command = InventoryMemberCreateCommand.builder()
                .title("2026 하반기 실사")
                .approvalRequired(true)
                .excludedMemberIds(List.of(excludedMemberId))
                .build();

        service.createMemberInventory(command);

        ArgumentCaptor<Inventory> inventoryCaptor = ArgumentCaptor.forClass(Inventory.class);
        verify(inventoryStore).store(inventoryCaptor.capture());
        Inventory stored = inventoryCaptor.getValue();
        assertThat(stored.getInventoryType()).isEqualTo(Inventory.InventoryType.MEMBER);
        assertThat(stored.getApprovalRequired()).isTrue();
        assertThat(stored.getCreatedBy()).isEqualTo(createdBy);

        ArgumentCaptor<List<InventoryExcludedMember>> excludedCaptor = ArgumentCaptor.forClass(List.class);
        verify(inventoryStore).storeExcludedMembers(excludedCaptor.capture());
        assertThat(excludedCaptor.getValue()).hasSize(1);
        assertThat(excludedCaptor.getValue().get(0).getMemberId()).isEqualTo(excludedMemberId);

        ArgumentCaptor<List<InventoryTarget>> targetsCaptor = ArgumentCaptor.forClass(List.class);
        verify(inventoryTargetStore).storeAll(targetsCaptor.capture());
        assertThat(targetsCaptor.getValue()).hasSize(1);
        assertThat(targetsCaptor.getValue().get(0).getMemberId()).isEqualTo(memberId);

        verify(inventoryResultStore).storeAll(argThat(results -> results.size() == 1));
    }

    @Test
    void previewAdminInventory는_공용_미배정_자산을_대상으로_삼는다() {
        when(tangibleAssetReader.findAllByAssignTypeInAndLifeStatus(any(), eq(TangibleAsset.LifeStatus.USE)))
                .thenReturn(List.of(
                        asset(TangibleAsset.AssignType.UNASSIGNED, null),
                        asset(TangibleAsset.AssignType.SHARED, null)));

        InventoryPreviewInfo preview = service.previewAdminInventory();

        assertThat(preview.getTargetAssetCount()).isEqualTo(2);
    }

    @Test
    void createAdminInventory_검수자가_없으면_예외() {
        InventoryAdminCreateCommand command = InventoryAdminCreateCommand.builder()
                .title("관리자형 실사")
                .inspectorMemberIds(List.of())
                .build();

        assertThatThrownBy(() -> service.createAdminInventory(command)).isInstanceOf(InvalidParamException.class);
        verifyNoInteractions(tangibleAssetReader);
    }

    @Test
    void createAdminInventory_성공하면_검수자풀이_저장되고_대상의_memberId는_비어있다() {
        UUID inspectorId = UUID.randomUUID();
        when(loginUserContext.getUserId()).thenReturn(UUID.randomUUID());
        when(tangibleAssetReader.findAllByAssignTypeInAndLifeStatus(any(), eq(TangibleAsset.LifeStatus.USE)))
                .thenReturn(List.of(asset(TangibleAsset.AssignType.SHARED, null)));

        InventoryAdminCreateCommand command = InventoryAdminCreateCommand.builder()
                .title("관리자형 실사")
                .inspectorMemberIds(List.of(inspectorId))
                .build();

        service.createAdminInventory(command);

        ArgumentCaptor<List<InventoryInspector>> inspectorsCaptor = ArgumentCaptor.forClass(List.class);
        verify(inventoryStore).storeInspectors(inspectorsCaptor.capture());
        assertThat(inspectorsCaptor.getValue()).hasSize(1);
        assertThat(inspectorsCaptor.getValue().get(0).getMemberId()).isEqualTo(inspectorId);

        ArgumentCaptor<List<InventoryTarget>> targetsCaptor = ArgumentCaptor.forClass(List.class);
        verify(inventoryTargetStore).storeAll(targetsCaptor.capture());
        assertThat(targetsCaptor.getValue().get(0).getMemberId()).isNull();
    }

    // ------------------------------------------------------------------
    // 진행 현황(S-303) · 검수 승인/반려(S-304) · 관리자 대체 확인 · 종결 처리(S-308)
    // ------------------------------------------------------------------

    private Inventory newInventory(boolean approvalRequired) {
        return Inventory.builder()
                .title("검수테스트조사")
                .inventoryType(Inventory.InventoryType.MEMBER)
                .approvalRequired(approvalRequired)
                .createdBy(UUID.randomUUID())
                .build();
    }

    private InventoryResult newResult(Inventory inventory, UUID memberId) {
        TangibleAsset tangibleAsset = asset(TangibleAsset.AssignType.PERSONAL, memberId);
        InventoryTarget target = InventoryTarget.builder().inventory(inventory).tangibleAsset(tangibleAsset).memberId(memberId).build();
        return InventoryResult.builder().inventoryTarget(target).build();
    }

    @Test
    void loadProgress는_4개_지표와_참여자별_분해를_계산한다() {
        Inventory inventory = newInventory(false);
        UUID memberA = UUID.randomUUID();
        UUID memberB = UUID.randomUUID();

        InventoryResult confirmedForA = newResult(inventory, memberA);
        confirmedForA.confirm(false);
        InventoryResult unconfirmedForA = newResult(inventory, memberA);
        InventoryResult unconfirmedForB = newResult(inventory, memberB);

        when(inventoryResultReader.findAllByInventoryId(any()))
                .thenReturn(List.of(confirmedForA, unconfirmedForA, unconfirmedForB));

        InventoryProgressInfo progress = service.loadProgress(UUID.randomUUID());

        assertThat(progress.getConfirmedCount()).isEqualTo(1);
        assertThat(progress.getUnconfirmedCount()).isEqualTo(2);
        assertThat(progress.getPendingApprovalCount()).isZero();
        assertThat(progress.getAnomalyCount()).isZero();
        assertThat(progress.getTotalCount()).isEqualTo(3);

        InventoryProgressInfo.ParticipantSummary summaryA = progress.getParticipants().stream()
                .filter(p -> p.getMemberId().equals(memberA)).findFirst().orElseThrow();
        assertThat(summaryA.getTotalCount()).isEqualTo(2);
        assertThat(summaryA.getUnconfirmedCount()).isEqualTo(1);
        assertThat(summaryA.isParticipated()).isTrue(); // 확인완료 1건이 있으므로 미참여가 아니다

        InventoryProgressInfo.ParticipantSummary summaryB = progress.getParticipants().stream()
                .filter(p -> p.getMemberId().equals(memberB)).findFirst().orElseThrow();
        assertThat(summaryB.getTotalCount()).isEqualTo(1);
        assertThat(summaryB.getUnconfirmedCount()).isEqualTo(1);
        assertThat(summaryB.isParticipated()).isFalse(); // 배정받은 유일한 대상이 미확인 상태 - 미참여
    }

    @Test
    void loadResultRows_상태필터를_넘기면_필터된_조회_메서드를_쓴다() {
        Inventory inventory = newInventory(false);
        InventoryResult result = newResult(inventory, UUID.randomUUID());
        when(inventoryResultReader.findAllByInventoryIdAndStatus(any(), eq(InventoryResult.Status.UNCONFIRMED)))
                .thenReturn(List.of(result));

        List<InventoryResultRowInfo> rows = service.loadResultRows(UUID.randomUUID(), InventoryResult.Status.UNCONFIRMED);

        assertThat(rows).hasSize(1);
        verify(inventoryResultReader, never()).findAllByInventoryId(any());
    }

    @Test
    void loadResultRows_필터가_없으면_전체를_조회한다() {
        Inventory inventory = newInventory(false);
        when(inventoryResultReader.findAllByInventoryId(any())).thenReturn(List.of(newResult(inventory, UUID.randomUUID())));

        List<InventoryResultRowInfo> rows = service.loadResultRows(UUID.randomUUID(), null);

        assertThat(rows).hasSize(1);
        verify(inventoryResultReader, never()).findAllByInventoryIdAndStatus(any(), any());
    }

    @Test
    void approveResult_승인대기를_확인완료로_바꾸고_검수자를_기록한다() {
        Inventory inventory = newInventory(true);
        InventoryResult result = newResult(inventory, UUID.randomUUID());
        result.confirm(true); // PENDING_APPROVAL
        UUID reviewerId = UUID.randomUUID();
        when(inventoryResultReader.findByInventoryTargetId(any())).thenReturn(result);
        when(loginUserContext.getUserId()).thenReturn(reviewerId);

        service.approveResult(UUID.randomUUID());

        assertThat(result.getStatus()).isEqualTo(InventoryResult.Status.CONFIRMED);
        assertThat(result.getReviewedBy()).isEqualTo(reviewerId);
        verify(inventoryResultStore).store(result);
    }

    @Test
    void approveResult_종료된_조사면_예외() {
        Inventory inventory = newInventory(true);
        inventory.close(UUID.randomUUID());
        InventoryResult result = newResult(inventory, UUID.randomUUID());
        when(inventoryResultReader.findByInventoryTargetId(any())).thenReturn(result);

        assertThatThrownBy(() -> service.approveResult(UUID.randomUUID())).isInstanceOf(InvalidParamException.class);
        verify(inventoryResultStore, never()).store(any());
    }

    @Test
    void rejectResult_승인대기를_미확인으로_되돌리고_사유를_기록한다() {
        Inventory inventory = newInventory(true);
        InventoryResult result = newResult(inventory, UUID.randomUUID());
        result.confirm(true);
        when(inventoryResultReader.findByInventoryTargetId(any())).thenReturn(result);
        when(loginUserContext.getUserId()).thenReturn(UUID.randomUUID());

        service.rejectResult(UUID.randomUUID(), "사진이 흐림");

        assertThat(result.getStatus()).isEqualTo(InventoryResult.Status.UNCONFIRMED);
        assertThat(result.getRejectionReason()).isEqualTo("사진이 흐림");
        verify(inventoryResultStore).store(result);
    }

    @Test
    void adminConfirmResult는_조사의_승인옵션에_따라_상태가_결정된다() {
        Inventory approvalOffInventory = newInventory(false);
        InventoryResult resultA = newResult(approvalOffInventory, UUID.randomUUID());
        when(inventoryResultReader.findByInventoryTargetId(any())).thenReturn(resultA);

        service.adminConfirmResult(UUID.randomUUID());

        assertThat(resultA.getStatus()).isEqualTo(InventoryResult.Status.CONFIRMED);
    }

    @Test
    void adminConfirmResult는_승인옵션이_켜져있으면_승인대기가_된다() {
        Inventory approvalOnInventory = newInventory(true);
        InventoryResult result = newResult(approvalOnInventory, UUID.randomUUID());
        when(inventoryResultReader.findByInventoryTargetId(any())).thenReturn(result);

        service.adminConfirmResult(UUID.randomUUID());

        assertThat(result.getStatus()).isEqualTo(InventoryResult.Status.PENDING_APPROVAL);
    }

    @Test
    void adminReportAnomaly는_이상상태로_바꾸고_유형과_메모를_남긴다() {
        Inventory inventory = newInventory(false);
        InventoryResult result = newResult(inventory, UUID.randomUUID());
        when(inventoryResultReader.findByInventoryTargetId(any())).thenReturn(result);

        service.adminReportAnomaly(UUID.randomUUID(), InventoryResult.AnomalyType.WRONG_HOLDER, "실제 보유자가 다릅니다");

        assertThat(result.getStatus()).isEqualTo(InventoryResult.Status.ANOMALY);
        assertThat(result.getAnomalyType()).isEqualTo(InventoryResult.AnomalyType.WRONG_HOLDER);
        assertThat(result.getNote()).isEqualTo("실제 보유자가 다릅니다");
    }

    @Test
    void closeResult_미확인_항목을_종결처리한다() {
        Inventory inventory = newInventory(false);
        InventoryResult result = newResult(inventory, UUID.randomUUID());
        when(inventoryResultReader.findByInventoryTargetId(any())).thenReturn(result);

        service.closeResult(UUID.randomUUID(), InventoryResult.ClosureAction.CARRY_OVER,
                InventoryResult.ClosureReasonCode.ON_LEAVE, "휴직 중");

        assertThat(result.getClosureAction()).isEqualTo(InventoryResult.ClosureAction.CARRY_OVER);
        assertThat(result.getClosureReasonCode()).isEqualTo(InventoryResult.ClosureReasonCode.ON_LEAVE);
    }

    @Test
    void closeResultsBulk_분실은_일괄처리를_거부한다() {
        assertThatThrownBy(() -> service.closeResultsBulk(
                List.of(UUID.randomUUID()), InventoryResult.ClosureAction.LOST, InventoryResult.ClosureReasonCode.LOCATION_UNKNOWN, null))
                .isInstanceOf(InvalidParamException.class);
        verifyNoInteractions(inventoryResultReader);
        verify(inventoryResultStore, never()).store(any());
    }

    @Test
    void closeResultsBulk_분실이_아니면_각각_종결처리된다() {
        Inventory inventory = newInventory(false);
        InventoryResult result1 = newResult(inventory, UUID.randomUUID());
        InventoryResult result2 = newResult(inventory, UUID.randomUUID());
        UUID targetId1 = UUID.randomUUID();
        UUID targetId2 = UUID.randomUUID();
        when(inventoryResultReader.findByInventoryTargetId(targetId1)).thenReturn(result1);
        when(inventoryResultReader.findByInventoryTargetId(targetId2)).thenReturn(result2);

        service.closeResultsBulk(List.of(targetId1, targetId2), InventoryResult.ClosureAction.CARRY_OVER,
                InventoryResult.ClosureReasonCode.ON_LEAVE, "일괄 이월");

        assertThat(result1.getClosureAction()).isEqualTo(InventoryResult.ClosureAction.CARRY_OVER);
        assertThat(result2.getClosureAction()).isEqualTo(InventoryResult.ClosureAction.CARRY_OVER);
        verify(inventoryResultStore, times(2)).store(any());
    }

    @Test
    void closeInventory_미처리_미확인이_남아있으면_예외() {
        Inventory inventory = newInventory(false);
        InventoryResult unprocessed = newResult(inventory, UUID.randomUUID()); // closureAction 없음
        UUID inventoryId = UUID.randomUUID();
        when(inventoryReader.findById(inventoryId)).thenReturn(inventory);
        when(inventoryResultReader.findAllByInventoryIdAndStatus(inventoryId, InventoryResult.Status.UNCONFIRMED))
                .thenReturn(List.of(unprocessed));

        assertThatThrownBy(() -> service.closeInventory(inventoryId)).isInstanceOf(InvalidParamException.class);
        assertThat(inventory.getStatus()).isEqualTo(Inventory.Status.IN_PROGRESS);
    }

    @Test
    void closeInventory_모든_미확인이_처리됐으면_종료된다() {
        Inventory inventory = newInventory(false);
        InventoryResult processed = newResult(inventory, UUID.randomUUID());
        processed.close(InventoryResult.ClosureAction.CARRY_OVER, InventoryResult.ClosureReasonCode.ON_LEAVE, null);
        UUID inventoryId = UUID.randomUUID();
        UUID closedBy = UUID.randomUUID();
        when(inventoryReader.findById(inventoryId)).thenReturn(inventory);
        when(inventoryResultReader.findAllByInventoryIdAndStatus(inventoryId, InventoryResult.Status.UNCONFIRMED))
                .thenReturn(List.of(processed));
        when(loginUserContext.getUserId()).thenReturn(closedBy);

        service.closeInventory(inventoryId);

        assertThat(inventory.getStatus()).isEqualTo(Inventory.Status.CLOSED);
        assertThat(inventory.getClosedBy()).isEqualTo(closedBy);
        verify(inventoryStore).store(inventory);
    }

    // ------------------------------------------------------------------
    // 리포트(S-305) · 반복 시행 스케줄·템플릿 복제(S-306)
    // ------------------------------------------------------------------

    @Test
    void loadReport는_종결내역과_분실목록과_이상분해를_계산한다() {
        Inventory inventory = newInventory(false);
        UUID inventoryId = UUID.randomUUID();
        when(inventoryReader.findById(inventoryId)).thenReturn(inventory);

        InventoryResult confirmed = newResult(inventory, UUID.randomUUID());
        confirmed.confirm(false);

        InventoryResult anomalyResult = newResult(inventory, UUID.randomUUID());
        anomalyResult.reportAnomaly(InventoryResult.AnomalyType.DAMAGE, "파손됨");

        InventoryResult carriedOver = newResult(inventory, UUID.randomUUID());
        carriedOver.close(InventoryResult.ClosureAction.CARRY_OVER, InventoryResult.ClosureReasonCode.ON_LEAVE, "휴직");

        InventoryResult lost = newResult(inventory, UUID.randomUUID());
        lost.close(InventoryResult.ClosureAction.LOST, InventoryResult.ClosureReasonCode.LOCATION_UNKNOWN, "찾을 수 없음");

        when(inventoryResultReader.findAllByInventoryId(inventoryId))
                .thenReturn(List.of(confirmed, anomalyResult, carriedOver, lost));

        InventoryReportInfo report = service.loadReport(inventoryId);

        assertThat(report.getTotalCount()).isEqualTo(4);
        assertThat(report.getConfirmedCount()).isEqualTo(1);
        assertThat(report.getAnomalyCount()).isEqualTo(1);
        assertThat(report.getUnconfirmedCount()).isEqualTo(2); // carriedOver·lost는 close()해도 status는 UNCONFIRMED 그대로

        assertThat(report.getClosureBreakdown()).hasSize(2);
        assertThat(report.getLostItems()).hasSize(1);
        assertThat(report.getLostItems().get(0).getClosureNote()).isEqualTo("찾을 수 없음");
        assertThat(report.getAnomalyBreakdown()).hasSize(1);
        assertThat(report.getAnomalyBreakdown().get(0).getAnomalyType()).isEqualTo(InventoryResult.AnomalyType.DAMAGE);
    }

    private Inventory newInventoryWithRecurrence(Inventory.RecurrenceRule rule) {
        return Inventory.builder()
                .title("검수테스트조사").inventoryType(Inventory.InventoryType.MEMBER)
                .recurrenceRule(rule).createdBy(UUID.randomUUID()).build();
    }

    @Test
    void loadSchedules는_반복주기가_설정된_최근_종료조사만_유형별로_반환한다() {
        Inventory memberClosed = newInventoryWithRecurrence(Inventory.RecurrenceRule.SEMIANNUAL);
        memberClosed.close(UUID.randomUUID());

        when(inventoryReader.findMostRecentClosedByType(Inventory.InventoryType.MEMBER)).thenReturn(Optional.of(memberClosed));
        when(inventoryReader.findMostRecentClosedByType(Inventory.InventoryType.ADMIN)).thenReturn(Optional.empty());
        when(inventoryReader.existsInProgressByType(Inventory.InventoryType.MEMBER)).thenReturn(false);

        List<InventoryScheduleInfo> schedules = service.loadSchedules();

        assertThat(schedules).hasSize(1);
        InventoryScheduleInfo schedule = schedules.get(0);
        assertThat(schedule.getInventoryType()).isEqualTo(Inventory.InventoryType.MEMBER);
        assertThat(schedule.getNextDueDate()).isEqualTo(memberClosed.getClosedAt().plusMonths(6));
        assertThat(schedule.isNextCycleInProgress()).isFalse();
    }

    @Test
    void loadSchedules는_다음회차가_이미_진행중이면_그렇게_표시한다() {
        Inventory memberClosed = newInventoryWithRecurrence(Inventory.RecurrenceRule.QUARTERLY);
        memberClosed.close(UUID.randomUUID());

        when(inventoryReader.findMostRecentClosedByType(Inventory.InventoryType.MEMBER)).thenReturn(Optional.of(memberClosed));
        when(inventoryReader.findMostRecentClosedByType(Inventory.InventoryType.ADMIN)).thenReturn(Optional.empty());
        when(inventoryReader.existsInProgressByType(Inventory.InventoryType.MEMBER)).thenReturn(true);

        List<InventoryScheduleInfo> schedules = service.loadSchedules();

        assertThat(schedules.get(0).isNextCycleInProgress()).isTrue();
        assertThat(schedules.get(0).isOverdue()).isFalse(); // 진행 중이면 지남 여부 자체를 따지지 않는다
    }

    @Test
    void loadCloneTemplate은_임직원형이면_제외인원만_담는다() {
        Inventory memberInventory = newInventory(true);
        UUID inventoryId = UUID.randomUUID();
        when(inventoryReader.findById(inventoryId)).thenReturn(memberInventory);
        UUID excludedId = UUID.randomUUID();
        when(inventoryExcludedMemberReader.findAllByInventoryId(inventoryId)).thenReturn(
                List.of(InventoryExcludedMember.builder().inventory(memberInventory).memberId(excludedId).build()));

        InventoryCloneTemplateInfo template = service.loadCloneTemplate(inventoryId);

        assertThat(template.getInventoryType()).isEqualTo(Inventory.InventoryType.MEMBER);
        assertThat(template.getExcludedMemberIds()).containsExactly(excludedId);
        assertThat(template.getInspectorMemberIds()).isEmpty();
        verifyNoInteractions(inventoryInspectorReader);
    }

    @Test
    void loadCloneTemplate은_관리자형이면_검수자만_담는다() {
        Inventory adminInventory = Inventory.builder()
                .title("관리자형 실사").inventoryType(Inventory.InventoryType.ADMIN).createdBy(UUID.randomUUID()).build();
        UUID inventoryId = UUID.randomUUID();
        when(inventoryReader.findById(inventoryId)).thenReturn(adminInventory);
        UUID inspectorId = UUID.randomUUID();
        when(inventoryInspectorReader.findAllByInventoryId(inventoryId)).thenReturn(
                List.of(InventoryInspector.builder().inventory(adminInventory).memberId(inspectorId).build()));

        InventoryCloneTemplateInfo template = service.loadCloneTemplate(inventoryId);

        assertThat(template.getInventoryType()).isEqualTo(Inventory.InventoryType.ADMIN);
        assertThat(template.getInspectorMemberIds()).containsExactly(inspectorId);
        assertThat(template.getExcludedMemberIds()).isEmpty();
        verifyNoInteractions(inventoryExcludedMemberReader);
    }

    // ------------------------------------------------------------------
    // S-310/311 임직원 셀프서비스 - 소유권 기반(메뉴 권한과 무관) 확인/이상보고
    // ------------------------------------------------------------------

    @Test
    void loadMyStatus는_진행중인_조사가_없으면_hasActiveInventory가_false다() {
        UUID myId = UUID.randomUUID();
        when(loginUserContext.getUserId()).thenReturn(myId);
        when(inventoryResultReader.findAllByMemberIdAndInventoryStatus(myId, Inventory.Status.IN_PROGRESS))
                .thenReturn(List.of());

        InventoryMyStatusInfo status = service.loadMyStatus();

        assertThat(status.isHasActiveInventory()).isFalse();
        assertThat(status.getResults()).isEmpty();
    }

    @Test
    void loadMyStatus는_본인_대상_목록을_돌려준다() {
        UUID myId = UUID.randomUUID();
        when(loginUserContext.getUserId()).thenReturn(myId);
        Inventory inventory = newInventory(false);
        InventoryResult mine = newResult(inventory, myId);
        when(inventoryResultReader.findAllByMemberIdAndInventoryStatus(myId, Inventory.Status.IN_PROGRESS))
                .thenReturn(List.of(mine));

        InventoryMyStatusInfo status = service.loadMyStatus();

        assertThat(status.isHasActiveInventory()).isTrue();
        assertThat(status.getInventoryId()).isEqualTo(inventory.getId());
        assertThat(status.getResults()).hasSize(1);
    }

    @Test
    void selfConfirmResult는_본인_소유_대상이면_승인옵션에_따라_확인처리된다() {
        UUID myId = UUID.randomUUID();
        when(loginUserContext.getUserId()).thenReturn(myId);
        Inventory inventory = newInventory(false);
        InventoryResult mine = newResult(inventory, myId);
        when(inventoryResultReader.findByInventoryTargetId(any())).thenReturn(mine);

        service.selfConfirmResult(UUID.randomUUID());

        assertThat(mine.getStatus()).isEqualTo(InventoryResult.Status.CONFIRMED);
        verify(inventoryResultStore).store(mine);
    }

    @Test
    void selfConfirmResult는_타인_소유_대상이면_예외() {
        UUID myId = UUID.randomUUID();
        UUID otherId = UUID.randomUUID();
        when(loginUserContext.getUserId()).thenReturn(myId);
        Inventory inventory = newInventory(false);
        InventoryResult othersResult = newResult(inventory, otherId);
        when(inventoryResultReader.findByInventoryTargetId(any())).thenReturn(othersResult);

        assertThatThrownBy(() -> service.selfConfirmResult(UUID.randomUUID())).isInstanceOf(InvalidParamException.class);
        verify(inventoryResultStore, never()).store(any());
    }

    @Test
    void selfConfirmResult는_종료된_조사면_예외() {
        UUID myId = UUID.randomUUID();
        when(loginUserContext.getUserId()).thenReturn(myId);
        Inventory inventory = newInventory(false);
        inventory.close(UUID.randomUUID());
        InventoryResult mine = newResult(inventory, myId);
        when(inventoryResultReader.findByInventoryTargetId(any())).thenReturn(mine);

        assertThatThrownBy(() -> service.selfConfirmResult(UUID.randomUUID())).isInstanceOf(InvalidParamException.class);
    }

    @Test
    void selfConfirmResultWithoutScan은_본인_소유_대상이면_사진과_함께_승인대기로_전환된다() {
        UUID myId = UUID.randomUUID();
        when(loginUserContext.getUserId()).thenReturn(myId);
        Inventory inventory = newInventory(false); // 승인옵션이 꺼져 있어도
        InventoryResult mine = newResult(inventory, myId);
        when(inventoryResultReader.findByInventoryTargetId(any())).thenReturn(mine);
        UUID photoFileId = UUID.randomUUID();
        java.time.OffsetDateTime now = java.time.OffsetDateTime.now();

        service.selfConfirmResultWithoutScan(UUID.randomUUID(), photoFileId, now, now);

        assertThat(mine.getStatus()).isEqualTo(InventoryResult.Status.PENDING_APPROVAL); // 항상 승인대기(I3 예외)
        assertThat(mine.getPhotoFileId()).isEqualTo(photoFileId);
        verify(inventoryResultStore).store(mine);
    }

    @Test
    void selfReportWrongHolder는_본인_소유_대상이면_타인보유로_기록된다() {
        UUID myId = UUID.randomUUID();
        when(loginUserContext.getUserId()).thenReturn(myId);
        Inventory inventory = newInventory(false);
        InventoryResult mine = newResult(inventory, myId);
        when(inventoryResultReader.findByInventoryTargetId(any())).thenReturn(mine);

        service.selfReportWrongHolder(UUID.randomUUID(), "제 것이 아닙니다");

        assertThat(mine.getStatus()).isEqualTo(InventoryResult.Status.ANOMALY);
        assertThat(mine.getAnomalyType()).isEqualTo(InventoryResult.AnomalyType.WRONG_HOLDER);
        assertThat(mine.getNote()).isEqualTo("제 것이 아닙니다");
    }

    @Test
    void selfReportWrongHolder는_타인_소유_대상이면_예외() {
        UUID myId = UUID.randomUUID();
        UUID otherId = UUID.randomUUID();
        when(loginUserContext.getUserId()).thenReturn(myId);
        Inventory inventory = newInventory(false);
        InventoryResult othersResult = newResult(inventory, otherId);
        when(inventoryResultReader.findByInventoryTargetId(any())).thenReturn(othersResult);

        assertThatThrownBy(() -> service.selfReportWrongHolder(UUID.randomUUID(), "메모"))
                .isInstanceOf(InvalidParamException.class);
    }
}
