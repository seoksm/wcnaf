package com.winitech.smartAsset.domain.inventory;

import org.springframework.data.domain.Page;

import java.util.List;
import java.util.UUID;

public interface InventoryService {

    Page<InventoryInfo> loadInventoryList(Integer page, Integer size, String sort);

    InventoryInfo loadInventory(UUID inventoryId);

    /** S-301 대상 미리보기 - excludedMemberIds만 반영하고 실제 저장은 하지 않는다 */
    InventoryPreviewInfo previewMemberInventory(List<UUID> excludedMemberIds);

    UUID createMemberInventory(InventoryMemberCreateCommand command);

    /** S-302 대상 미리보기 */
    InventoryPreviewInfo previewAdminInventory();

    UUID createAdminInventory(InventoryAdminCreateCommand command);

    /** S-303 진행 현황 - 4개 지표 + 참여자별 분해 */
    InventoryProgressInfo loadProgress(UUID inventoryId);

    /** S-303(자산별/이상보고 탭)·S-304·S-308 목록 - statusFilter가 null이면 전체 */
    List<InventoryResultRowInfo> loadResultRows(UUID inventoryId, InventoryResult.Status statusFilter);

    /** S-304: 승인대기 → 확인완료 */
    void approveResult(UUID inventoryTargetId);

    /** S-304: 승인대기 → 미확인(반려), I5 */
    void rejectResult(UUID inventoryTargetId, String reason);

    /**
     * 관리자 대체 확인 - S-310/311(임직원 모바일 QR 연속 스캔)이 아직 구현되지 않아, 그 입력 경로를
     * 대신하는 임시 장치. 정식 확인 경로가 준비되면 이 메서드의 실제 호출부(컨트롤러)만 걷어내면
     * 되고, 도메인 로직(InventoryResult.confirm)은 그대로 재사용된다.
     */
    void adminConfirmResult(UUID inventoryTargetId);

    /** 관리자 대체 이상 보고 - 위와 동일한 이유의 임시 장치 */
    void adminReportAnomaly(UUID inventoryTargetId, InventoryResult.AnomalyType anomalyType, String note);

    /** S-308: 미확인 항목 1건 종결 처리 결정 */
    void closeResult(UUID inventoryTargetId, InventoryResult.ClosureAction closureAction,
                      InventoryResult.ClosureReasonCode closureReasonCode, String note);

    /** S-308: 여러 건 일괄 종결 처리 - LOST(분실)는 되돌리기 어려운 동작이라 일괄 대상에서 제외(R6) */
    void closeResultsBulk(List<UUID> inventoryTargetIds, InventoryResult.ClosureAction closureAction,
                           InventoryResult.ClosureReasonCode closureReasonCode, String note);

    /** S-308: 조사 종료 확정 - 미확인 중 종결 처리가 안 된 항목이 남아있으면 실패(강제 관문) */
    void closeInventory(UUID inventoryId);

    /** S-305 리포트 - 진행 중/종료 모두 조회 가능(화면·엑셀 다운로드 공통 데이터) */
    InventoryReportInfo loadReport(UUID inventoryId);

    /** S-306 반복 시행 스케줄 - 유형별로 최근 종료된 조사 기준 다음 예정일 안내(자동 실행은 미구현) */
    List<InventoryScheduleInfo> loadSchedules();

    /** S-306 템플릿 복제 - 선택한 조사의 옵션(대상 자산 자체는 제외)을 그대로 읽어온다 */
    InventoryCloneTemplateInfo loadCloneTemplate(UUID inventoryId);

    /** S-310 "내 전수조사" - 로그인한 본인이 배정받은, 진행 중인 조사의 대상 목록(I2: 최대 1건) */
    InventoryMyStatusInfo loadMyStatus();

    /** S-311 QR 연속 스캔으로 확인 - 본인 소유 대상만 허용(소유권 검사, 메뉴 권한과 무관) */
    void selfConfirmResult(UUID inventoryTargetId);

    /** S-311 I3 예외 경로 - 라벨 없음/훼손: 스캔 없이 사진으로 확인, 항상 승인대기가 된다 */
    void selfConfirmResultWithoutScan(UUID inventoryTargetId, UUID photoFileId,
                                       java.time.OffsetDateTime capturedAt, java.time.OffsetDateTime uploadedAt);

    /** S-311 "제 자산이 아닙니다" - 본인 소유 대상에 대해 타인보유 이상 보고 */
    void selfReportWrongHolder(UUID inventoryTargetId, String note);
}
