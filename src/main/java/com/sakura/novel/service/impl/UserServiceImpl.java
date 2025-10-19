package com.sakura.novel.service.impl;

import com.sakura.novel.entity.User;
import com.sakura.novel.mapper.UserMapper;
import com.sakura.novel.service.UserService;
import com.sakura.novel.service.FileUploadService;
import com.sakura.novel.DTO.Request.UserRegisterReqDTO;
import com.sakura.novel.DTO.Request.UserLoginReqDTO;
import com.sakura.novel.DTO.Response.UserLoginResponse;
import com.sakura.novel.utils.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 用户服务实现类
 */
@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final JwtUtil jwtUtil;
    private final UserMapper userMapper;
    private final FileUploadService fileUploadService;
    private final PasswordEncoder passwordEncoder;

    @Override
    public User getUserById(Integer id) {
        if (id == null || id <= 0) {
            throw new IllegalArgumentException("用户ID不能为空或非正数");
        }
        return userMapper.selectById(id);
    }

    @Override
    public User getUserByUsername(String username) {
        if (username == null || username.trim().isEmpty()) {
            throw new IllegalArgumentException("用户名不能为空");
        }
        return userMapper.selectByUsername(username);
    }

    @Override
    public List<User> getAllUsers() {
        return userMapper.selectAll();
    }

    @Override
    public User createUser(User user) {
        if (user == null) {
            throw new IllegalArgumentException("用户信息不能为空");
        }
        if (user.getUsername() == null || user.getUsername().trim().isEmpty()) {
            throw new IllegalArgumentException("用户名不能为空");
        }

        // 检查用户名是否已存在
        User existingUser = userMapper.selectByUsername(user.getUsername());
        if (existingUser != null) {
            throw new IllegalArgumentException("用户名已存在");
        }

        // 设置创建时间和更新时间
        LocalDateTime now = LocalDateTime.now();
        user.setCreateTime(now);
        user.setUpdateTime(now);

        // 如果没有设置状态，默认为active
        if (user.getStatus() == null) {
            user.setStatus(User.UserStatus.active);
        }

        userMapper.insert(user);
        return user;
    }

    @Override
    public User updateUser(User user) {
        if (user == null || user.getId() == null) {
            throw new IllegalArgumentException("用户信息和用户ID不能为空");
        }

        // 检查用户是否存在
        User existingUser = userMapper.selectById(user.getId());
        if (existingUser == null) {
            throw new IllegalArgumentException("用户不存在");
        }

        // 如果用户名发生变化，检查新用户名是否已被其他用户使用
        if (!existingUser.getUsername().equals(user.getUsername())) {
            User userWithSameName = userMapper.selectByUsername(user.getUsername());
            if (userWithSameName != null && !userWithSameName.getId().equals(user.getId())) {
                throw new IllegalArgumentException("用户名已被其他用户使用");
            }
        }

        // 设置更新时间
        user.setUpdateTime(LocalDateTime.now());

        userMapper.updateById(user);
        return userMapper.selectById(user.getId());
    }

    @Override
    public boolean deleteUser(Integer id) {
        if (id == null || id <= 0) {
            throw new IllegalArgumentException("用户ID不能为空或非正数");
        }

        // 检查用户是否存在
        User existingUser = userMapper.selectById(id);
        if (existingUser == null) {
            throw new IllegalArgumentException("用户不存在");
        }

        return userMapper.deleteById(id) > 0;
    }

    @Override
    public List<User> getUsersByPage(int page, int size) {
        if (page <= 0 || size <= 0) {
            throw new IllegalArgumentException("页码和每页大小必须为正数");
        }

        int offset = (page - 1) * size;
        return userMapper.selectByPage(offset, size);
    }

    @Override
    public int getTotalCount() {
        return userMapper.countTotal();
    }

    @Override
    public User registerUser(UserRegisterReqDTO userRegisterReqDTO) {
        if (userRegisterReqDTO == null) {
            throw new IllegalArgumentException("注册信息不能为空");
        }
        // 检查用户名是否已存在
        User existingUser = userMapper.selectByUsername(userRegisterReqDTO.getUsername());
        if (existingUser != null) {
            throw new IllegalArgumentException("用户名已存在");
        }

        // 创建用户对象
        User user = new User();
        user.setUsername(userRegisterReqDTO.getUsername());
        user.setPasswordHash(passwordEncoder.encode(userRegisterReqDTO.getPassword()));
        user.setNickname(userRegisterReqDTO.getNickname() != null ? userRegisterReqDTO.getNickname() : userRegisterReqDTO.getUsername());
        user.setEmail(userRegisterReqDTO.getEmail());
        user.setStatus(User.UserStatus.active);

        // 处理头像上传
        if (userRegisterReqDTO.getAvatar() != null && !userRegisterReqDTO.getAvatar().isEmpty()) {
            try {
                // 验证文件类型
                String contentType = userRegisterReqDTO.getAvatar().getContentType();
                if (contentType == null || !contentType.startsWith("image/")) {
                    throw new IllegalArgumentException("头像必须是图片文件");
                }

                // 验证文件大小（限制为5MB）
                if (userRegisterReqDTO.getAvatar().getSize() > 5 * 1024 * 1024) {
                    throw new IllegalArgumentException("头像文件大小不能超过5MB");
                }

                // 生成唯一文件名并上传
                String fileName = fileUploadService.generateUniqueFileName(userRegisterReqDTO.getAvatar().getOriginalFilename());
                String avatarUrl = fileUploadService.uploadToJsdelivr(userRegisterReqDTO.getAvatar(), fileName);
                String objectName = "avatars/" + fileName;
                String avatarUrl2 = fileUploadService.uploadToMinio(userRegisterReqDTO.getAvatar(), objectName);

                user.setAvatarUrl(avatarUrl);
            } catch (Exception e) {
                throw new RuntimeException("头像上传失败: " + e.getMessage());
            }
        }

        // 设置创建时间和更新时间
        LocalDateTime now = LocalDateTime.now();
        user.setCreateTime(now);
        user.setUpdateTime(now);

        // 保存用户
        userMapper.insert(user);

        // 返回用户信息（不包含密码）
        user.setPasswordHash(null);
        return user;
    }

    @Override
    public UserLoginResponse loginUser(UserLoginReqDTO userLoginDTO) {
        if (userLoginDTO == null) {
            throw new IllegalArgumentException("登录信息不能为空");
        }
        // 查找用户
        User user = userMapper.selectByUsername(userLoginDTO.getUsername());
        if (user == null) {
            throw new IllegalArgumentException("用户名或密码错误");
        }
        // 验证密码
        if (!passwordEncoder.matches(userLoginDTO.getPassword(), user.getPasswordHash())) {
            throw new IllegalArgumentException("用户名或密码错误");
        }

        // 检查用户状态
        if (user.getStatus() == User.UserStatus.banned) {
            throw new IllegalArgumentException("账号已被封禁");
        }

        if (user.getStatus() == User.UserStatus.inactive) {
            throw new IllegalArgumentException("账号未激活");
        }
        String token = jwtUtil.generateToken(user.getId(), user.getUsername());
        // 构建登录响应
        UserLoginResponse response = new UserLoginResponse();
        response.setId(user.getId());
        response.setUsername(user.getUsername());
        response.setNickname(user.getNickname());
        response.setEmail(user.getEmail());
        response.setAvatarUrl(user.getAvatarUrl());
        response.setStatus(user.getStatus().toString());
        response.setCreateTime(user.getCreateTime());
        response.setToken(token);
        return response;
    }
}
