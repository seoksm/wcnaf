package com.winitech.smartAsset.domain.license;

import com.winitech.common.bean.LoginUserContext;
import com.winitech.common.exception.InvalidParamException;
import com.winitech.smartAsset.domain.software.Software;
import com.winitech.smartAsset.domain.software.SoftwareService;
import com.winitech.smartAsset.domain.vendor.Vendor;
import com.winitech.smartAsset.domain.vendor.VendorReader;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.egovframe.rte.fdl.cmmn.EgovAbstractServiceImpl;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Slf4j
@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class LicenseServiceImpl extends EgovAbstractServiceImpl implements LicenseService {

    private static final int DEFAULT_PAGE_SIZE = 20;
    private static final int MAX_PAGE_SIZE = 200;
    private static final Sort DEFAULT_SORT = Sort.by(Sort.Direction.DESC, "createAt");

    private final LicenseReader licenseReader;
    private final LicenseStore licenseStore;
    private final LicensePurchaseRecordReader purchaseRecordReader;
    private final LicensePurchaseRecordStore purchaseRecordStore;
    private final LicenseAssignedUserReader assignedUserReader;
    private final LicenseAssignedUserStore assignedUserStore;
    private final LicenseIncludedSoftwareReader includedSoftwareReader;
    private final LicenseIncludedSoftwareStore includedSoftwareStore;
    private final VendorReader vendorReader;
    private final SoftwareService softwareService;
    private final LoginUserContext loginUserContext;

    @Transactional
    @Override
    public UUID createLicense(LicenseCommand command) {
        return licenseStore.store(command.toEntity());
    }

    @Transactional
    @Override
    public void updateLicense(LicenseCommand.UpdateCommand updateCommand) {
        License license = licenseReader.findById(updateCommand.getLicenseId());
        licenseStore.modify(license, updateCommand);
    }

    @Transactional
    @Override
    public void deleteLicense(UUID licenseId) {
        License license = licenseReader.findById(licenseId);
        licenseStore.delete(license);
    }

    @Override
    public Page<LicenseInfo> loadList(String keyword, Integer page, Integer size) {
        return licenseReader.findAll(keyword, toPageable(page, size))
                .map(this::toLicenseInfo);
    }

    @Override
    public LicenseInfo loadLicense(UUID licenseId) {
        return toLicenseInfo(licenseReader.findById(licenseId));
    }

    private LicenseInfo toLicenseInfo(License license) {
        int purchased = purchaseRecordReader.sumQuantityByLicenseId(license.getId());
        int assigned = assignedUserReader.countActiveByLicenseId(license.getId());
        return new LicenseInfo(license, purchased, assigned);
    }

    @Override
    public List<LicensePurchaseRecordInfo> loadPurchaseRecords(UUID licenseId) {
        return purchaseRecordReader.findAllByLicenseId(licenseId).stream()
                .map(LicensePurchaseRecordInfo::new).collect(Collectors.toList());
    }

    @Transactional
    @Override
    public UUID addPurchaseRecord(UUID licenseId, UUID vendorId, LocalDate purchaseDate, Integer quantity,
                                   BigDecimal unitPrice, BigDecimal totalAmount, String memo) {
        License license = licenseReader.findById(licenseId);
        Vendor vendor = vendorId != null ? vendorReader.findById(vendorId) : null;
        LicensePurchaseRecord record = LicensePurchaseRecord.builder()
                .license(license)
                .vendor(vendor)
                .purchaseDate(purchaseDate)
                .quantity(quantity)
                .unitPrice(unitPrice)
                .totalAmount(totalAmount)
                .memo(memo)
                .build();
        return purchaseRecordStore.store(record).getId();
    }

    @Transactional
    @Override
    public void updatePurchaseRecord(UUID licensePurchaseRecordId, UUID vendorId, LocalDate purchaseDate, Integer quantity,
                                      BigDecimal unitPrice, BigDecimal totalAmount, String memo) {
        LicensePurchaseRecord record = purchaseRecordReader.findById(licensePurchaseRecordId);
        Vendor vendor = vendorId != null ? vendorReader.findById(vendorId) : null;
        purchaseRecordStore.modify(record, vendor, purchaseDate, quantity, unitPrice, totalAmount, memo);
    }

    @Override
    public List<LicenseAssignedUserInfo> loadAssignedUsers(UUID licenseId) {
        return assignedUserReader.findAllByLicenseId(licenseId).stream()
                .map(LicenseAssignedUserInfo::new).collect(Collectors.toList());
    }

    @Override
    public List<LicenseAssignedUserInfo> loadMyAssignedLicenses() {
        return assignedUserReader.findAllActiveByMemberId(loginUserContext.getUserId()).stream()
                .map(LicenseAssignedUserInfo::new).collect(Collectors.toList());
    }

    @Transactional
    @Override
    public UUID assignUser(UUID licenseId, UUID memberId, UUID licensePurchaseRecordId) {
        License license = licenseReader.findById(licenseId);
        LicensePurchaseRecord record = licensePurchaseRecordId != null
                ? purchaseRecordReader.findById(licensePurchaseRecordId) : null;
        LicenseAssignedUser assignedUser = LicenseAssignedUser.builder()
                .license(license)
                .memberId(memberId)
                .licensePurchaseRecord(record)
                .assignedBy(loginUserContext.getUserId())
                .build();
        return assignedUserStore.store(assignedUser).getId();
    }

    @Transactional
    @Override
    public LicenseAssignedUserInfo requestRelease(UUID licenseAssignedUserId) {
        LicenseAssignedUser assignedUser = assignedUserReader.findById(licenseAssignedUserId);
        if (!assignedUser.getMemberId().equals(loginUserContext.getUserId())) {
            throw new InvalidParamException("본인에게 배정된 라이선스만 회수 요청할 수 있습니다.");
        }
        assignedUserStore.requestRelease(assignedUser);
        return new LicenseAssignedUserInfo(assignedUser);
    }

    @Transactional
    @Override
    public void releaseUser(UUID licenseAssignedUserId) {
        LicenseAssignedUser assignedUser = assignedUserReader.findById(licenseAssignedUserId);
        assignedUserStore.release(assignedUser);
    }

    @Override
    public List<LicenseIncludedSoftwareInfo> loadIncludedSoftware(UUID licenseId) {
        return includedSoftwareReader.findAllByLicenseId(licenseId).stream()
                .map(LicenseIncludedSoftwareInfo::new).collect(Collectors.toList());
    }

    @Transactional
    @Override
    public UUID addIncludedSoftware(UUID licenseId, String softwareName) {
        License license = licenseReader.findById(licenseId);
        Software software = softwareService.findOrCreateByName(softwareName);
        LicenseIncludedSoftware includedSoftware = LicenseIncludedSoftware.builder()
                .license(license)
                .software(software)
                .build();
        return includedSoftwareStore.store(includedSoftware).getId();
    }

    @Transactional
    @Override
    public void removeIncludedSoftware(UUID licenseIncludedSoftwareId) {
        includedSoftwareStore.remove(licenseIncludedSoftwareId);
    }

    private Pageable toPageable(Integer page, Integer size) {
        int safePage = (page == null || page < 0) ? 0 : page;
        int safeSize = (size == null) ? DEFAULT_PAGE_SIZE : Math.max(1, Math.min(size, MAX_PAGE_SIZE));
        return PageRequest.of(safePage, safeSize, DEFAULT_SORT);
    }
}
