package com.sakura.novel.service.impl;

import com.sakura.novel.DTO.Response.RankingResponse;
import com.sakura.novel.entity.BookRanking;
import com.sakura.novel.mapper.BookRankingMapper;
import com.sakura.novel.service.RankService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.temporal.TemporalAdjusters;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class RankServiceImpl implements RankService {

    private final BookRankingMapper rankingMapper;

    // =================================================================
    // Part 1: 对外API实现 (从 book_rankings 表快速查询)
    // =================================================================

    @Override
    public RankingResponse getDailyRanking(String statType, LocalDate date, Integer limit) {
        // 当不指定日期时，默认查询昨天的数据
        if (date == null) {
            date = LocalDate.now().minusDays(1);
        }
        return getSavedRanking("daily", statType, date, date, limit);
    }

    @Override
    public RankingResponse getWeeklyRanking(String statType, LocalDate date, Integer limit) {
        LocalDate startDate, endDate;

        if (date == null) {
            // =================== 修正后的逻辑 ===================
            // 如果日期为空，自动计算上一个完整周的日期
            LocalDate today = LocalDate.now();

            // 1. 先找到本周的周一
            LocalDate mondayOfThisWeek = today.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY));

            // 2. 从本周的周一减去1天，就得到了上周的周日 (endDate)
            endDate = mondayOfThisWeek.minusDays(1);

            // 3. 从上周的周日减去6天，就得到了上周的周一 (startDate)
            // ======================================================
        } else {
            // 如果指定了日期，则以该日期为结束日计算一周
            endDate = date;
        }
        startDate = endDate.minusDays(6);
        log.info("查询周榜, statType: {}, startDate: {}, endDate: {}", statType, startDate, endDate);
        return getSavedRanking("weekly", statType, startDate, endDate, limit);
    }

    @Override
    public RankingResponse getMonthlyRanking(String statType, LocalDate date, Integer limit) {
        LocalDate startDate, endDate;

        if (date == null) {
            // 如果日期为空，自动计算上一个完整月的日期
            LocalDate today = LocalDate.now();
            // 找到上个月的最后一天
            endDate = today.minusMonths(1).with(TemporalAdjusters.lastDayOfMonth());
            // 找到上个月的第一天
            startDate = endDate.with(TemporalAdjusters.firstDayOfMonth());
        } else {
            // 如果指定了日期，则以该日期为结束日计算一个月
            endDate = date;
            startDate = endDate.withDayOfMonth(1);
        }
        return getSavedRanking("monthly", statType, startDate, endDate, limit);
    }
    /**
     * 获取最新的巅峰榜 (API调用) - 修正此方法
     */
    @Override
    public RankingResponse getPeakRanking(Integer channel, Integer limit) {
        String statType;
        if (channel == null) {
            statType = "all";
        } else if (channel == 1) {
            statType = "male";
        } else {
            statType = "female";
        }

        // 1. 先查询这个榜单类型的最新日期
        LocalDate latestDate = rankingMapper.findLatestRankingDate("peak", statType);

        // 2. 如果没有任何榜单数据，返回一个空的响应
        if (latestDate == null) {
            log.warn("未找到类型为 'peak', 指标为 '{}' 的排行榜数据。", statType);
            RankingResponse emptyResponse = new RankingResponse();
            emptyResponse.setRankType("peak");
            emptyResponse.setStatType(statType);
            emptyResponse.setRankings(new ArrayList<>());
            return emptyResponse;
        }

        // 3. 使用获取到的最新日期，复用现有的查询方法获取榜单
        return getSavedRanking("peak", statType, latestDate, latestDate, limit);
    }

    private RankingResponse getSavedRanking(String rankType, String statType, LocalDate periodStart, LocalDate periodEnd, Integer limit) {
        if (limit == null || limit <= 0) limit = 100;
        List<RankingResponse.RankingItem> rankings = rankingMapper.getSavedRanking(rankType, statType, periodStart, periodEnd, limit);

        RankingResponse response = new RankingResponse();
        response.setRankType(rankType);
        response.setStatType(statType);
        response.setPeriodStart(periodStart);
        response.setPeriodEnd(periodEnd);
        response.setRankings(rankings);
        return response;
    }


    // =================================================================
    // Part 2: 对内定时任务实现 (从 book_daily_stats 计算并保存)
    // =================================================================

    @Override
    @Transactional
    public void generateAndSaveDailyRanking(String statType, LocalDate date, Integer limit) {
        List<RankingResponse.RankingItem> items = rankingMapper.calculateDailyRanking(statType, date, limit);
        saveCalculatedRanking("daily", statType, date, date, items);
    }

    @Override
    @Transactional
    public void generateAndSaveWeeklyRanking(String statType, LocalDate endDate, Integer limit) {
        LocalDate startDate = endDate.minusDays(6);
        List<RankingResponse.RankingItem> items = rankingMapper.calculatePeriodicRanking(statType, startDate, endDate, limit);
        saveCalculatedRanking("weekly", statType, startDate, endDate, items);
    }

    @Override
    @Transactional
    public void generateAndSaveMonthlyRanking(String statType, LocalDate endDate, Integer limit) {
        LocalDate startDate = endDate.withDayOfMonth(1);
        List<RankingResponse.RankingItem> items = rankingMapper.calculatePeriodicRanking(statType, startDate, endDate, limit);
        saveCalculatedRanking("monthly", statType, startDate, endDate, items);
    }

    private void saveCalculatedRanking(String rankType, String statType, LocalDate periodStart, LocalDate periodEnd, List<RankingResponse.RankingItem> items) {
        if (items == null || items.isEmpty()) {
            log.warn("计算出的排行榜为空，不执行保存。类型: {}, 指标: {}, 时间: {} - {}", rankType, statType, periodStart, periodEnd);
            return;
        }

        rankingMapper.deleteRankingByPeriod(rankType, statType, periodStart, periodEnd);

        List<BookRanking> rankingsToSave = items.stream().map(item -> new BookRanking(
                item.getBookId().longValue(),
                rankType,
                statType,
                item.getRank(),
                item.getScore(),
                periodStart,
                periodEnd
        )).collect(Collectors.toList());

        rankingMapper.insertBookRankings(rankingsToSave);
        log.info("成功保存 {} 条 {} 排行榜数据. 指标: {}, 时间: {} - {}", rankingsToSave.size(), rankType, statType, periodStart, periodEnd);
    }
    /**
     * 生成巅峰榜 (定时任务调用)
     */
    @Override
    @Transactional
    public void generateAndSavePeakRanking(LocalDate date) {
        // 1. 生成全站巅峰榜
        log.info("正在生成全站巅峰榜...");
        List<RankingResponse.RankingItem> allSiteItems = rankingMapper.calculatePeakRanking(null, 100);
        saveCalculatedRanking("peak", "all", date, date, allSiteItems);
    }

    // =================================================================
    // Part 3: 辅助方法
    // =================================================================

    @Override
    public boolean isValidStatType(String statType) {
        return "read_count".equals(statType) ||
                "recommend_votes".equals(statType) ||
                "monthly_tickets".equals(statType) ||
                "collection_count".equals(statType);
    }

    @Override
    public boolean isValidRankType(String rankType) {
        return "daily".equals(rankType) || "weekly".equals(rankType) || "monthly".equals(rankType);
    }
}