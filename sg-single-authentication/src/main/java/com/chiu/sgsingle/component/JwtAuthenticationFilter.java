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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
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


	@Autowired
	public void setObjectMapper(ObjectMapper objectMapper) {
		this.objectMapper = objectMapper;
	}

	@Autowired
	public void setRedisTemplate(RedisTemplate<String, Object> redisTemplate) {
		this.redisTemplate = redisTemplate;
	}

	@Autowired
	public void setJwtUtils(JwtUtils jwtUtils) {
		this.jwtUtils = jwtUtils;
	}

	@Autowired
	public void setSysUserService(UserRepository userRepository) {
		this.userRepository = userRepository;
	}

	public JwtAuthenticationFilter(AuthenticationManager authenticationManager) {
		super(authenticationManager);
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
		} catch (Exception e) {
			response.setContentType("application/json;charset=utf-8");
			response.getWriter().write(objectMapper.writeValueAsString(Result.fail(401, e.getMessage(), null)));
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
		return new PreAuthenticatedAuthenticationToken(username, null, AuthorityUtils.createAuthorityList(role));
	}
}
