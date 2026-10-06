package com.winitech.smartAsset.infrastructure.ackTemplate;

import com.winitech.smartAsset.domain.ackTemplate.AckTemplate;
import com.winitech.smartAsset.domain.ackTemplate.AckTemplateReader;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

import java.util.List;

@Slf4j
@Repository
@RequiredArgsConstructor
public class AckTemplateReaderImpl implements AckTemplateReader {

    private final AckTemplateRepository ackTemplateRepository;

    @Override
    public List<AckTemplate> findAll() {
        return ackTemplateRepository.findAll();
    }

    @Override
    public AckTemplate findByType(AckTemplate.Type type) {
        return ackTemplateRepository.findByType(type).orElseThrow();
    }
}
