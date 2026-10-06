package com.winitech.smartAsset.infrastructure.ackTemplate;

import com.winitech.smartAsset.domain.ackTemplate.AckTemplate;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface AckTemplateRepository extends JpaRepository<AckTemplate, UUID> {

    Optional<AckTemplate> findByType(AckTemplate.Type type);
}
