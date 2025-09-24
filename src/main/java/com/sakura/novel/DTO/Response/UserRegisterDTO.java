package com.sakura.novel.DTO.Response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

/**
 * 用户注册DTO
 */
@Data
@Schema(name = "UserRegisterDTO", description = "用户注册请求数据")
public class UserRegisterDTO {

    @Schema(description = "用户名", required = true, example = "testuser", minLength = 3, maxLength = 20)
    private String username;

    @Schema(description = "密码", required = true, example = "password123", minLength = 6, maxLength = 30)
    private String password;

    @Schema(description = "昵称", required = false, example = "测试用户", maxLength = 50)
    private String nickname;

    @Schema(description = "邮箱地址", required = false, example = "test@example.com")
    private String email;

    @Schema(description = "头像文件", required = false, type = "string", format = "binary")
    private MultipartFile avatar; // 头像文件
}
