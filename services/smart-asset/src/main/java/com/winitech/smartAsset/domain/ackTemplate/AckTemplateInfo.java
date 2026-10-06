package com.winitech.smartAsset.domain.ackTemplate;

import lombok.Getter;

@Getter
public class AckTemplateInfo {

    private final AckTemplate.Type type;
    private final String bodyTpl;

    public AckTemplateInfo(AckTemplate ackTemplate) {
        this.type = ackTemplate.getType();
        this.bodyTpl = ackTemplate.getBodyTpl();
    }
}
