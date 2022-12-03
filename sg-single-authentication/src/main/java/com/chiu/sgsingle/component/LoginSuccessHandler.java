package com.chiu.sgsingle.component;

import com.chiu.sgsingle.entity.UserEntity;
import com.chiu.sgsingle.jwt.JwtUtils;
import com.chiu.sgsingle.lang.Result;
import com.chiu.sgsingle.repository.UserRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.ServletOutputStream;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.Principal;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;


@Component
public class LoginSuccessHandler implements AuthenticationSuccessHandler {

	ObjectMapper objectMapper;
	JwtUtils jwtUtils;
	UserRepository userRepository;

	public LoginSuccessHandler(ObjectMapper objectMapper, JwtUtils jwtUtils, UserRepository userRepository) {
		this.objectMapper = objectMapper;
		this.jwtUtils = jwtUtils;
		this.userRepository = userRepository;
	}

	@Override
	@Transactional
	public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException {
		response.setContentType("application/json;charset=UTF-8");
		ServletOutputStream outputStream = response.getOutputStream();

		// 生成jwt
		String jwt = jwtUtils.generateToken(authentication.getName(),
				authentication.getAuthorities().stream().
						findFirst().
						map(GrantedAuthority::getAuthority).
						orElse("ROLE_default"));

		Optional<Authentication> authOptional = Optional.of(authentication);


		UserEntity user = userRepository.retrieveUserInfo(authOptional.map(Principal::getName).orElseThrow()).
				orElseThrow();

		userRepository.updateLoginTime(authOptional.
				map(Principal::getName).
				orElseThrow(), LocalDateTime.now());

		HashMap<String, Object> map = new HashMap<>();
		map.put("user", user);
		map.put("token", jwt);

		Result<Map<String, Object>> success = Result.success(map);

		outputStream.write(objectMapper.writeValueAsString(success).getBytes(StandardCharsets.UTF_8));

		outputStream.flush();
		outputStream.close();
	}

}
