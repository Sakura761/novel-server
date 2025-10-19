package com.sakura.novel.DTO.Request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

/**
 * 用户注册DTO
 */
@Data
@Schema(name = "UserRegisterDTO", description = "用户注册请求数据")
public class UserRegisterReqDTO {

    @Schema(description = "用户名", example = "tester", minLength = 3, maxLength = 20)
    private String username;

    @Schema(description = "密码", example = "password123", minLength = 6, maxLength = 30)
    private String password;

    @Schema(description = "昵称", example = "测试用户", maxLength = 50)
    private String nickname;

    @Schema(description = "邮箱地址", example = "test@example.com")
    private String email;

    @Schema(description = "头像文件", type = "string", format = "binary")
    private MultipartFile avatar; // 头像文件
}
