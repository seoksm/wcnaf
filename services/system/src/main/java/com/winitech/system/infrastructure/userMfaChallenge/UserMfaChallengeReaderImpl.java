package com.winitech.system.infrastructure.userMfaChallenge;

import com.winitech.system.domain.userMfaChallenge.UserMfaChallenge;
import com.winitech.system.domain.userMfaChallenge.UserMfaChallengeReader;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.UUID;

@Slf4j
@Component
@RequiredArgsConstructor
public class UserMfaChallengeReaderImpl implements UserMfaChallengeReader {
    private final UserMfaChallengeRepository userMfaChallengeRepository;

    @Override
    public Optional<UserMfaChallenge> findById(UUID id) {
        return userMfaChallengeRepository.findById(id);
    }
}
