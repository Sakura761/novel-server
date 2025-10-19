package com.sakura.novel.controller;

import com.sakura.novel.core.common.vo.ResultVO;
import com.sakura.novel.entity.User;
import com.sakura.novel.service.UserService;
import com.sakura.novel.DTO.Request.UserRegisterReqDTO;
import com.sakura.novel.DTO.Request.UserLoginReqDTO;
import com.sakura.novel.DTO.Response.UserLoginResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 用户控制器
 */
@RestController
@RequestMapping("/api/users")
@CrossOrigin(origins = "*")
@RequiredArgsConstructor
@Tag(name = "用户管理", description = "用户相关的API接口，包括注册、登录、查询、管理等功能")
public class UserController {

    private final UserService userService;

    /**
     * 根据ID查询用户
     */
    @Operation(
        summary = "根据ID查询用户",
        description = "通过用户ID查询单个用户的详细信息"
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "查询成功"
        ),
        @ApiResponse(responseCode = "404", description = "用户不存在"),
        @ApiResponse(responseCode = "400", description = "请求参数错误"),
        @ApiResponse(responseCode = "500", description = "服务器内部错误")
    })
    @GetMapping("/{id}")
    public ResultVO<?> getUserById(
        @Parameter(description = "用户ID", required = true, example = "1")
        @PathVariable Integer id
    ) {
        try {
            User user = userService.getUserById(id);
            if (user == null) {
                return ResultVO.error(404, "用户不存在");
            }
            return ResultVO.success("查询成功", user);
        } catch (IllegalArgumentException e) {
            return ResultVO.error(400, e.getMessage());
        } catch (Exception e) {
            return ResultVO.error(500, "服务器内部错误");
        }
    }

    /**
     * 根据用户名查询用户
     */
    @Operation(
        summary = "根据用户名查询用户",
        description = "通过用户名查询单个用户的详细信息"
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "查询成功"),
        @ApiResponse(responseCode = "404", description = "用户不存在"),
        @ApiResponse(responseCode = "400", description = "请求参数错误"),
        @ApiResponse(responseCode = "500", description = "服务器内部错误")
    })
    @GetMapping("/username/{username}")
    public ResultVO<?> getUserByUsername(
        @Parameter(description = "用户名", required = true, example = "testuser")
        @PathVariable String username
    ) {
        try {
            User user = userService.getUserByUsername(username);
            if (user == null) {
                return ResultVO.error(404, "用户不存在");
            }
            return ResultVO.success("查询成功", user);
        } catch (IllegalArgumentException e) {
            return ResultVO.error(400, e.getMessage());
        } catch (Exception e) {
            return ResultVO.error(500, "服务器内部错误");
        }
    }

    /**
     * 分页查询用户
     */
    @Operation(
        summary = "分页查询用户",
        description = "分页获取用户列表，支持自定义页码和每页数量"
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "查询成功"
        ),
        @ApiResponse(responseCode = "400", description = "请求参数错误"),
        @ApiResponse(responseCode = "500", description = "服务器内部错误")
    })
    @GetMapping("/page")
    public ResultVO<?> getUsersByPage(
        @Parameter(description = "页码，从1开始", example = "1")
        @RequestParam(defaultValue = "1") int page,
        @Parameter(description = "每页数量", example = "10")
        @RequestParam(defaultValue = "10") int size
    ) {
        try {
            List<User> users = userService.getUsersByPage(page, size);
            int totalCount = userService.getTotalCount();

            Map<String, Object> data = new HashMap<>();
            data.put("users", users);
            data.put("totalCount", totalCount);
            data.put("page", page);
            data.put("size", size);
            data.put("totalPages", (int) Math.ceil((double) totalCount / size));

            return ResultVO.success("查询成功", data);
        } catch (IllegalArgumentException e) {
            return ResultVO.error(400, e.getMessage());
        } catch (Exception e) {
            return ResultVO.error(500, "服务器内部错误");
        }
    }

    /**
     * 创建用户
     */
    @Operation(
        summary = "创建用户",
        description = "创建新用户（管理员功能，建议普通用户使用注册接口）"
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "用户创建成功"),
        @ApiResponse(responseCode = "400", description = "请求参数错误"),
        @ApiResponse(responseCode = "500", description = "服务器内部错误")
    })
    @PostMapping
    public ResultVO<?> createUser(
        @Parameter(description = "用户信息", required = true)
        @RequestBody User user
    ) {
        try {
            User createdUser = userService.createUser(user);
            return ResultVO.success("用户创建成功", createdUser);
        } catch (IllegalArgumentException e) {
            return ResultVO.error(400, e.getMessage());
        } catch (Exception e) {
            return ResultVO.error(500, "服务器内部错误");
        }
    }

    /**
     * 更新用户信息
     */
    @Operation(
        summary = "更新用户信息",
        description = "根据用户ID更新用户信息"
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "用户更新成功"),
        @ApiResponse(responseCode = "400", description = "请求参数错误"),
        @ApiResponse(responseCode = "404", description = "用户不存在"),
        @ApiResponse(responseCode = "500", description = "服务器内部错误")
    })
    @PutMapping("/{id}")
    public ResultVO<?> updateUser(
        @Parameter(description = "用户ID", required = true, example = "1")
        @PathVariable Integer id,
        @Parameter(description = "用户信息", required = true)
        @RequestBody User user
    ) {
        try {
            user.setId(id);
            User updatedUser = userService.updateUser(user);
            return ResultVO.success("用户更新成功", updatedUser);
        } catch (IllegalArgumentException e) {
            return ResultVO.error(400, e.getMessage());
        } catch (Exception e) {
            return ResultVO.error(500, "服务器内部错误");
        }
    }

    /**
     * 删除用户
     */
    @Operation(
        summary = "删除用户",
        description = "根据用户ID删除用户（危险操作）"
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "用户删除成功"),
        @ApiResponse(responseCode = "400", description = "请求参数错误"),
        @ApiResponse(responseCode = "404", description = "用户不存在"),
        @ApiResponse(responseCode = "500", description = "服务器内部错误")
    })
    @DeleteMapping("/{id}")
    public ResultVO<?> deleteUser(
        @Parameter(description = "用户ID", required = true, example = "1")
        @PathVariable Integer id
    ) {
        try {
            boolean deleted = userService.deleteUser(id);
            if (deleted) {
                return ResultVO.success("用户删除成功");
            } else {
                return ResultVO.error(500, "删除失败");
            }
        } catch (IllegalArgumentException e) {
            return ResultVO.error(400, e.getMessage());
        } catch (Exception e) {
            return ResultVO.error(500, "服务器内部错误");
        }
    }

    /**
     * 用户注册接口
     * 支持多部分表单数据，包括文件上传
     * Content-Type: multipart/form-data
     *
     * @param userRegisterReqDTO 注册信息，包含：
     *                       - username: 用户名（必填）
     *                       - password: 密码（必填）
     *                       - nickname: 昵称（可选）
     *                       - email: 邮箱（可选）
     *                       - avatar: 头像文件（可选，MultipartFile类型）
     * @return 注册结果
     */
    @Operation(
        summary = "用户注册",
        description = "用户注册接口，支持头像文件上传到CDN。注意：Content-Type必须为multipart/form-data"
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "注册成功"),
        @ApiResponse(responseCode = "400", description = "请求参数错误或用户名已存在"),
        @ApiResponse(responseCode = "500", description = "服务器内部错误或头像上传失败")
    })
    @PostMapping(value = "/register", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResultVO<?> registerUser(
        @Parameter(
            description = "注册信息", required = true
        )
        @ModelAttribute UserRegisterReqDTO userRegisterReqDTO
    ) {
        try {
            User registeredUser = userService.registerUser(userRegisterReqDTO);
            return ResultVO.success("注册成功", registeredUser);
        } catch (IllegalArgumentException e) {
            return ResultVO.error(400, e.getMessage());
        } catch (RuntimeException e) {
            return ResultVO.error(500, e.getMessage());
        } catch (Exception e) {
            return ResultVO.error(500, "服务器内部错误");
        }
    }

    /**
     * 用户登录接口
     *
     * @param userLoginDTO 登录信息，包含：
     *                     - username: 用户名（必填）
     *                     - password: 密码（必填）
     * @return 登录结果
     */
    @Operation(
        summary = "用户登录",
        description = "用户登录验证，返回用户信息（不含密码）"
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "登录成功"),
        @ApiResponse(responseCode = "400", description = "登录失败"),
        @ApiResponse(responseCode = "500", description = "服务器内部错误")
    })
    @PostMapping("/login")
    public ResultVO<?> loginUser(
        @Parameter(
            description = "登录信息",
            required = true
        )
        @RequestBody UserLoginReqDTO userLoginDTO
    ) {
        try {
            UserLoginResponse loginResponse = userService.loginUser(userLoginDTO);
            return ResultVO.success("登录成功", loginResponse);
        } catch (IllegalArgumentException e) {
            return ResultVO.error(400, e.getMessage());
        } catch (Exception e) {
            return ResultVO.error(500, "服务器内部错误");
        }
    }
}
