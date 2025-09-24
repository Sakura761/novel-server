package com.sakura.novel.DTO.Response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 用户登录DTO
 */
@Data
@Schema(name = "UserLoginDTO", description = "用户登录请求数据")
public class UserLoginDTO {

    @Schema(description = "用户名", required = true, example = "testuser")
    private String username;

    @Schema(description = "密码", required = true, example = "password123")
    private String password;
}
