package com.winitech.system.infrastructure.user;

import com.winitech.common.exception.EntityNotFoundException;
import com.winitech.system.domain.user.User;
import com.winitech.system.domain.user.UserReader;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;

/**
* com.winitech.user.infrastructure
* ㄴ UserReaderImpl.java
* @author : 박준희 과장 (부설연구소)
* @since : 2021-12-15 오후 5:11
* @see : None
 **/
@Slf4j
@Component
@RequiredArgsConstructor
public class UserReaderImpl implements UserReader {
    private final UserRepository userRepository;

    @Override
    public User getUser(UUID id) {
        return userRepository.findByIdAndStatus(id, User.Status.ENABLE)
                .orElseThrow(EntityNotFoundException::new);
    }
    
    @Override
    public List<User> getUserListById(List<UUID> idList) {
        return userRepository.findByIdInAndStatus(idList, User.Status.ENABLE);
    }
 
    @Override
    public User getUserByUsername(String username) {
        return userRepository.findByUsernameAndStatus(username, User.Status.ENABLE)
                .orElseThrow(EntityNotFoundException::new);
    }

    @Override
    public List<User> getAllUser() {
        return userRepository.findAllByStatus(User.Status.ENABLE);
    }

    @Override
    public List<User> getAllUser(List<UUID> userIds) {
        return userRepository.findAllByStatusAndIdIn(User.Status.ENABLE, userIds);
    }

    @Override
    public List<User> getAllJoinedUser() {
        return userRepository.findAllByStatusAndJoinStatus(User.Status.ENABLE, User.JoinStatus.ACCEPTED);
    }

    @Override
    public boolean existsByEmail(String email) {
        return userRepository.existsByEmailAndStatus(email, User.Status.ENABLE);
    }
}
