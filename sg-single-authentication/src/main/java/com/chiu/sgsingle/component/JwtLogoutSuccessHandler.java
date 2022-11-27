package com.chiu.sgsingle.component;

import com.chiu.sgsingle.jwt.JwtUtils;
import com.chiu.sgsingle.lang.Result;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.ServletOutputStream;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.logout.LogoutSuccessHandler;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.stereotype.Component;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

@Component
public class JwtLogoutSuccessHandler implements LogoutSuccessHandler {

	ObjectMapper objectMapper;

	JwtUtils jwtUtils;

	RedisTemplate<String, Object> redisTemplate;

	private final static SecurityContextLogoutHandler logoutHandler = new SecurityContextLogoutHandler();


	@Autowired
	public void setObjectMapper(ObjectMapper objectMapper) {
		this.objectMapper = objectMapper;
	}

	@Autowired
	public void setJwtUtils(JwtUtils jwtUtils) {
		this.jwtUtils = jwtUtils;
	}

	@Autowired
	public void setRedisTemplate(RedisTemplate<String, Object> redisTemplate) {
		this.redisTemplate = redisTemplate;
	}

	@Override
	public void onLogoutSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException {

		logoutHandler.logout(request, response, authentication);

		response.setContentType("application/json;charset=UTF-8");
		ServletOutputStream outputStream = response.getOutputStream();

		response.setHeader(jwtUtils.getHeader(), "");

		Result<String> result = Result.success("");

		outputStream.write(objectMapper.writeValueAsString(result).getBytes(StandardCharsets.UTF_8));

		outputStream.flush();
		outputStream.close();
	}

}
