package com.winitech.smartAsset.interfaces.inboundAdapter.web.acknowledgement;

import com.winitech.smartAsset.domain.acknowledgement.AcknowledgementApprovalInfo;
import com.winitech.smartAsset.domain.acknowledgement.AcknowledgementDetailInfo;
import com.winitech.smartAsset.domain.acknowledgement.AcknowledgementInfo;
import com.winitech.smartAsset.interfaces.inboundAdapter.spec.AcknowledgementApprovalResponseDto;
import com.winitech.smartAsset.interfaces.inboundAdapter.spec.AcknowledgementDetailResponseDto;
import com.winitech.smartAsset.interfaces.inboundAdapter.spec.AcknowledgementResponseDto;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;
import org.mapstruct.factory.Mappers;

import java.time.LocalDate;
import java.time.OffsetDateTime;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface AcknowledgementDtoMapper {
    AcknowledgementDtoMapper INSTANCE = Mappers.getMapper(AcknowledgementDtoMapper.class);

    AcknowledgementResponseDto toAcknowledgementResponseDto(AcknowledgementInfo acknowledgementInfo);

    AcknowledgementDetailResponseDto toAcknowledgementDetailResponseDto(AcknowledgementDetailInfo acknowledgementDetailInfo);

    AcknowledgementApprovalResponseDto toAcknowledgementApprovalResponseDto(AcknowledgementApprovalInfo acknowledgementApprovalInfo);

    // TangibleAssetDtoMapper/LoanDtoMapper와 동일한 이유 - 순수 날짜(dueDate)를 OffsetDateTime으로
    // 직렬화할 때 자정 기준이면 타임존에 따라 하루가 밀릴 수 있어 정오(UTC)를 고정 기준으로 쓴다.
    default OffsetDateTime map(LocalDate value) {
        return value == null ? null : value.atTime(12, 0).atOffset(java.time.ZoneOffset.UTC);
    }
}
