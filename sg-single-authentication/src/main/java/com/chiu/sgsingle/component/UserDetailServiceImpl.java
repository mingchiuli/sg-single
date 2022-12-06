package com.chiu.sgsingle.component;

import com.chiu.sgsingle.entity.UserEntity;
import com.chiu.sgsingle.service.UserService;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class UserDetailServiceImpl implements UserDetailsService {
	UserService userService;

	public UserDetailServiceImpl(UserService userService) {
		this.userService = userService;
	}

	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		Optional<UserEntity> sysUser = userService.findByUsername(username);

		if (sysUser.isEmpty()) {
			throw new UsernameNotFoundException("用户名错误!");
		}

		//通过User去自动比较用户名和密码
		return new User(sysUser.get().getUsername(),
				sysUser.get().getPassword(),
				true,
				true,
				true,
				sysUser.get().getStatus() == 0,
				AuthorityUtils.createAuthorityList("ROLE_" + sysUser.get().getRole()));
	}
}
