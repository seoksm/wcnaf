package com.winitech.smartAsset.interfaces.inboundAdapter.web.acknowledgement;

import com.winitech.common.annotation.ForceDefaultTenant;
import com.winitech.common.library.WiniCom;
import com.winitech.common.response.CommonResponse;
import com.winitech.smartAsset.application.acknowledgement.AcknowledgementFacade;
import com.winitech.smartAsset.domain.acknowledgement.Acknowledgement;
import com.winitech.smartAsset.domain.acknowledgement.AcknowledgementApproval;
import com.winitech.smartAsset.domain.acknowledgement.AcknowledgementInfo;
import com.winitech.smartAsset.domain.tangibleAsset.TangibleAsset;
import com.winitech.smartAsset.interfaces.inboundAdapter.spec.*;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@RestController
@ForceDefaultTenant
@RequiredArgsConstructor
public class AcknowledgementApiController implements AcknowledgementApi {

    private final AcknowledgementFacade acknowledgementFacade;
    private final HttpServletRequest request;

    @Override
    public CommonResponse<AcknowledgementPageResponseDto> searchAllAcknowledgement(String status, Integer page, Integer size) {
        Acknowledgement.Status statusFilter = status == null ? null : Acknowledgement.Status.valueOf(status);
        Page<AcknowledgementInfo> ackPage = acknowledgementFacade.getList(statusFilter, page, size);

        List<AcknowledgementResponseDto> content = ackPage.getContent().stream()
                .map(AcknowledgementDtoMapper.INSTANCE::toAcknowledgementResponseDto)
                .collect(Collectors.toList());

        AcknowledgementPageResponseDto res = AcknowledgementPageResponseDto.builder()
                .content(content)
                .currentPage(ackPage.getNumber())
                .pageSize(ackPage.getSize())
                .totalElements(ackPage.getTotalElements())
                .totalPages(ackPage.getTotalPages())
                .build();
        return CommonResponse.success(res);
    }

    @Override
    public CommonResponse<AcknowledgementIdResponseDto> registerAcknowledgement(AcknowledgementRegisterRequestDto dto) {
        UUID id = acknowledgementFacade.requestAcknowledgement(
                dto.getTangibleAssetId(), dto.getMemberId(), dto.getMemberName(),
                Acknowledgement.Type.valueOf(dto.getType().name()), dto.getManagerName());
        return CommonResponse.success(AcknowledgementIdResponseDto.builder().acknowledgementId(id).build());
    }

    @Override
    public CommonResponse<AcknowledgementDetailResponseDto> searchAcknowledgement(UUID acknowledgementId) {
        return CommonResponse.success(AcknowledgementDtoMapper.INSTANCE.toAcknowledgementDetailResponseDto(
                acknowledgementFacade.getDetail(acknowledgementId)));
    }

    @Override
    public CommonResponse<String> approveAcknowledgementByManager(UUID acknowledgementId, AcknowledgementManagerApproveRequestDto dto) {
        acknowledgementFacade.approveByManager(
                acknowledgementId,
                WiniCom.getClientIp(request),
                AcknowledgementApproval.ReturnCondition.valueOf(dto.getReturnCondition().name()),
                TangibleAsset.LifeStatus.valueOf(dto.getNextLifeStatus().name()),
                TangibleAsset.AssignType.valueOf(dto.getNextAssignType().name()));
        return CommonResponse.success("OK");
    }

    @Override
    public CommonResponse<String> cancelAcknowledgement(UUID acknowledgementId, AcknowledgementCancelRequestDto dto) {
        acknowledgementFacade.cancelAcknowledgement(acknowledgementId, dto.getReason());
        return CommonResponse.success("OK");
    }

    @Override
    public CommonResponse<List<AcknowledgementResponseDto>> searchMyAcknowledgement() {
        List<AcknowledgementResponseDto> res = acknowledgementFacade.getMyPending().stream()
                .map(AcknowledgementDtoMapper.INSTANCE::toAcknowledgementResponseDto)
                .collect(Collectors.toList());
        return CommonResponse.success(res);
    }

    @Override
    public CommonResponse<AcknowledgementDetailResponseDto> searchMyAcknowledgementDetail(UUID acknowledgementId) {
        return CommonResponse.success(AcknowledgementDtoMapper.INSTANCE.toAcknowledgementDetailResponseDto(
                acknowledgementFacade.getMyDetail(acknowledgementId)));
    }

    @Override
    public CommonResponse<String> approveAcknowledgementByEmployeeSelf(UUID acknowledgementId) {
        acknowledgementFacade.approveByEmployeeSelf(acknowledgementId, WiniCom.getClientIp(request));
        return CommonResponse.success("OK");
    }
}
