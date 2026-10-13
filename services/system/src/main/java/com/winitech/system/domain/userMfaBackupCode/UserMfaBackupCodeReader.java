package com.winitech.system.domain.userMfaBackupCode;

import java.util.List;
import java.util.UUID;

public interface UserMfaBackupCodeReader {
    List<UserMfaBackupCode> getUnusedBackupCodesByUserId(UUID userId);
}
