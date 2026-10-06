package com.winitech.smartAsset.application.license;

import com.winitech.smartAsset.domain.license.LicenseAssignedUserInfo;
import com.winitech.smartAsset.domain.license.LicenseCommand;
import com.winitech.smartAsset.domain.license.LicenseIncludedSoftwareInfo;
import com.winitech.smartAsset.domain.license.LicenseInfo;
import com.winitech.smartAsset.domain.license.LicensePurchaseRecordInfo;
import com.winitech.smartAsset.domain.license.LicenseService;
import com.winitech.smartAsset.domain.ticket.TicketService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class LicenseFacade {

    private final LicenseService licenseService;
    private final TicketService ticketService;

    public UUID postLicense(LicenseCommand command) {
        return licenseService.createLicense(command);
    }

    public void reviseLicense(LicenseCommand.UpdateCommand updateCommand) {
        licenseService.updateLicense(updateCommand);
    }

    public void removeLicense(UUID licenseId) {
        licenseService.deleteLicense(licenseId);
    }

    public Page<LicenseInfo> getList(String keyword, Integer page, Integer size) {
        return licenseService.loadList(keyword, page, size);
    }

    public LicenseInfo getLicense(UUID licenseId) {
        return licenseService.loadLicense(licenseId);
    }

    public List<LicensePurchaseRecordInfo> getPurchaseRecords(UUID licenseId) {
        return licenseService.loadPurchaseRecords(licenseId);
    }

    public UUID addPurchaseRecord(UUID licenseId, UUID vendorId, LocalDate purchaseDate, Integer quantity,
                                   BigDecimal unitPrice, BigDecimal totalAmount, String memo) {
        return licenseService.addPurchaseRecord(licenseId, vendorId, purchaseDate, quantity, unitPrice, totalAmount, memo);
    }

    public void updatePurchaseRecord(UUID licensePurchaseRecordId, UUID vendorId, LocalDate purchaseDate, Integer quantity,
                                      BigDecimal unitPrice, BigDecimal totalAmount, String memo) {
        licenseService.updatePurchaseRecord(licensePurchaseRecordId, vendorId, purchaseDate, quantity, unitPrice, totalAmount, memo);
    }

    public List<LicenseAssignedUserInfo> getAssignedUsers(UUID licenseId) {
        return licenseService.loadAssignedUsers(licenseId);
    }

    public List<LicenseAssignedUserInfo> getMyAssignedLicenses() {
        return licenseService.loadMyAssignedLicenses();
    }

    public UUID assignUser(UUID licenseId, UUID memberId, UUID licensePurchaseRecordId) {
        return licenseService.assignUser(licenseId, memberId, licensePurchaseRecordId);
    }

    /** S-550 회수 요청 - 서비스데스크 RETURN 티켓을 함께 생성한다(설계문서 §3 "회수 요청 처리") */
    public void requestRelease(UUID licenseAssignedUserId) {
        LicenseAssignedUserInfo info = licenseService.requestRelease(licenseAssignedUserId);
        ticketService.createReturnTicket(licenseAssignedUserId, info.getMemberId(), info.getLicenseName());
    }

    public void releaseUser(UUID licenseAssignedUserId) {
        licenseService.releaseUser(licenseAssignedUserId);
    }

    public List<LicenseIncludedSoftwareInfo> getIncludedSoftware(UUID licenseId) {
        return licenseService.loadIncludedSoftware(licenseId);
    }

    public UUID addIncludedSoftware(UUID licenseId, String softwareName) {
        return licenseService.addIncludedSoftware(licenseId, softwareName);
    }

    public void removeIncludedSoftware(UUID licenseIncludedSoftwareId) {
        licenseService.removeIncludedSoftware(licenseIncludedSoftwareId);
    }
}
