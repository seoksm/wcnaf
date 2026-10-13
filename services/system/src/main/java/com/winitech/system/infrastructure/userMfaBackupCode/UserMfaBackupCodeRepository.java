package com.winitech.system.infrastructure.userMfaBackupCode;

import com.winitech.system.domain.userMfaBackupCode.UserMfaBackupCode;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface UserMfaBackupCodeRepository extends JpaRepository<UserMfaBackupCode, UUID> {
    List<UserMfaBackupCode> findByUserIdAndUsedAtIsNull(UUID userId);

    long deleteByUserId(UUID userId);
}
