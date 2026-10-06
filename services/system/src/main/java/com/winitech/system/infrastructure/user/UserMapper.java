package com.winitech.system.infrastructure.user;

import com.winitech.common.exception.EntityNotFoundException;
import com.winitech.system.domain.user.User;
import org.apache.ibatis.annotations.Param;
import org.egovframe.rte.psl.dataaccess.mapper.Mapper;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * <pre>
 * com.winitech.system.infrastructure.user
 * └ UserMapper.java
 * </pre>
 * @author : coding (클라우드팀)
 * @since : 2025-01-08 09:29
 **/
@Mapper
public interface UserMapper {
	Optional<User> findByIdAndStatus(@Param("id") UUID id, @Param("status") User.Status status);

	List<User> findByIdInAndStatus(List<UUID> userIdList, User.Status status);

	Optional<User> findByUsernameAndStatus(String username, User.Status status);

	List<User> findAllByStatus(User.Status status);

	List<User> findAllByStatusAndIdIn(User.Status status, List<UUID> userIdList);

	List<User> findAllByStatusAndJoinStatus(User.Status status, User.JoinStatus joinStatus);

	boolean existsByEmailAndStatus(String email, User.Status status);

	int insertUser(User user);
	
	int updateUser(User user);
}
