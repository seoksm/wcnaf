package com.winitech.system.domain.userMfaChallenge;

import java.util.Optional;
import java.util.UUID;

public interface UserMfaChallengeReader {
    Optional<UserMfaChallenge> findById(UUID id);
}
