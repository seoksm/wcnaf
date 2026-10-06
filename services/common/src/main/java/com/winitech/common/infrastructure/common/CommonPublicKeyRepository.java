package com.winitech.common.infrastructure.common;

import com.winitech.common.domain.common.CommonPublicKey;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

/**
 * <pre>
 * com.winitech.common.infrastructure.common
 * └ CommonPublicKeyRepository.java
 * </pre>
 * @author : coding (클라우드팀)
 * @since : 2024-12-30 18:30
 **/
public interface CommonPublicKeyRepository extends JpaRepository<CommonPublicKey, UUID> {
}
