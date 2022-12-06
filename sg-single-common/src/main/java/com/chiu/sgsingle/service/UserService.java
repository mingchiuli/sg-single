package com.chiu.sgsingle.service;

import com.chiu.sgsingle.entity.UserEntity;

import java.time.LocalDateTime;
import java.util.Optional;

/**
 * @author mingchiuli
 * @create 2022-12-04 4:55 pm
 */
public interface UserService {

    Optional<UserEntity> findUsernameById(Long userId);

    Optional<UserEntity> findByUsername(String username);

    Optional<UserEntity> retrieveUserInfo(String username);

    void updateLoginTime(String username, LocalDateTime time);


}
