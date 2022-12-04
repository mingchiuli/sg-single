package com.chiu.sgsingle.component;

import com.chiu.sgsingle.exception.CaptchaException;
import com.chiu.sgsingle.lang.Const;
import com.chiu.sgsingle.lang.Result;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.micrometer.common.util.StringUtils;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.MediaType;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class CaptchaFilter extends OncePerRequestFilter {

	ObjectMapper objectMapper;

	RedisTemplate<String, Object> redisTemplate;

	LoginFailureHandler loginFailureHandler;

	public CaptchaFilter(ObjectMapper objectMapper, RedisTemplate<String, Object> redisTemplate, LoginFailureHandler loginFailureHandler) {
		this.objectMapper = objectMapper;
		this.redisTemplate = redisTemplate;
		this.loginFailureHandler = loginFailureHandler;
	}

	@Override
	protected void doFilterInternal(HttpServletRequest request, @NonNull HttpServletResponse response, @NonNull FilterChain filterChain) throws ServletException, IOException {

		String url = request.getRequestURI();

		if ("/login".equals(url) && request.getMethod().equals("POST")) {
			// 校验验证码
			try {
				validate(request);
			} catch (CaptchaException e) {
				response.setContentType(MediaType.APPLICATION_JSON_VALUE);
				response.getWriter().write(objectMapper.writeValueAsString(Result.fail(400, e.getMessage(), null)));
				return;
			}

		}
		filterChain.doFilter(request, response);
	}

	// 校验验证码逻辑
	private void validate(HttpServletRequest httpServletRequest) {

		String code = httpServletRequest.getParameter("code");
		String key = httpServletRequest.getParameter("key");

		if (StringUtils.isBlank(code) || StringUtils.isBlank(key)) {
			throw new CaptchaException("验证码无效");
		}

		if (!code.equals(redisTemplate.opsForValue().get(Const.CAPTCHA_KEY + key))) {
			redisTemplate.delete(Const.CAPTCHA_KEY + key);
			throw new CaptchaException("验证码错误");
		}

	}
}
