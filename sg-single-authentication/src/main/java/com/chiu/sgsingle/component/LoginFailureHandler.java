package com.chiu.sgsingle.component;

import com.chiu.sgsingle.lang.Result;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.ServletOutputStream;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.MediaType;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

@Component
public class LoginFailureHandler implements AuthenticationFailureHandler {
	ObjectMapper objectMapper;

	public LoginFailureHandler(ObjectMapper objectMapper) {
		this.objectMapper = objectMapper;
	}


	@Override
	public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response, AuthenticationException exception) throws IOException {

		response.setContentType(MediaType.APPLICATION_JSON_VALUE);
		ServletOutputStream outputStream = response.getOutputStream();

		Result<Object> result = Result.fail(401, exception.getMessage());

		outputStream.write(objectMapper.writeValueAsString(result).getBytes(StandardCharsets.UTF_8));

		outputStream.flush();
		outputStream.close();
	}
}
