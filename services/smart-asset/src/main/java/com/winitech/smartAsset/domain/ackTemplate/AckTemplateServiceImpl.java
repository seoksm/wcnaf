package com.winitech.smartAsset.domain.ackTemplate;

import com.winitech.common.bean.LoginUserContext;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.egovframe.rte.fdl.cmmn.EgovAbstractServiceImpl;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class AckTemplateServiceImpl extends EgovAbstractServiceImpl implements AckTemplateService {

    private final AckTemplateReader ackTemplateReader;
    private final AckTemplateStore ackTemplateStore;
    private final LoginUserContext loginUserContext;

    @Override
    public List<AckTemplateInfo> loadTemplates() {
        return ackTemplateReader.findAll().stream().map(AckTemplateInfo::new).collect(Collectors.toList());
    }

    @Transactional
    @Override
    public void updateTemplate(AckTemplate.Type type, String bodyTpl) {
        AckTemplate ackTemplate = ackTemplateReader.findByType(type);
        ackTemplateStore.modify(ackTemplate, bodyTpl, loginUserContext.getUserId());
    }
}
