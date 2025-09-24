package com.sakura.novel.scheduler;

import com.sakura.novel.entity.BookDailyStats;
import com.sakura.novel.mapper.BookDailyStatsMapper;
import com.sakura.novel.mapper.BookStatsMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.Cursor;
import org.springframework.data.redis.core.ScanOptions;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Component
@RequiredArgsConstructor
@Slf4j
public class StatsPersistenceScheduler {

    private final StringRedisTemplate redisTemplate;
    private final BookStatsMapper bookStatsMapper; // 2. 注入 BookStatsMapper
    private final BookDailyStatsMapper statsMapper;
    private static final String KEY_PREFIX = "daily_stats:";
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    /**
     * 每日凌晨 2:15 执行，将前一天的Redis统计数据持久化到MySQL
     */
    @Scheduled(cron = "0 0 1 * * ?")
    public void persistDailyStats() {
        LocalDate yesterday = LocalDate.now().minusDays(1);
        String yesterdayStr = DATE_FORMATTER.format(yesterday);
        String scanPattern = KEY_PREFIX + yesterdayStr + ":*";
        log.info("开始执行持久化任务，扫描key: {}", scanPattern);

        List<BookDailyStats> statsToPersist = new ArrayList<>();
        List<String> keysToDelete = new ArrayList<>();

        try (Cursor<String> cursor = redisTemplate.scan(ScanOptions.scanOptions().match(scanPattern).count(1000).build())) {
            while (cursor.hasNext()) {
                String key = cursor.next();
                // 从key中解析出 book_id
                Long bookId = Long.parseLong(key.substring(key.lastIndexOf(':') + 1));

                // 获取Hash中的所有字段和值
                Map<Object, Object> statsMap = redisTemplate.opsForHash().entries(key);

                if (!statsMap.isEmpty()) {
                    BookDailyStats stats = new BookDailyStats();
                    stats.setBookId(bookId);
                    stats.setStatDate(yesterday);
                    stats.setReadCount((int) Long.parseLong((String) statsMap.getOrDefault("read_count", "0")));
                    stats.setRecommendVotes(Integer.parseInt((String) statsMap.getOrDefault("recommend_votes", "0")));
                    stats.setMonthlyTickets(Integer.parseInt((String) statsMap.getOrDefault("monthly_tickets", "0")));
                    stats.setCollectionCount(Integer.parseInt((String) statsMap.getOrDefault("collection_count", "0")));
                    statsToPersist.add(stats);
                    keysToDelete.add(key);
                }
            }
        } catch (Exception e) {
            log.error("扫描Redis keys时出错: {}", e.getMessage(), e);
            return; // 出现错误，终止本次任务
        }

        if (statsToPersist.isEmpty()) {
            log.info("没有需要持久化的统计数据。");
            return;
        }

        try {
            // 批量写入数据库
            int affectedRows = statsMapper.batchInsertOrUpdate(statsToPersist);
            log.info("成功将 {} 条统计数据持久化到MySQL，影响行数: {}", statsToPersist.size(), affectedRows);
            // ================= 新增逻辑开始 =================
                    log.info("开始更新作品总数据统计...");
            int updatedBooksCount = 0;
            for (BookDailyStats dailyStat : statsToPersist) {
                try {
                    bookStatsMapper.updateBookStats(
                            dailyStat.getBookId(),
                            dailyStat.getReadCount(),
                            dailyStat.getRecommendVotes(),
                            dailyStat.getCollectionCount()
                    );
                    updatedBooksCount++;
                } catch (Exception e) {
                    log.error("更新 book_id: {} 的总数据时失败: {}", dailyStat.getBookId(), e.getMessage());
                    // 根据业务决定是否需要继续或中断
                }
            }
            log.info("成功更新了 {} 本书的总数据统计。", updatedBooksCount);
            // 持久化成功后，从Redis中删除这些key
            Long deletedKeys = redisTemplate.delete(keysToDelete);
            log.info("成功从Redis中删除 {} 个已持久化的key。", deletedKeys);

        } catch (Exception e) {
            log.error("持久化统计数据到MySQL时发生错误: {}", e.getMessage(), e);
            // **重要**: 这里不要删除Redis的key，以便下次任务重试
        }
    }
}