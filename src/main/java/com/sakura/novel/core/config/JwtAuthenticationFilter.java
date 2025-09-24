package com.sakura.novel.core.config;

import com.sakura.novel.utils.JwtUtil;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
// [!code focus:start]
// 引入 Spring Security 核心类
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
// [!code focus:end]
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.ArrayList; // [!code word:新增]

/**
 * JWT认证过滤器
 * 负责解析token并将用户信息设置到Spring Security的上下文中
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtUtil jwtUtil;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {

        // 从请求中获取JWT token
        String token = getTokenFromRequest(request);

        // [!code focus:start]
        // 核心改动：如果token有效，则设置Spring Security的认证信息
        if (StringUtils.hasText(token) && jwtUtil.validateToken(token)) {
            Integer userId = jwtUtil.getUserIdFromToken(token);
            String username = jwtUtil.getUsernameFromToken(token);

            if (userId != null && username != null) {
                // 注意：在真实的复杂应用中，你可能需要从数据库加载用户的权限信息
                // 这里为了简化，我们创建一个包含用户名的 UserDetails 对象，并赋予一个空的权限列表
                UserDetails userDetails = new User(username, "", new ArrayList<>());

                // 创建一个 Authentication 对象，这是Spring Security的核心
                // 第一个参数是principal(当事人)，通常是UserDetails对象
                // 第二个参数是credentials(凭证)，对于JWT来说，这里是null
                // 第三个参数是authorities(权限)
                UsernamePasswordAuthenticationToken authentication =
                        new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());

                // 将 Authentication 对象设置到 SecurityContextHolder 中
                // 这样，Spring Security的后续过滤器就知道当前请求已经通过了身份验证
                SecurityContextHolder.getContext().setAuthentication(authentication);

                // (可选) 你仍然可以设置request attribute，方便在Controller中直接获取
                request.setAttribute("userId", userId);
                request.setAttribute("username", username);
            }
        }
        // [!code focus:end]

        // 无论token是否存在或是否有效，都必须继续执行过滤器链
        // 如果token无效，SecurityContextHolder中将没有Authentication对象
        // Spring Security的授权过滤器会因此拒绝需要认证的请求
        filterChain.doFilter(request, response);
    }

    /**
     * 从请求中获取JWT token
     */
    private String getTokenFromRequest(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }
        return null;
    }
}