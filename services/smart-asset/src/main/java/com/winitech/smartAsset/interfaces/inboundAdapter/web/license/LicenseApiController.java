package com.winitech.smartAsset.interfaces.inboundAdapter.web.license;

import com.winitech.common.annotation.ForceDefaultTenant;
import com.winitech.common.response.CommonResponse;
import com.winitech.smartAsset.application.license.LicenseFacade;
import com.winitech.smartAsset.domain.license.LicenseCommand;
import com.winitech.smartAsset.domain.license.LicenseInfo;
import com.winitech.smartAsset.interfaces.inboundAdapter.spec.*;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@RestController
@ForceDefaultTenant
@RequiredArgsConstructor
public class LicenseApiController implements LicenseApi {

    private final LicenseFacade licenseFacade;

    @Override
    public CommonResponse<LicenseIdResponseDto> registerLicense(LicenseRegisterRequestDto dto) {
        LicenseCommand command = LicenseDtoMapper.INSTANCE.toRegisterRequestCommand(dto);
        UUID id = licenseFacade.postLicense(command);
        return CommonResponse.success(LicenseIdResponseDto.builder().licenseId(id).build());
    }

    @Override
    public CommonResponse<LicenseIdResponseDto> modifyLicense(UUID licenseId, LicenseModifyRequestDto dto) {
        LicenseCommand.UpdateCommand updateCommand = LicenseDtoMapper.INSTANCE.toModifyRequestCommand(dto);
        updateCommand.setLicenseId(licenseId);
        licenseFacade.reviseLicense(updateCommand);
        return CommonResponse.success(LicenseIdResponseDto.builder().licenseId(licenseId).build());
    }

    @Override
    public CommonResponse<String> removeLicense(UUID licenseId) {
        licenseFacade.removeLicense(licenseId);
        return CommonResponse.success("OK");
    }

    @Override
    public CommonResponse<LicensePageResponseDto> searchAllLicense(String keyword, Integer page, Integer size) {
        Page<LicenseInfo> licensePage = licenseFacade.getList(keyword, page, size);

        List<LicenseResponseDto> content = licensePage.getContent().stream()
                .map(LicenseDtoMapper.INSTANCE::toLicenseResponseDto)
                .collect(Collectors.toList());

        LicensePageResponseDto res = LicensePageResponseDto.builder()
                .content(content)
                .currentPage(licensePage.getNumber())
                .pageSize(licensePage.getSize())
                .totalElements(licensePage.getTotalElements())
                .totalPages(licensePage.getTotalPages())
                .build();
        return CommonResponse.success(res);
    }

    @Override
    public CommonResponse<LicenseResponseDto> searchLicense(UUID licenseId) {
        return CommonResponse.success(LicenseDtoMapper.INSTANCE.toLicenseResponseDto(licenseFacade.getLicense(licenseId)));
    }

    @Override
    public CommonResponse<List<LicensePurchaseRecordResponseDto>> searchAllLicensePurchaseRecord(UUID licenseId) {
        List<LicensePurchaseRecordResponseDto> res = licenseFacade.getPurchaseRecords(licenseId).stream()
                .map(LicenseDtoMapper.INSTANCE::toLicensePurchaseRecordResponseDto)
                .collect(Collectors.toList());
        return CommonResponse.success(res);
    }

    @Override
    public CommonResponse<LicensePurchaseRecordIdResponseDto> registerLicensePurchaseRecord(UUID licenseId, LicensePurchaseRecordRegisterRequestDto dto) {
        UUID id = licenseFacade.addPurchaseRecord(
                licenseId,
                dto.getVendorId(),
                dto.getPurchaseDate().toLocalDate(),
                dto.getQuantity(),
                dto.getUnitPrice(),
                dto.getTotalAmount(),
                dto.getMemo());
        return CommonResponse.success(LicensePurchaseRecordIdResponseDto.builder().licensePurchaseRecordId(id).build());
    }

    @Override
    public CommonResponse<String> modifyLicensePurchaseRecord(UUID licensePurchaseRecordId, LicensePurchaseRecordRegisterRequestDto dto) {
        licenseFacade.updatePurchaseRecord(
                licensePurchaseRecordId,
                dto.getVendorId(),
                dto.getPurchaseDate().toLocalDate(),
                dto.getQuantity(),
                dto.getUnitPrice(),
                dto.getTotalAmount(),
                dto.getMemo());
        return CommonResponse.success("OK");
    }

    @Override
    public CommonResponse<List<LicenseAssignedUserResponseDto>> searchAllLicenseAssignedUser(UUID licenseId) {
        List<LicenseAssignedUserResponseDto> res = licenseFacade.getAssignedUsers(licenseId).stream()
                .map(LicenseDtoMapper.INSTANCE::toLicenseAssignedUserResponseDto)
                .collect(Collectors.toList());
        return CommonResponse.success(res);
    }

    @Override
    public CommonResponse<List<LicenseAssignedUserResponseDto>> searchMyLicense() {
        List<LicenseAssignedUserResponseDto> res = licenseFacade.getMyAssignedLicenses().stream()
                .map(LicenseDtoMapper.INSTANCE::toLicenseAssignedUserResponseDto)
                .collect(Collectors.toList());
        return CommonResponse.success(res);
    }

    @Override
    public CommonResponse<LicenseAssignedUserIdResponseDto> assignLicenseUser(UUID licenseId, LicenseAssignedUserRegisterRequestDto dto) {
        UUID id = licenseFacade.assignUser(licenseId, dto.getMemberId(), dto.getLicensePurchaseRecordId());
        return CommonResponse.success(LicenseAssignedUserIdResponseDto.builder().licenseAssignedUserId(id).build());
    }

    @Override
    public CommonResponse<String> requestReleaseLicenseAssignedUser(UUID licenseAssignedUserId) {
        licenseFacade.requestRelease(licenseAssignedUserId);
        return CommonResponse.success("OK");
    }

    @Override
    public CommonResponse<String> releaseLicenseAssignedUser(UUID licenseAssignedUserId) {
        licenseFacade.releaseUser(licenseAssignedUserId);
        return CommonResponse.success("OK");
    }

    @Override
    public CommonResponse<List<LicenseIncludedSoftwareResponseDto>> searchAllLicenseIncludedSoftware(UUID licenseId) {
        List<LicenseIncludedSoftwareResponseDto> res = licenseFacade.getIncludedSoftware(licenseId).stream()
                .map(LicenseDtoMapper.INSTANCE::toLicenseIncludedSoftwareResponseDto)
                .collect(Collectors.toList());
        return CommonResponse.success(res);
    }

    @Override
    public CommonResponse<LicenseIncludedSoftwareIdResponseDto> registerLicenseIncludedSoftware(UUID licenseId, LicenseIncludedSoftwareRegisterRequestDto dto) {
        UUID id = licenseFacade.addIncludedSoftware(licenseId, dto.getSoftwareName());
        return CommonResponse.success(LicenseIncludedSoftwareIdResponseDto.builder().licenseIncludedSoftwareId(id).build());
    }

    @Override
    public CommonResponse<String> removeLicenseIncludedSoftware(UUID licenseIncludedSoftwareId) {
        licenseFacade.removeIncludedSoftware(licenseIncludedSoftwareId);
        return CommonResponse.success("OK");
    }
}
