package com.sakura.novel.core.config;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

/**
 * Security配置类
 * 禁用Spring Security的默认安全配置，只使用密码加密功能
 */
@Configuration
@EnableWebSecurity
@RequiredArgsConstructor // [!code word:新增] 使用 Lombok 构造函数注入
public class SecurityConfig {

    // [!code focus:start]
    // 注入你自定义的JWT过滤器
    private final JwtAuthenticationFilter jwtAuthenticationFilter;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        return http
                // 配置请求授权规则
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/api/bookshelf/**").authenticated() // 需要登录才能访问的接口
                        .anyRequest().permitAll() // 其他所有请求都允许
                )
                // 禁用 CSRF
                .csrf(csrf -> csrf.disable())
                // 配置 Session 管理为 STATELESS（无状态），因为我们用JWT
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                // 禁用 frameOptions
                .headers(headers -> headers.frameOptions().disable())

                // 关键改动：将你的JWT过滤器添加到Spring Security的过滤器链中
                // 这会确保在每次请求的认证流程中，你的过滤器都会被执行
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)

                .build();
    }
    // [!code focus:end]
}