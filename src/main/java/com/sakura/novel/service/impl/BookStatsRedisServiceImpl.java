package com.sakura.novel.service.impl;

import com.sakura.novel.service.BookStatsRedisService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.TimeUnit;

/**
 * 书籍统计数据Redis服务实现类
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class BookStatsRedisServiceImpl implements BookStatsRedisService {

    private final RedisTemplate<String, Object> redisTemplate;

    // Redis key前缀
    private static final String STATS_KEY_PREFIX = "book:stats:";
    private static final String DATE_PATTERN = "yyyy-MM-dd";
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern(DATE_PATTERN);

    // 统计字段常量
    private static final String FIELD_READ_COUNT = "readCount";
    private static final String FIELD_RECOMMEND_VOTES = "recommendVotes";
    private static final String FIELD_MONTHLY_TICKETS = "monthlyTickets";
    private static final String FIELD_COLLECTION_COUNT = "collectionCount";

    /**
     * 生成Redis key
     */
    private String generateKey(Long bookId, LocalDate date) {
        return STATS_KEY_PREFIX + date.format(DATE_FORMATTER) + ":" + bookId;
    }

    /**
     * 生成日期模式的key
     */
    private String generateDatePattern(LocalDate date) {
        return STATS_KEY_PREFIX + date.format(DATE_FORMATTER) + ":*";
    }

    @Override
    public void incrementReadCount(Long bookId, int count) {

        String key = generateKey(bookId, LocalDate.now());
        redisTemplate.opsForHash().increment(key, FIELD_READ_COUNT, count);
        // 设置过期时间为3天，确保数据能够被持久化
        redisTemplate.expire(key, 3, TimeUnit.DAYS);
        log.debug("增加书籍{}阅读量: {}", bookId, count);
    }

    @Override
    public void incrementRecommendVotes(Long bookId, int count) {
        String key = generateKey(bookId, LocalDate.now());
        redisTemplate.opsForHash().increment(key, FIELD_RECOMMEND_VOTES, count);
        redisTemplate.expire(key, 3, TimeUnit.DAYS);
        log.debug("增加书籍{}推荐票: {}", bookId, count);
    }

    @Override
    public void incrementMonthlyTickets(Long bookId, int count) {
        String key = generateKey(bookId, LocalDate.now());
        redisTemplate.opsForHash().increment(key, FIELD_MONTHLY_TICKETS, count);
        redisTemplate.expire(key, 3, TimeUnit.DAYS);
        log.debug("增加书籍{}月票: {}", bookId, count);
    }

    @Override
    public void incrementCollectionCount(Long bookId, int count) {
        String key = generateKey(bookId, LocalDate.now());
        redisTemplate.opsForHash().increment(key, FIELD_COLLECTION_COUNT, count);
        redisTemplate.expire(key, 3, TimeUnit.DAYS);
        log.debug("增加书籍{}收藏量: {}", bookId, count);
    }

    @Override
    public Map<String, Integer> getTodayStats(Long bookId) {
        String key = generateKey(bookId, LocalDate.now());
        Map<Object, Object> hashMap = redisTemplate.opsForHash().entries(key);
        
        Map<String, Integer> stats = new HashMap<>();
        stats.put(FIELD_READ_COUNT, getIntValue(hashMap.get(FIELD_READ_COUNT)));
        stats.put(FIELD_RECOMMEND_VOTES, getIntValue(hashMap.get(FIELD_RECOMMEND_VOTES)));
        stats.put(FIELD_MONTHLY_TICKETS, getIntValue(hashMap.get(FIELD_MONTHLY_TICKETS)));
        stats.put(FIELD_COLLECTION_COUNT, getIntValue(hashMap.get(FIELD_COLLECTION_COUNT)));
        
        return stats;
    }

    @Override
    public Map<Long, Map<String, Integer>> getAllBooksStatsForDate(LocalDate date) {
        String pattern = generateDatePattern(date);
        Set<String> keys = redisTemplate.keys(pattern);
        
        if (keys == null || keys.isEmpty()) {
            return new HashMap<>();
        }

        Map<Long, Map<String, Integer>> result = new HashMap<>();
        
        for (String key : keys) {
            // 从key中提取bookId
            String[] parts = key.split(":");
            if (parts.length >= 4) {
                try {
                    Long bookId = Long.parseLong(parts[3]);
                    Map<Object, Object> hashMap = redisTemplate.opsForHash().entries(key);
                    
                    Map<String, Integer> stats = new HashMap<>();
                    stats.put(FIELD_READ_COUNT, getIntValue(hashMap.get(FIELD_READ_COUNT)));
                    stats.put(FIELD_RECOMMEND_VOTES, getIntValue(hashMap.get(FIELD_RECOMMEND_VOTES)));
                    stats.put(FIELD_MONTHLY_TICKETS, getIntValue(hashMap.get(FIELD_MONTHLY_TICKETS)));
                    stats.put(FIELD_COLLECTION_COUNT, getIntValue(hashMap.get(FIELD_COLLECTION_COUNT)));
                    
                    result.put(bookId, stats);
                } catch (NumberFormatException e) {
                    log.warn("无法解析key中的bookId: {}", key);
                }
            }
        }
        
        log.info("获取{}的统计数据，共{}本书", date, result.size());
        return result;
    }

    @Override
    public void clearStatsForDate(LocalDate date) {
        String pattern = generateDatePattern(date);
        Set<String> keys = redisTemplate.keys(pattern);
        
        if (keys != null && !keys.isEmpty()) {
            redisTemplate.delete(keys);
            log.info("清除{}的Redis统计数据，共{}条记录", date, keys.size());
        }
    }

    @Override
    public List<Long> getAllBookIds() {
        String pattern = generateDatePattern(LocalDate.now());
        Set<String> keys = redisTemplate.keys(pattern);
        
        List<Long> bookIds = new ArrayList<>();
        if (keys != null) {
            for (String key : keys) {
                String[] parts = key.split(":");
                if (parts.length >= 4) {
                    try {
                        bookIds.add(Long.parseLong(parts[3]));
                    } catch (NumberFormatException e) {
                        log.warn("无法解析key中的bookId: {}", key);
                    }
                }
            }
        }
        
        return bookIds;
    }

    /**
     * 安全地将Object转换为Integer
     */
    private Integer getIntValue(Object value) {
        if (value == null) {
            return 0;
        }
        if (value instanceof Integer) {
            return (Integer) value;
        }
        if (value instanceof String) {
            try {
                return Integer.parseInt((String) value);
            } catch (NumberFormatException e) {
                log.warn("无法转换为Integer: {}", value);
                return 0;
            }
        }
        return 0;
    }
}