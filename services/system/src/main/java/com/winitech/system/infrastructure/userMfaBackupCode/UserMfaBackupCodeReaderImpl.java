package com.winitech.system.infrastructure.userMfaBackupCode;

import com.winitech.system.domain.userMfaBackupCode.UserMfaBackupCode;
import com.winitech.system.domain.userMfaBackupCode.UserMfaBackupCodeReader;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;

@Slf4j
@Component
@RequiredArgsConstructor
public class UserMfaBackupCodeReaderImpl implements UserMfaBackupCodeReader {
    private final UserMfaBackupCodeRepository userMfaBackupCodeRepository;

    @Override
    public List<UserMfaBackupCode> getUnusedBackupCodesByUserId(UUID userId) {
        return userMfaBackupCodeRepository.findByUserIdAndUsedAtIsNull(userId);
    }
}
