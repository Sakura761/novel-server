package com.sakura.novel.service;

import com.sakura.novel.DTO.Response.RankingResponse;
import java.time.LocalDate;
import java.util.List;

public interface RankService {

    // --- 对外API接口，用于查询已生成的排行榜 ---

    RankingResponse getDailyRanking(String statType, LocalDate date, Integer limit);
    RankingResponse getWeeklyRanking(String statType, LocalDate date, Integer limit);
    RankingResponse getMonthlyRanking(String statType, LocalDate date, Integer limit);
    /**
     * 新增的巅峰榜服务接口
     * @param channel 频道 (1: 男频, 0: 女频, null: 全站)
     * @param limit 返回数量
     * @return 排行榜列表
     */
    RankingResponse getPeakRanking(Integer channel, Integer limit);

    // --- 对内定时任务接口，用于生成和保存排行榜 ---

    void generateAndSaveDailyRanking(String statType, LocalDate date, Integer limit);
    void generateAndSaveWeeklyRanking(String statType, LocalDate date, Integer limit);
    void generateAndSaveMonthlyRanking(String statType, LocalDate date, Integer limit);
    /**
     * 生成并保存巅峰榜 (用于定时任务)
     * @param date 榜单日期
     */
    void generateAndSavePeakRanking(LocalDate date);

    // --- 辅助方法 ---
    boolean isValidStatType(String statType);
    boolean isValidRankType(String rankType);
}