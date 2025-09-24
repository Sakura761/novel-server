package com.sakura.novel.scheduler;

import com.sakura.novel.service.RankService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.DayOfWeek; // 新增导入
import java.time.LocalDate;
import java.time.temporal.TemporalAdjusters; // 新增导入
import java.util.Arrays;
import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class RankingScheduler {

    private final RankService rankService;

    private static final List<String> STAT_TYPES = Arrays.asList(
            "read_count", "recommend_votes", "monthly_tickets", "collection_count"
    );

    /**
     * 每天凌晨2点执行
     */
    @Scheduled(cron = "0 0 2 * * ?")
    public void generateRankings() {
        log.info("开始执行每日排行榜生成任务...");
        LocalDate today = LocalDate.now();
        LocalDate yesterday = today.minusDays(1);

        // --- 1. 生成日榜和巅峰榜（每天都执行） ---
        for (String statType : STAT_TYPES) {
            try {
                log.info("正在生成 {} 的日榜...", statType);
                rankService.generateAndSaveDailyRanking(statType, yesterday, 100);
            } catch (Exception e) {
                log.error("生成日榜时发生错误, statType: {}. 错误信息: {}", statType, e.getMessage(), e);
            }
        }
        try {
            log.info("正在生成巅峰榜...");
            rankService.generateAndSavePeakRanking(yesterday);
        } catch (Exception e) {
            log.error("生成巅峰榜时发生错误: {}", e.getMessage(), e);
        }

        // --- 2. 判断是否需要生成周榜 ---
        // 如果今天是周一，那么昨天（周日）就是一个完整周的结束
        if (today.getDayOfWeek() == DayOfWeek.MONDAY) {
            log.info("今天是周一，开始生成上周的周榜...");
            for (String statType : STAT_TYPES) {
                try {
                    log.info("正在生成 {} 的周榜...", statType);
                    // yesterday 就是上周日，是周榜的结束日期
                    rankService.generateAndSaveWeeklyRanking(statType, yesterday, 100);
                } catch (Exception e) {
                    log.error("生成周榜时发生错误, statType: {}. 错误信息: {}", statType, e.getMessage(), e);
                }
            }
        }

        // --- 3. 判断是否需要生成月榜 ---
        // 如果今天是某月的第一天，那么昨天就是上一个完整月的结束
        if (today.getDayOfMonth() == 1) {
            log.info("今天是本月第一天，开始生成上个月的月榜...");
            for (String statType : STAT_TYPES) {
                try {
                    log.info("正在生成 {} 的月榜...", statType);
                    // yesterday 就是上个月最后一天，是月榜的结束日期
                    rankService.generateAndSaveMonthlyRanking(statType, yesterday, 100);
                } catch (Exception e) {
                    log.error("生成月榜时发生错误, statType: {}. 错误信息: {}", statType, e.getMessage(), e);
                }
            }
        }

        log.info("每日排行榜生成任务执行完毕。");
    }
}