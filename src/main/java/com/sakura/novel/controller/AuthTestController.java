package com.sakura.novel.controller;

import com.sakura.novel.core.common.vo.ResultVO;
import com.sakura.novel.core.config.RequireJwtAuth;
import com.sakura.novel.utils.UserContextUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

/**
 * JWT认证测试控制器
 */
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@Tag(name = "认证测试", description = "JWT认证功能测试接口")
public class AuthTestController {

    /**
     * 公开接口，不需要JWT验证
     */
    @GetMapping("/public")
    @Operation(summary = "公开接口", description = "不需要JWT token的公开接口")
    public ResultVO<Map<String, Object>> publicEndpoint() {
        Map<String, Object> data = new HashMap<>();
        data.put("timestamp", System.currentTimeMillis());
        return ResultVO.success("这是一个公开接口，无需登录即可访问", data);
    }

    /**
     * 需要JWT验证的私有接口
     */
    @GetMapping("/protected")
    @RequireJwtAuth(validateUserId = false)
    @Operation(summary = "受保护接口", description = "需要JWT token的受保护接口")
    public ResultVO<Map<String, Object>> protectedEndpoint() {
        Integer currentUserId = UserContextUtil.getCurrentUserId();
        String currentUsername = UserContextUtil.getCurrentUsername();

        Map<String, Object> data = new HashMap<>();
        data.put("currentUserId", currentUserId);
        data.put("currentUsername", currentUsername);
        data.put("timestamp", System.currentTimeMillis());

        return ResultVO.success("JWT验证成功，您已通过身份认证", data);
    }

    /**
     * 需要JWT验证且验证用户ID的接口
     */
    @GetMapping("/user/{userId}/profile")
    @RequireJwtAuth(validateUserId = true)
    @Operation(summary = "用户个人资料", description = "只能访问自己的个人资料")
    public ResultVO<Map<String, Object>> getUserProfile(@PathVariable Integer userId) {
        Integer currentUserId = UserContextUtil.getCurrentUserId();
        String currentUsername = UserContextUtil.getCurrentUsername();

        Map<String, Object> data = new HashMap<>();
        data.put("userId", userId);
        data.put("currentUserId", currentUserId);
        data.put("currentUsername", currentUsername);
        data.put("timestamp", System.currentTimeMillis());

        return ResultVO.success("成功获取用户个人资料", data);
    }

    /**
     * 测试用户权限验证的接口
     */
    @PostMapping("/user/{userId}/action")
    @RequireJwtAuth(validateUserId = true)
    @Operation(summary = "用户操作", description = "只能操作自己的资源")
    public ResultVO<Map<String, Object>> userAction(@PathVariable Integer userId) {
        Map<String, Object> data = new HashMap<>();
        data.put("targetUserId", userId);
        data.put("currentUserId", UserContextUtil.getCurrentUserId());
        data.put("timestamp", System.currentTimeMillis());

        return ResultVO.success("用户操作执行成功", data);
    }
}
