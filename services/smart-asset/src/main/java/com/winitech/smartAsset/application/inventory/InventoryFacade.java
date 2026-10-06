package com.winitech.smartAsset.application.inventory;

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
import com.winitech.smartAsset.domain.inventory.InventoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class InventoryFacade {

    private final InventoryService inventoryService;

    public Page<InventoryInfo> getInventoryList(Integer page, Integer size, String sort) {
        return inventoryService.loadInventoryList(page, size, sort);
    }

    public InventoryInfo getInventory(UUID inventoryId) {
        return inventoryService.loadInventory(inventoryId);
    }

    public InventoryPreviewInfo previewMemberInventory(List<UUID> excludedMemberIds) {
        return inventoryService.previewMemberInventory(excludedMemberIds);
    }

    public UUID registerMemberInventory(InventoryMemberCreateCommand command) {
        return inventoryService.createMemberInventory(command);
    }

    public InventoryPreviewInfo previewAdminInventory() {
        return inventoryService.previewAdminInventory();
    }

    public UUID registerAdminInventory(InventoryAdminCreateCommand command) {
        return inventoryService.createAdminInventory(command);
    }

    public InventoryProgressInfo getProgress(UUID inventoryId) {
        return inventoryService.loadProgress(inventoryId);
    }

    public List<InventoryResultRowInfo> getResultRows(UUID inventoryId, String status) {
        InventoryResult.Status statusFilter = status == null || status.isBlank() ? null : InventoryResult.Status.valueOf(status);
        return inventoryService.loadResultRows(inventoryId, statusFilter);
    }

    public void approveResult(UUID inventoryTargetId) {
        inventoryService.approveResult(inventoryTargetId);
    }

    public void rejectResult(UUID inventoryTargetId, String reason) {
        inventoryService.rejectResult(inventoryTargetId, reason);
    }

    public void adminConfirmResult(UUID inventoryTargetId) {
        inventoryService.adminConfirmResult(inventoryTargetId);
    }

    public void adminReportAnomaly(UUID inventoryTargetId, InventoryResult.AnomalyType anomalyType, String note) {
        inventoryService.adminReportAnomaly(inventoryTargetId, anomalyType, note);
    }

    public void closeResult(UUID inventoryTargetId, InventoryResult.ClosureAction closureAction,
                             InventoryResult.ClosureReasonCode closureReasonCode, String note) {
        inventoryService.closeResult(inventoryTargetId, closureAction, closureReasonCode, note);
    }

    public void closeResultsBulk(List<UUID> inventoryTargetIds, InventoryResult.ClosureAction closureAction,
                                  InventoryResult.ClosureReasonCode closureReasonCode, String note) {
        inventoryService.closeResultsBulk(inventoryTargetIds, closureAction, closureReasonCode, note);
    }

    public void closeInventory(UUID inventoryId) {
        inventoryService.closeInventory(inventoryId);
    }

    public InventoryReportInfo getReport(UUID inventoryId) {
        return inventoryService.loadReport(inventoryId);
    }

    public List<InventoryScheduleInfo> getSchedules() {
        return inventoryService.loadSchedules();
    }

    public InventoryCloneTemplateInfo getCloneTemplate(UUID inventoryId) {
        return inventoryService.loadCloneTemplate(inventoryId);
    }

    public InventoryMyStatusInfo getMyStatus() {
        return inventoryService.loadMyStatus();
    }

    public void selfConfirmResult(UUID inventoryTargetId) {
        inventoryService.selfConfirmResult(inventoryTargetId);
    }

    public void selfConfirmResultWithoutScan(UUID inventoryTargetId, UUID photoFileId,
                                              java.time.OffsetDateTime capturedAt, java.time.OffsetDateTime uploadedAt) {
        inventoryService.selfConfirmResultWithoutScan(inventoryTargetId, photoFileId, capturedAt, uploadedAt);
    }

    public void selfReportWrongHolder(UUID inventoryTargetId, String note) {
        inventoryService.selfReportWrongHolder(inventoryTargetId, note);
    }
}
