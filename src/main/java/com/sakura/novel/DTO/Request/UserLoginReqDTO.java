package com.sakura.novel.DTO.Request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 用户登录DTO
 */
@Data
@Schema(name = "UserLoginDTO", description = "用户登录请求数据")
public class UserLoginReqDTO {

    @Schema(description = "用户名", example = "testuser")
    private String username;

    @Schema(description = "密码", example = "password123")
    private String password;
}
