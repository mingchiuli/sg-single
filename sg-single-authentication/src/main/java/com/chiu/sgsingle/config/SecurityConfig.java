package com.chiu.sgsingle.config;

import com.chiu.sgsingle.component.*;
import com.chiu.sgsingle.repository.UserRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.authentication.logout.LogoutFilter;


@Configuration(proxyBeanMethods = false)
@EnableMethodSecurity
public class SecurityConfig {
    LoginFailureHandler loginFailureHandler;

    LoginSuccessHandler loginSuccessHandler;

    CaptchaFilter captchaFilter;

    JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint;

    JwtLogoutSuccessHandler jwtLogoutSuccessHandler;

    UserRepository userRepository;

    UserDetailsService userDetailsService;

    AuthenticationManager authenticationManager;

    JwtAuthenticationFilter jwtAuthenticationFilter;

    public SecurityConfig(LoginFailureHandler loginFailureHandler, LoginSuccessHandler loginSuccessHandler, CaptchaFilter captchaFilter, JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint, JwtLogoutSuccessHandler jwtLogoutSuccessHandler, UserRepository userRepository, UserDetailsService userDetailsService, AuthenticationManager authenticationManager, JwtAuthenticationFilter jwtAuthenticationFilter) {
        this.loginFailureHandler = loginFailureHandler;
        this.loginSuccessHandler = loginSuccessHandler;
        this.captchaFilter = captchaFilter;
        this.jwtAuthenticationEntryPoint = jwtAuthenticationEntryPoint;
        this.jwtLogoutSuccessHandler = jwtLogoutSuccessHandler;
        this.userRepository = userRepository;
        this.userDetailsService = userDetailsService;
        this.authenticationManager = authenticationManager;
        this.jwtAuthenticationFilter = jwtAuthenticationFilter;
    }


    private static final String[] URL_WHITELIST = {
            "/captcha",
            "/favicon.ico",
            "/blogsByYear/**",
            "/getCountByYear/**",
            "/blogs/**",
            "/blog/**",
            "/blogStatus/**",
            "/searchYears",
            "/searchByYear/**",
            "/search/**",
            "/blogToken/**",
            "/getJWT",
            "/addWebsite",
            "/searchRecent/**",
            "/searchWebsite/**",
            "/sysLog/**",
            "/cooperate/**",
            "/upload/img/**"

    };

    @Bean
    public SecurityFilterChain configure(HttpSecurity http) throws Exception {

        http.cors().and().csrf().disable()

                // ????????????
                .formLogin()
                .successHandler(loginSuccessHandler)
                .failureHandler(loginFailureHandler)

                .and()
                .logout()
                .logoutSuccessHandler(jwtLogoutSuccessHandler)

                // ??????session
                .and()
                .sessionManagement()
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS)


                // ??????????????????
                .and()
                .authorizeHttpRequests()
                .requestMatchers(URL_WHITELIST).permitAll()
                .anyRequest().authenticated()

                // ???????????????
                .and()
                .exceptionHandling()
                .authenticationEntryPoint(jwtAuthenticationEntryPoint)

                // ???????????????????????????
                .and()
                .addFilterBefore(captchaFilter, UsernamePasswordAuthenticationFilter.class)
                .addFilterBefore(jwtAuthenticationFilter, LogoutFilter.class)
                .userDetailsService(userDetailsService);

        return http.build();
    }

}
