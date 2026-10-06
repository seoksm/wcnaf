package com.winitech.system.infrastructure.user;

import com.winitech.system.domain.user.User;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
* com.winitech.user.infrastructure
* ㄴ UserRepository.java
* @author : 박준희 과장 (부설연구소)
* @since : 2021-12-15 오후 5:09
* @see : None
 **/
public interface UserRepository extends JpaRepository<User, UUID> {
    Optional<User> findByIdAndStatus(UUID id, User.Status status);
    @EntityGraph(attributePaths = {"department"})
    List<User> findByIdInAndStatus(Collection<UUID> ids, User.Status status);
    Optional<User> findByUsernameAndStatus(String username, User.Status status);
    Optional<User> findByUsername(String username);
    @EntityGraph(attributePaths = {"department"})
    List<User> findAllByStatus(User.Status status);

    @EntityGraph(attributePaths = {"department"})
    List<User> findAllByStatusAndIdIn(User.Status status, Collection<UUID> userIds);

    @EntityGraph(attributePaths = {"department"})
    List<User> findAllByStatusAndJoinStatus(User.Status status, User.JoinStatus joinStatus);
    boolean existsByEmailAndStatus(String email, User.Status status);
}
