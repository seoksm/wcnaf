package com.winitech.smartAsset.infrastructure.ackTemplate;

import com.winitech.smartAsset.domain.ackTemplate.AckTemplate;
import com.winitech.smartAsset.domain.ackTemplate.AckTemplateStore;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Slf4j
@Repository
@RequiredArgsConstructor
public class AckTemplateStoreImpl implements AckTemplateStore {

    @Override
    public void modify(AckTemplate ackTemplate, String bodyTpl, UUID updatedBy) {
        ackTemplate.updateBody(bodyTpl, updatedBy);
    }
}
