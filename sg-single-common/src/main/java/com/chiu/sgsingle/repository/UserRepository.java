package com.chiu.sgsingle.repository;

import com.chiu.sgsingle.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;

/**
 * @author mingchiuli
 * @create 2022-11-27 11:53 am
 */
@Repository
public interface UserRepository extends JpaRepository<UserEntity, Long> {
    Optional<UserEntity> findByUsername(String username);

    @Query(value = "UPDATE UserEntity user set user.lastLogin = ?2 where user.username = ?1")
    @Modifying
    @Transactional
    void updateLoginTime(String username, LocalDateTime time);

    @Query(value = "SELECT new UserEntity (id, username, avatar, email, role) from UserEntity where username = ?1")
    Optional<UserEntity> retrieveUserInfo(String username);

    @Query(value = "SELECT new UserEntity (username) from UserEntity where id = ?1")
    Optional<UserEntity> findUsernameById(Long userId);
}
