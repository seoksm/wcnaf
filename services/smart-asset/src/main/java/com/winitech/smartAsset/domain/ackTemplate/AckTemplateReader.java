package com.winitech.smartAsset.domain.ackTemplate;

import java.util.List;

public interface AckTemplateReader {

    List<AckTemplate> findAll();

    AckTemplate findByType(AckTemplate.Type type);
}
