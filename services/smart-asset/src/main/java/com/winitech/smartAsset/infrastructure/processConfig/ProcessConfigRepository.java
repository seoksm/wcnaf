package com.winitech.smartAsset.infrastructure.processConfig;

import com.winitech.smartAsset.domain.processConfig.ProcessConfig;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface ProcessConfigRepository extends JpaRepository<ProcessConfig, UUID> {
}
