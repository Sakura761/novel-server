package com.sakura.novel.service;

import com.sakura.novel.entity.User;
import com.sakura.novel.DTO.Response.UserRegisterDTO;
import com.sakura.novel.DTO.Response.UserLoginDTO;
import com.sakura.novel.DTO.Response.UserLoginResponse;

import java.util.List;

/**
 * 用户服务接口
 */
public interface UserService {

    /**
     * 根据ID查询用户
     * @param id 用户ID
     * @return 用户信息
     */
    User getUserById(Integer id);

    /**
     * 根据用户名查询用户
     * @param username 用户名
     * @return 用户信息
     */
    User getUserByUsername(String username);

    /**
     * 查询所有用户
     * @return 用户列表
     */
    List<User> getAllUsers();

    /**
     * 创建用户
     * @param user 用户信息
     * @return 创建的用户信息
     */
    User createUser(User user);

    /**
     * 更新用户信息
     * @param user 用户信息
     * @return 更新后的用户信息
     */
    User updateUser(User user);

    /**
     * 删除用户
     * @param id 用户ID
     * @return 是否删除成功
     */
    boolean deleteUser(Integer id);

    /**
     * 分页查询用户
     * @param page 页码（从1开始）
     * @param size 每页大小
     * @return 用户列表
     */
    List<User> getUsersByPage(int page, int size);

    /**
     * 获取用户总数
     * @return 用户总数
     */
    int getTotalCount();

    /**
     * 用户注册
     * @param userRegisterDTO 注册信息
     * @return 注册成功的用户信息
     */
    User registerUser(UserRegisterDTO userRegisterDTO);

    /**
     * 用户登录
     * @param userLoginDTO 登录信息
     * @return 登录响应信息
     */
    UserLoginResponse loginUser(UserLoginDTO userLoginDTO);
}
