package com.sakura.novel.mapper;

import com.sakura.novel.DTO.Response.RankingResponse;
import com.sakura.novel.entity.BookRanking;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.time.LocalDate;
import java.util.List;

@Mapper
public interface BookRankingMapper {

    // --- 计算查询 ---
    List<RankingResponse.RankingItem> calculateDailyRanking(
            @Param("statType") String statType,
            @Param("date") LocalDate date,
            @Param("limit") Integer limit
    );

    List<RankingResponse.RankingItem> calculatePeriodicRanking(
            @Param("statType") String statType,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate,
            @Param("limit") Integer limit
    );
    List<RankingResponse.RankingItem> calculatePeakRanking(@Param("channel") Integer channel, @Param("limit") Integer limit);
    // --- 结果查询 ---
    List<RankingResponse.RankingItem> getSavedRanking(
            @Param("rankType") String rankType,
            @Param("statType") String statType,
            @Param("periodStart") LocalDate periodStart,
            @Param("periodEnd") LocalDate periodEnd,
            @Param("limit") Integer limit
    );
    List<RankingResponse.RankingItem> getPeakRanking(@Param("channel") Integer channel, @Param("limit") Integer limit);
    /**
     * 新增：查询指定排行榜的最新日期
     */
    LocalDate findLatestRankingDate(@Param("rankType") String rankType, @Param("statType") String statType);
    // --- 数据操作 ---
    int insertBookRankings(@Param("rankings") List<BookRanking> rankings);

    int deleteRankingByPeriod(
            @Param("rankType") String rankType,
            @Param("statType") String statType,
            @Param("periodStart") LocalDate periodStart,
            @Param("periodEnd") LocalDate periodEnd
    );
}