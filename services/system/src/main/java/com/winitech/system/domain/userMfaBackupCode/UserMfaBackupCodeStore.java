package com.winitech.system.domain.userMfaBackupCode;

import java.util.List;
import java.util.UUID;

public interface UserMfaBackupCodeStore {
    List<UserMfaBackupCode> storeAll(List<UserMfaBackupCode> codes);

    UserMfaBackupCode store(UserMfaBackupCode code);

    /** 재발급 시 기존 코드를 전부 무효화하기 위해 삭제한다. */
    void removeAllByUserId(UUID userId);
}
