package com.sakura.novel.entity;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 用户表实体类
 * 存储用户信息，用于认证和基本信息展示
 */
@Data
@Schema(name = "User", description = "用户实体")
public class User {

    @Schema(description = "用户ID", example = "1")
    private Integer id;

    @Schema(description = "用户名", example = "testuser")
    private String username;

    @Schema(description = "密码哈希值", hidden = true)
    private String passwordHash;

    @Schema(description = "昵称", example = "测试用户")
    private String nickname;

    @Schema(description = "邮箱地址", example = "test@example.com")
    private String email;

    @Schema(description = "头像URL", example = "https://cdn.jsdelivr.net/gh/user/repo@main/avatars/avatar.jpg")
    private String avatarUrl;

    @Schema(description = "账号状态", example = "active")
    private UserStatus status;

    @Schema(description = "创建时间", example = "2024-01-01T10:00:00")
    private LocalDateTime createTime;

    @Schema(description = "更新时间", example = "2024-01-01T10:00:00")
    private LocalDateTime updateTime;

    @Schema(description = "用户状态枚举")
    public enum UserStatus {
        @Schema(description = "正常状态")
        active,
        @Schema(description = "已封禁")
        banned,
        @Schema(description = "未激活")
        inactive
    }
}
