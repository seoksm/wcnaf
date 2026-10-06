package com.winitech.smartAsset.domain.license;

import org.springframework.data.domain.Page;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

public interface LicenseService {

    UUID createLicense(LicenseCommand command);

    void updateLicense(LicenseCommand.UpdateCommand updateCommand);

    void deleteLicense(UUID licenseId);

    Page<LicenseInfo> loadList(String keyword, Integer page, Integer size);

    LicenseInfo loadLicense(UUID licenseId);

    List<LicensePurchaseRecordInfo> loadPurchaseRecords(UUID licenseId);

    UUID addPurchaseRecord(UUID licenseId, UUID vendorId, LocalDate purchaseDate, Integer quantity,
                            BigDecimal unitPrice, BigDecimal totalAmount, String memo);

    void updatePurchaseRecord(UUID licensePurchaseRecordId, UUID vendorId, LocalDate purchaseDate, Integer quantity,
                               BigDecimal unitPrice, BigDecimal totalAmount, String memo);

    List<LicenseAssignedUserInfo> loadAssignedUsers(UUID licenseId);

    /** 내 라이선스(S-550) - 임직원 본인에게 배정된(유효한) 라이선스 목록 */
    List<LicenseAssignedUserInfo> loadMyAssignedLicenses();

    /** Q-47: 잔여 수량이 부족해도 막지 않고 배정은 허용한다(LicenseInfo.overAssigned로 화면 경고) */
    UUID assignUser(UUID licenseId, UUID memberId, UUID licensePurchaseRecordId);

    /** S-550 회수 요청 - 반환값은 서비스데스크 RETURN 티켓 생성에 필요한 라이선스명·본인ID를 위해서다 */
    LicenseAssignedUserInfo requestRelease(UUID licenseAssignedUserId);

    void releaseUser(UUID licenseAssignedUserId);

    List<LicenseIncludedSoftwareInfo> loadIncludedSoftware(UUID licenseId);

    /** R2: 이름으로 소프트웨어를 찾거나 새로 만들어 포함 SW로 추가한다 */
    UUID addIncludedSoftware(UUID licenseId, String softwareName);

    void removeIncludedSoftware(UUID licenseIncludedSoftwareId);
}
