package com.chiu.sgsingle.component;

import com.chiu.sgsingle.entity.UserEntity;
import com.chiu.sgsingle.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class UserDetailServiceImpl implements UserDetailsService {
	RedisTemplate<String, Object> redisTemplate;
	UserRepository userRepository;

	public UserDetailServiceImpl(RedisTemplate<String, Object> redisTemplate, UserRepository userRepository) {
		this.redisTemplate = redisTemplate;
		this.userRepository = userRepository;
	}

	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		UserEntity sysUser = userRepository.findByUsername(username);

		Optional<UserEntity> optional = Optional.ofNullable(sysUser);

		if (optional.isEmpty()) {
			throw new UsernameNotFoundException("用户名错误!");
		}

		//通过User去自动比较用户名和密码
		return new User(optional.get().getUsername(), optional.get().getPassword(), true,true,true, optional.get().getStatus() == 0, AuthorityUtils.createAuthorityList("ROLE_" + optional.get().getRole()));
	}
}
