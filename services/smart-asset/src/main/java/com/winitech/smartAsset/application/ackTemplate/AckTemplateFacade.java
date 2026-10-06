package com.winitech.smartAsset.application.ackTemplate;

import com.winitech.smartAsset.domain.ackTemplate.AckTemplate;
import com.winitech.smartAsset.domain.ackTemplate.AckTemplateInfo;
import com.winitech.smartAsset.domain.ackTemplate.AckTemplateService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class AckTemplateFacade {

    private final AckTemplateService ackTemplateService;

    public List<AckTemplateInfo> getTemplates() {
        return ackTemplateService.loadTemplates();
    }

    public void updateTemplate(AckTemplate.Type type, String bodyTpl) {
        ackTemplateService.updateTemplate(type, bodyTpl);
    }
}
