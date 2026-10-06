package com.winitech.smartAsset.interfaces.inboundAdapter.web.intangibleAsset;

import com.winitech.smartAsset.domain.intangibleAsset.IntangibleAssetActionLogInfo;
import com.winitech.smartAsset.domain.intangibleAsset.IntangibleAssetCommand;
import com.winitech.smartAsset.domain.intangibleAsset.IntangibleAssetInfo;
import com.winitech.smartAsset.interfaces.inboundAdapter.spec.*;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;
import org.mapstruct.factory.Mappers;

import java.time.LocalDate;
import java.time.OffsetDateTime;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface IntangibleAssetDtoMapper {
    IntangibleAssetDtoMapper INSTANCE = Mappers.getMapper(IntangibleAssetDtoMapper.class);

    IntangibleAssetResponseDto toIntangibleAssetResponseDto(IntangibleAssetInfo info);

    IntangibleAssetCommand toRegisterRequestCommand(IntangibleAssetRegisterRequestDto dto);

    IntangibleAssetCommand.UpdateCommand toModifyRequestCommand(IntangibleAssetModifyRequestDto dto);

    IntangibleAssetActionLogResponseDto toActionLogResponseDto(IntangibleAssetActionLogInfo info);

    // TangibleAssetDtoMapper와 동일한 이유 - 순수 날짜 <-> OffsetDateTime 변환 시 자정 기준이면
    // 타임존에 따라 하루가 밀릴 수 있어 정오(UTC)를 고정 기준으로 쓴다.
    default OffsetDateTime map(LocalDate value) {
        return value == null ? null : value.atTime(12, 0).atOffset(java.time.ZoneOffset.UTC);
    }

    default LocalDate map(OffsetDateTime value) {
        return value == null ? null : value.toLocalDate();
    }
}
