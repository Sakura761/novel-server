package com.sakura.novel.mapper;

import com.sakura.novel.entity.Author;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 作者表数据访问层
 */
@Mapper
public interface AuthorMapper {

    /**
     * 插入作者
     */
    int insert(Author author);

    /**
     * 根据ID删除作者
     */
    int deleteById(@Param("id") Integer id);

    /**
     * 更新作者信息
     */
    int updateById(Author author);

    /**
     * 根据ID查询作者
     */
    Author selectById(@Param("id") Integer id);

    /**
     * 根据用户ID查询作者
     */
    Author selectByUserId(@Param("userId") Integer userId);

    /**
     * 根据作者名称查询作者（支持模糊搜索）
     */
    List<Author> selectByNameLike(@Param("name") String name);

    /**
     * 查询所有作者
     */
    List<Author> selectAll();

    /**
     * 分页查询作者列表
     */
    List<Author> selectAuthorsWithConditions(@Param("name") String name,
                                           @Param("minLevel") Short minLevel,
                                           @Param("maxLevel") Short maxLevel,
                                           @Param("minTotalBooks") Integer minTotalBooks,
                                           @Param("maxTotalBooks") Integer maxTotalBooks,
                                           @Param("minWordCount") Long minWordCount,
                                           @Param("maxWordCount") Long maxWordCount);

    /**
     * 批量插入作者
     */
    int batchInsert(@Param("authors") List<Author> authors);

    /**
     * 检查作者名称是否存在
     */
    boolean existsByName(@Param("name") String name);

    /**
     * 检查用户是否已是作者
     */
    boolean existsByUserId(@Param("userId") Integer userId);

    /**
     * 统计作者总数
     */
    int count();

    /**
     * 根据等级统计作者数量
     */
    int countByLevel(@Param("level") Short level);

    /**
     * 查询热门作者（按粉丝数排序）
     */
    List<Author> selectPopularAuthors(@Param("limit") Integer limit);

    /**
     * 查询高产作者（按作品数排序）
     */
    List<Author> selectProductiveAuthors(@Param("limit") Integer limit);

    /**
     * 更新作者统计信息
     */
    int updateAuthorStats(@Param("id") Integer id,
                         @Param("totalWordCount") Long totalWordCount,
                         @Param("totalBooks") Integer totalBooks);

    /**
     * 更新作者粉丝数
     */
    int updateFollowerCount(@Param("id") Integer id, @Param("followerCount") Integer followerCount);

    /**
     * 增加粉丝数
     */
    int increaseFollowerCount(@Param("id") Integer id);

    /**
     * 减少粉丝数
     */
    int decreaseFollowerCount(@Param("id") Integer id);
}
