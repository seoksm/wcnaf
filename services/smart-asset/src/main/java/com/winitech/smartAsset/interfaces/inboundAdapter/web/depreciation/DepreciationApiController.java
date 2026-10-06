package com.winitech.smartAsset.interfaces.inboundAdapter.web.depreciation;

import com.winitech.common.annotation.ForceDefaultTenant;
import com.winitech.common.response.CommonResponse;
import com.winitech.smartAsset.application.depreciation.DepreciationFacade;
import com.winitech.smartAsset.domain.depreciation.DepreciationConfirmationInfo;
import com.winitech.smartAsset.domain.depreciation.DepreciationScheduleRowInfo;
import com.winitech.smartAsset.domain.depreciation.DepreciationStatusInfo;
import com.winitech.smartAsset.interfaces.inboundAdapter.spec.*;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@RestController
@ForceDefaultTenant
@RequiredArgsConstructor
public class DepreciationApiController implements DepreciationApi {

    private final DepreciationFacade depreciationFacade;

    @Override
    public CommonResponse<DepreciationStatusResponseDto> searchDepreciationStatus(Integer fiscalYear, String quarter) {
        DepreciationStatusInfo status = depreciationFacade.getStatus(fiscalYear, quarter);

        List<DepreciationRowResponseDto> rows = status.getRows().stream()
                .map(DepreciationDtoMapper.INSTANCE::toDepreciationRowResponseDto)
                .collect(Collectors.toList());
        DepreciationSummaryResponseDto summary = DepreciationDtoMapper.INSTANCE.toDepreciationSummaryResponseDto(status.getSummary());

        return CommonResponse.success(DepreciationStatusResponseDto.builder().rows(rows).summary(summary).build());
    }

    @Override
    public CommonResponse<List<DepreciationScheduleRowResponseDto>> searchTangibleAssetDepreciationSchedule(UUID tangibleAssetId, Integer fiscalYear) {
        List<DepreciationScheduleRowInfo> schedule = depreciationFacade.getSchedule(tangibleAssetId, fiscalYear);

        List<DepreciationScheduleRowResponseDto> res = schedule.stream()
                .map(DepreciationDtoMapper.INSTANCE::toDepreciationScheduleRowResponseDto)
                .collect(Collectors.toList());
        return CommonResponse.success(res);
    }

    @Override
    public CommonResponse<String> confirmDepreciation(DepreciationConfirmRequestDto depreciationConfirmRequestDto) {
        depreciationFacade.confirm(depreciationConfirmRequestDto.getFiscalYear(), depreciationConfirmRequestDto.getQuarter().getValue());
        return CommonResponse.success("OK");
    }

    @Override
    public CommonResponse<String> releaseDepreciation(DepreciationReleaseRequestDto depreciationReleaseRequestDto) {
        depreciationFacade.release(depreciationReleaseRequestDto.getFiscalYear(), depreciationReleaseRequestDto.getQuarter().getValue(),
                depreciationReleaseRequestDto.getReason());
        return CommonResponse.success("OK");
    }

    @Override
    public CommonResponse<List<DepreciationConfirmationResponseDto>> searchDepreciationConfirmationLog() {
        List<DepreciationConfirmationInfo> log = depreciationFacade.getConfirmationLog();

        List<DepreciationConfirmationResponseDto> res = log.stream()
                .map(DepreciationDtoMapper.INSTANCE::toDepreciationConfirmationResponseDto)
                .collect(Collectors.toList());
        return CommonResponse.success(res);
    }
}
