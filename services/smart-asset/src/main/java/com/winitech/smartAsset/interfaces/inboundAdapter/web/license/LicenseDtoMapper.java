package com.winitech.smartAsset.interfaces.inboundAdapter.web.license;

import com.winitech.smartAsset.domain.license.LicenseAssignedUserInfo;
import com.winitech.smartAsset.domain.license.LicenseCommand;
import com.winitech.smartAsset.domain.license.LicenseIncludedSoftwareInfo;
import com.winitech.smartAsset.domain.license.LicenseInfo;
import com.winitech.smartAsset.domain.license.LicensePurchaseRecordInfo;
import com.winitech.smartAsset.interfaces.inboundAdapter.spec.*;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;
import org.mapstruct.factory.Mappers;

import java.time.LocalDate;
import java.time.OffsetDateTime;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface LicenseDtoMapper {
    LicenseDtoMapper INSTANCE = Mappers.getMapper(LicenseDtoMapper.class);

    LicenseResponseDto toLicenseResponseDto(LicenseInfo info);

    LicenseCommand toRegisterRequestCommand(LicenseRegisterRequestDto dto);

    LicenseCommand.UpdateCommand toModifyRequestCommand(LicenseModifyRequestDto dto);

    LicensePurchaseRecordResponseDto toLicensePurchaseRecordResponseDto(LicensePurchaseRecordInfo info);

    LicenseAssignedUserResponseDto toLicenseAssignedUserResponseDto(LicenseAssignedUserInfo info);

    LicenseIncludedSoftwareResponseDto toLicenseIncludedSoftwareResponseDto(LicenseIncludedSoftwareInfo info);

    default OffsetDateTime map(LocalDate value) {
        return value == null ? null : value.atTime(12, 0).atOffset(java.time.ZoneOffset.UTC);
    }

    default LocalDate map(OffsetDateTime value) {
        return value == null ? null : value.toLocalDate();
    }
}
