package com.sakura.novel.mapper;

import com.sakura.novel.entity.User;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 用户数据访问层接口
 */
@Mapper
public interface UserMapper {

    /**
     * 根据ID查询用户
     * @param id 用户ID
     * @return 用户信息
     */
    User selectById(@Param("id") Integer id);

    /**
     * 根据用户名查询用户
     * @param username 用户名
     * @return 用户信息
     */
    User selectByUsername(@Param("username") String username);

    /**
     * 查询所有用户
     * @return 用户列表
     */
    List<User> selectAll();

    /**
     * 插入用户
     * @param user 用户信息
     * @return 影响行数
     */
    int insert(User user);

    /**
     * 更新用户信息
     * @param user 用户信息
     * @return 影响行数
     */
    int updateById(User user);

    /**
     * 根据ID删除用户
     * @param id 用户ID
     * @return 影响行数
     */
    int deleteById(@Param("id") Integer id);

    /**
     * 分页查询用户
     * @param offset 偏移量
     * @param limit 限制条数
     * @return 用户列表
     */
    List<User> selectByPage(@Param("offset") int offset, @Param("limit") int limit);

    /**
     * 查询用户总数
     * @return 用户总数
     */
    int countTotal();
}
