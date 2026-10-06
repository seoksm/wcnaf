package com.winitech.smartAsset.interfaces.inboundAdapter.web.vendor;

import com.winitech.smartAsset.domain.vendor.VendorCommand;
import com.winitech.smartAsset.domain.vendor.VendorInfo;
import com.winitech.smartAsset.interfaces.inboundAdapter.spec.*;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;
import org.mapstruct.factory.Mappers;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface VendorDtoMapper {
    VendorDtoMapper INSTANCE = Mappers.getMapper(VendorDtoMapper.class);

    VendorResponseDto toVendorResponseDto(VendorInfo info);

    VendorCommand toRegisterRequestCommand(VendorRegisterRequestDto dto);

    VendorCommand.UpdateCommand toModifyRequestCommand(VendorModifyRequestDto dto);
}
