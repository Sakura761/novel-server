package com.sakura.novel.utils;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

/**
 * 用户上下文工具类
 * 用于在控制器中获取当前登录用户的信息
 */
public class UserContextUtil {

    /**
     * 获取当前登录用户的ID
     */
    public static Integer getCurrentUserId() {
        HttpServletRequest request = getCurrentRequest();
        if (request == null) {
            return null;
        }

        Object userId = request.getAttribute("userId");
        return userId instanceof Integer ? (Integer) userId : null;
    }

    /**
     * 获取当前登录用户的用户名
     */
    public static String getCurrentUsername() {
        HttpServletRequest request = getCurrentRequest();
        if (request == null) {
            return null;
        }

        Object username = request.getAttribute("username");
        return username instanceof String ? (String) username : null;
    }

    /**
     * 验证当前用户是否有权限访问指定用户的资源
     * @param targetUserId 目标用户ID
     * @return 是否有权限
     */
    public static boolean hasPermissionForUser(Integer targetUserId) {
        Integer currentUserId = getCurrentUserId();
        return currentUserId != null && currentUserId.equals(targetUserId);
    }

    /**
     * 获取当前请求对象
     */
    private static HttpServletRequest getCurrentRequest() {
        ServletRequestAttributes attributes =
            (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        return attributes != null ? attributes.getRequest() : null;
    }
}
