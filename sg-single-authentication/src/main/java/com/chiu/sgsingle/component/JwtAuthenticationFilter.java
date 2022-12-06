package com.chiu.sgsingle.component;

import com.chiu.sgsingle.jwt.JwtUtils;
import com.chiu.sgsingle.lang.Result;
import com.chiu.sgsingle.repository.UserRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.preauth.PreAuthenticatedAuthenticationToken;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.io.IOException;

@Component
public class JwtAuthenticationFilter extends BasicAuthenticationFilter {

	ObjectMapper objectMapper;

	JwtUtils jwtUtils;

	UserRepository userRepository;

	RedisTemplate<String, Object> redisTemplate;

	public JwtAuthenticationFilter(AuthenticationManager authenticationManager, ObjectMapper objectMapper, JwtUtils jwtUtils, UserRepository userRepository, RedisTemplate<String, Object> redisTemplate) {
		super(authenticationManager);
		this.jwtUtils = jwtUtils;
		this.objectMapper = objectMapper;
		this.userRepository = userRepository;
		this.redisTemplate = redisTemplate;
	}

	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain) throws IOException, ServletException {

		String jwt = request.getHeader(jwtUtils.getHeader());
		if (!StringUtils.hasLength(jwt)) {
			chain.doFilter(request, response);
			return;
		}

		Authentication authentication;

		try {
			authentication = getAuthentication(jwt);
		} catch (JwtException e) {
			response.setContentType(MediaType.APPLICATION_JSON_VALUE);
			response.getWriter().write(objectMapper.writeValueAsString(Result.fail(401, e.getMessage())));
			return;
		}

		//非白名单资源、接口都要走这个流程，没有set就不能访问
		SecurityContextHolder.getContext().setAuthentication(authentication);

		chain.doFilter(request, response);
	}

	private Authentication getAuthentication(String jwt) {
		Claims claim = jwtUtils.getClaimByToken(jwt);
		if (claim == null) {
			throw new JwtException("token异常，请重新登录");
		}
		if (jwtUtils.isTokenExpired(claim.getExpiration())) {
			throw new JwtException("token已过期，请重新登录");
		}

		String username = claim.getSubject();
		String role = (String) claim.get("authorization");
		return new PreAuthenticatedAuthenticationToken(username,
				"",
				AuthorityUtils.createAuthorityList(role));
	}
}
