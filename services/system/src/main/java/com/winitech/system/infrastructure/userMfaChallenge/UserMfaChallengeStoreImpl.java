package com.winitech.system.infrastructure.userMfaChallenge;

import com.winitech.system.domain.userMfaChallenge.UserMfaChallenge;
import com.winitech.system.domain.userMfaChallenge.UserMfaChallengeStore;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class UserMfaChallengeStoreImpl implements UserMfaChallengeStore {
    private final UserMfaChallengeRepository userMfaChallengeRepository;

    @Override
    public UserMfaChallenge store(UserMfaChallenge challenge) {
        return userMfaChallengeRepository.save(challenge);
    }
}
