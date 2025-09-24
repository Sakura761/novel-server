package com.sakura.novel.service;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

/**
 * 书籍统计数据Redis服务接口
 */
public interface BookStatsRedisService {

    /**
     * 增加书籍阅读量
     * @param bookId 书籍ID
     * @param count 增加数量，默认为1
     */
    void incrementReadCount(Long bookId, int count);

    /**
     * 增加书籍推荐票
     * @param bookId 书籍ID
     * @param count 增加数量，默认为1
     */
    void incrementRecommendVotes(Long bookId, int count);

    /**
     * 增加书籍月票
     * @param bookId 书籍ID
     * @param count 增加数量，默认为1
     */
    void incrementMonthlyTickets(Long bookId, int count);

    /**
     * 增加书籍收藏量
     * @param bookId 书籍ID
     * @param count 增加数量，可以是负数（取消收藏）
     */
    void incrementCollectionCount(Long bookId, int count);

    /**
     * 获取指定书籍的当日统计数据
     * @param bookId 书籍ID
     * @return 统计数据Map
     */
    Map<String, Integer> getTodayStats(Long bookId);

    /**
     * 获取指定日期的所有书籍统计数据
     * @param date 日期
     * @return 书籍ID -> 统计数据Map的映射
     */
    Map<Long, Map<String, Integer>> getAllBooksStatsForDate(LocalDate date);

    /**
     * 清除指定日期的Redis数据
     * @param date 日期
     */
    void clearStatsForDate(LocalDate date);

    /**
     * 获取当前Redis中有统计数据的所有书籍ID
     * @return 书籍ID列表
     */
    List<Long> getAllBookIds();
}
