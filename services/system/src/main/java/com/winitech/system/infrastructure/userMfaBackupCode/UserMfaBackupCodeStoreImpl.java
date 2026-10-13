package com.winitech.system.infrastructure.userMfaBackupCode;

import com.winitech.system.domain.userMfaBackupCode.UserMfaBackupCode;
import com.winitech.system.domain.userMfaBackupCode.UserMfaBackupCodeStore;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Slf4j
@Component
@RequiredArgsConstructor
public class UserMfaBackupCodeStoreImpl implements UserMfaBackupCodeStore {
    private final UserMfaBackupCodeRepository userMfaBackupCodeRepository;

    @Override
    public List<UserMfaBackupCode> storeAll(List<UserMfaBackupCode> codes) {
        return userMfaBackupCodeRepository.saveAll(codes);
    }

    @Override
    public UserMfaBackupCode store(UserMfaBackupCode code) {
        return userMfaBackupCodeRepository.save(code);
    }

    @Override
    @Transactional
    public void removeAllByUserId(UUID userId) {
        userMfaBackupCodeRepository.deleteByUserId(userId);
    }
}
