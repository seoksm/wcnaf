package com.winitech.system.infrastructure.userMfaChallenge;

import com.winitech.system.domain.userMfaChallenge.UserMfaChallenge;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface UserMfaChallengeRepository extends JpaRepository<UserMfaChallenge, UUID> {
}
