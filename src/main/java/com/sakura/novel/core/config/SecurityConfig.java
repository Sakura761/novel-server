package com.sakura.novel.core.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
/**
 * Security配置类
 * 禁用Spring Security的默认安全配置，只使用密码加密功能
 */
@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
        public SecurityFilterChain securityFilterChain (HttpSecurity http) throws Exception {
            return http
                    .authorizeHttpRequests(auth -> auth
                                    .requestMatchers("/bookshelf/**").authenticated()
                                    .anyRequest().permitAll()
                            // 需要登录才能访问的接口
                            )
                    .csrf(csrf -> csrf.disable())
                    .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                    .headers(headers -> headers.frameOptions().disable())
                    .build();
        }
    }