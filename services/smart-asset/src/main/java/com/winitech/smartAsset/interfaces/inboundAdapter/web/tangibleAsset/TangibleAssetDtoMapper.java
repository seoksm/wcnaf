package com.winitech.smartAsset.interfaces.inboundAdapter.web.tangibleAsset;

import com.winitech.smartAsset.domain.assetAssignment.AssetAssignmentInfo;
import com.winitech.smartAsset.domain.assetHistory.AssetHistoryInfo;
import com.winitech.smartAsset.domain.disposalAsset.DisposalAssetRowInfo;
import com.winitech.smartAsset.domain.tangibleAsset.TangibleAsset;
import com.winitech.smartAsset.domain.tangibleAsset.TangibleAssetCommand;
import com.winitech.smartAsset.domain.tangibleAsset.TangibleAssetDisposalCommand;
import com.winitech.smartAsset.domain.tangibleAsset.TangibleAssetInfo;
import com.winitech.smartAsset.interfaces.inboundAdapter.spec.*;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;
import org.mapstruct.factory.Mappers;

import java.time.LocalDate;
import java.time.OffsetDateTime;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface TangibleAssetDtoMapper {
    TangibleAssetDtoMapper INSTANCE = Mappers.getMapper(TangibleAssetDtoMapper.class);

    TangibleAssetResponseDto toTangibleAssetResponseDto(TangibleAssetInfo tangibleAssetInfo);

    TangibleAssetCommand toRegisterRequestCommand(TangibleAssetRegisterRequestDto tangibleAssetRegisterRequestDto);

    TangibleAssetCommand.UpdateCommand toModifyRequestCommand(TangibleAssetModifyRequestDto tangibleAssetModifyRequestDto);

    AssetAssignmentResponseDto toAssetAssignmentResponseDto(AssetAssignmentInfo assetAssignmentInfo);

    TangibleAssetCommand.BatchUpdateCommand toBatchUpdateCommand(TangibleAssetBatchModifyRequestDto tangibleAssetBatchModifyRequestDto);

    AssetHistoryResponseDto toAssetHistoryResponseDto(AssetHistoryInfo assetHistoryInfo);

    TangibleAssetDisposalCommand toDisposalCommand(TangibleAssetDisposeRequestDto tangibleAssetDisposeRequestDto);

    DisposalAssetRowResponseDto toDisposalAssetRowResponseDto(DisposalAssetRowInfo disposalAssetRowInfo);

    // 순수 날짜(LocalDate) <-> OffsetDateTime 변환 시 자정 기준으로 하면 타임존에 따라
    // 하루가 밀리는 문제가 생기므로, 정오(UTC)를 고정 기준으로 사용해 날짜 경계를 피한다.
    default OffsetDateTime map(LocalDate value) {
        return value == null ? null : value.atTime(12, 0).atOffset(java.time.ZoneOffset.UTC);
    }

    default LocalDate map(OffsetDateTime value) {
        return value == null ? null : value.toLocalDate();
    }

    // DisposalAssetRowResponseDto.lifeStatus는 DISUSE/DISPOSED 2종만 있는 좁은 열거형이라(S-240
    // 목록은 이 둘만 조회하므로), MapStruct 기본 열거형 자동 매핑에 맡기면 TangibleAsset.LifeStatus의
    // 나머지 값(USE 등)에 대한 매핑이 없다는 경고/오류가 날 수 있다 - 이 값들은 실제로는 절대 여기에
    // 들어오지 않으므로 이름 기준으로 직접 변환한다.
    default DisposalAssetRowResponseDto.LifeStatusEnum map(TangibleAsset.LifeStatus value) {
        return value == null ? null : DisposalAssetRowResponseDto.LifeStatusEnum.valueOf(value.name());
    }
}
