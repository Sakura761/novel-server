package com.sakura.novel.DTO.Response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import java.time.LocalDateTime;

/**
 * 用户登录响应DTO
 */
@Data
@Schema(name = "UserLoginResponse", description = "用户登录响应数据")
public class UserLoginResponse {

    @Schema(description = "用户ID", example = "1")
    private Integer id;

    @Schema(description = "用户名", example = "testuser")
    private String username;

    @Schema(description = "昵称", example = "测试用户")
    private String nickname;

    @Schema(description = "邮箱地址", example = "test@example.com")
    private String email;

    @Schema(description = "头像URL", example = "https://cdn.jsdelivr.net/gh/user/repo@main/avatars/avatar.jpg")
    private String avatarUrl;

    @Schema(description = "账号状态", example = "active", allowableValues = {"active", "banned", "inactive"})
    private String status;

    @Schema(description = "注册时间", example = "2024-01-01T10:00:00")
    private LocalDateTime createTime;

    @Schema(description = "JWT令牌（预留字段）", example = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...")
    private String token; // JWT令牌（如果需要的话）
}
