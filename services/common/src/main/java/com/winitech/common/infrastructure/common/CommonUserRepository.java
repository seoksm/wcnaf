package com.winitech.common.infrastructure.common;

import com.winitech.common.domain.common.CommonUser;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * <pre>
 * com.winitech.common.infrastructure.common
 * └ CommonUserRepository.java
 * </pre>
 * @author : coding (클라우드팀)
 * @since : 2025-01-21 12:59
 **/
public interface CommonUserRepository extends JpaRepository<CommonUser, UUID> {
	List<CommonUser> findAllByStatus(CommonUser.Status status);

	Optional<CommonUser> findByIdAndStatus(UUID id, CommonUser.Status status);

	List<CommonUser> findAllByFullNameAndStatus(String fullName, CommonUser.Status status);
}
