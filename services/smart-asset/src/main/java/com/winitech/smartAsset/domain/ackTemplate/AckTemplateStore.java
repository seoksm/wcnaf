package com.winitech.smartAsset.domain.ackTemplate;

public interface AckTemplateStore {

    void modify(AckTemplate ackTemplate, String bodyTpl, java.util.UUID updatedBy);
}
