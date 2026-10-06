package com.winitech.smartAsset.domain.ackTemplate;

import java.util.List;

public interface AckTemplateService {

    List<AckTemplateInfo> loadTemplates();

    void updateTemplate(AckTemplate.Type type, String bodyTpl);
}
