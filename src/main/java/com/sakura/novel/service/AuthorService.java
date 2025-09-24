package com.sakura.novel.service;

import com.sakura.novel.entity.Author;
import com.sakura.novel.DTO.Response.PageResult;
import lombok.Data;

import java.util.List;

/**
 * 作者服务接口
 */
public interface AuthorService {

    /**
     * 创建作者
     */
    Author createAuthor(Author author);

    /**
     * 根据ID删除作者
     */
    boolean deleteById(Integer id);

    /**
     * 更新作者信息
     */
    Author updateAuthor(Author author);

    /**
     * 根据ID查询作者详情
     */
    Author getById(Integer id);

    /**
     * 根据用户ID查询作者信息
     */
    Author getByUserId(Integer userId);

    /**
     * 根据作者名称模糊搜索
     */
    List<Author> searchByName(String name);

    /**
     * 分页查询作者列表（支持多条件搜索）
     */
    PageResult<Author> getAuthorsWithConditions(String name, Short minLevel, Short maxLevel,
                                              Integer minTotalBooks, Integer maxTotalBooks,
                                              Long minWordCount, Long maxWordCount,
                                              Integer pageNum, Integer pageSize);

    /**
     * 获取所有作者（不分页）
     */
    List<Author> getAllAuthors();

    /**
     * 批量创建作者
     */
    boolean batchCreateAuthors(List<Author> authors);

    /**
     * 检查作者名称是否存在
     */
    boolean existsByName(String name);

    /**
     * 检查用户是否已是作者
     */
    boolean existsByUserId(Integer userId);

    /**
     * 获取作者总数
     */
    int getTotalCount();

    /**
     * 根据等级统计作者数量
     */
    int getCountByLevel(Short level);

    /**
     * 获取热门作者列表（按粉丝数排序）
     */
    List<Author> getPopularAuthors(Integer limit);

    /**
     * 获取高产作者列表（按作品数排序）
     */
    List<Author> getProductiveAuthors(Integer limit);

    /**
     * 更新作者统计信息（字数和作品数）
     */
    boolean updateAuthorStats(Integer id, Long totalWordCount, Integer totalBooks);

    /**
     * 关注作者（增加粉丝数）
     */
    boolean followAuthor(Integer authorId);

    /**
     * 取消关注作者（减少粉丝数）
     */
    boolean unfollowAuthor(Integer authorId);

    /**
     * 更新作者粉丝数
     */
    boolean updateFollowerCount(Integer id, Integer followerCount);

    /**
     * 升级作者等级
     */
    boolean upgradeAuthorLevel(Integer id, Short newLevel);

    /**
     * 获取作者详细统计信息
     */
    AuthorStatsInfo getAuthorStatsInfo(Integer id);

    /**
     * 作者统计信息封装类
     */
    @Data
    class AuthorStatsInfo {
        private Author author;
        private Integer bookCount;
        private Long totalWordCount;
        private Integer followerCount;
        private Double averageRating;
        private Integer totalFavorites;
    }
}
