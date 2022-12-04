package com.chiu.sgsingle.service.impl;

import com.chiu.sgsingle.entity.UserEntity;
import com.chiu.sgsingle.repository.UserRepository;
import com.chiu.sgsingle.service.UserService;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * @author mingchiuli
 * @create 2022-12-04 4:55 pm
 */
@Service
public class UserServiceImpl implements UserService {

    UserRepository userRepository;

    public UserServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public Optional<UserEntity> findUsernameById(Long userId) {
        return userRepository.findUsernameById(userId);
    }

    @Override
    public Optional<UserEntity> findByUsername(String username) {
        return userRepository.findByUsername(username);
    }

    @Override
    public Optional<UserEntity> retrieveUserInfo(String username) {
        return userRepository.retrieveUserInfo(username);
    }

    @Override
    public void updateLoginTime(String username, LocalDateTime time) {
        userRepository.updateLoginTime(username, time);
    }

}
