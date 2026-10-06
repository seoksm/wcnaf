package com.winitech.smartAsset.interfaces.inboundAdapter.web.ackTemplate;

import com.winitech.common.annotation.ForceDefaultTenant;
import com.winitech.common.response.CommonResponse;
import com.winitech.smartAsset.application.ackTemplate.AckTemplateFacade;
import com.winitech.smartAsset.domain.ackTemplate.AckTemplate;
import com.winitech.smartAsset.interfaces.inboundAdapter.spec.*;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@ForceDefaultTenant
@RequiredArgsConstructor
public class AckTemplateApiController implements AckTemplateApi {

    private final AckTemplateFacade ackTemplateFacade;

    @Override
    public CommonResponse<List<AckTemplateResponseDto>> searchAllAckTemplate() {
        List<AckTemplateResponseDto> res = ackTemplateFacade.getTemplates().stream()
                .map(AckTemplateDtoMapper.INSTANCE::toAckTemplateResponseDto)
                .collect(Collectors.toList());
        return CommonResponse.success(res);
    }

    @Override
    public CommonResponse<String> modifyAckTemplate(String type, AckTemplateModifyRequestDto dto) {
        ackTemplateFacade.updateTemplate(AckTemplate.Type.valueOf(type), dto.getBodyTpl());
        return CommonResponse.success("OK");
    }
}
