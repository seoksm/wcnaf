package com.winitech.smartAsset.interfaces.inboundAdapter.web.depreciation;

import com.winitech.smartAsset.domain.depreciation.DepreciationConfirmationInfo;
import com.winitech.smartAsset.domain.depreciation.DepreciationRowInfo;
import com.winitech.smartAsset.domain.depreciation.DepreciationScheduleRowInfo;
import com.winitech.smartAsset.domain.depreciation.DepreciationSummaryInfo;
import com.winitech.smartAsset.interfaces.inboundAdapter.spec.DepreciationConfirmationResponseDto;
import com.winitech.smartAsset.interfaces.inboundAdapter.spec.DepreciationRowResponseDto;
import com.winitech.smartAsset.interfaces.inboundAdapter.spec.DepreciationScheduleRowResponseDto;
import com.winitech.smartAsset.interfaces.inboundAdapter.spec.DepreciationSummaryResponseDto;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;
import org.mapstruct.factory.Mappers;

import java.time.LocalDate;
import java.time.OffsetDateTime;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface DepreciationDtoMapper {
    DepreciationDtoMapper INSTANCE = Mappers.getMapper(DepreciationDtoMapper.class);

    DepreciationRowResponseDto toDepreciationRowResponseDto(DepreciationRowInfo info);

    DepreciationSummaryResponseDto toDepreciationSummaryResponseDto(DepreciationSummaryInfo info);

    DepreciationScheduleRowResponseDto toDepreciationScheduleRowResponseDto(DepreciationScheduleRowInfo info);

    DepreciationConfirmationResponseDto toDepreciationConfirmationResponseDto(DepreciationConfirmationInfo info);

    // 순수 날짜(LocalDate) <-> OffsetDateTime 변환 시 자정 기준으로 하면 타임존에 따라
    // 하루가 밀리는 문제가 생기므로, 정오(UTC)를 고정 기준으로 사용해 날짜 경계를 피한다.
    default OffsetDateTime map(LocalDate value) {
        return value == null ? null : value.atTime(12, 0).atOffset(java.time.ZoneOffset.UTC);
    }
}
