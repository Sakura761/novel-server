package com.sakura.novel.controller;

import com.sakura.novel.core.common.vo.ResultVO;
import com.sakura.novel.DTO.Response.RankingResponse;
import com.sakura.novel.service.RankService;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

@RestController
@RequestMapping("/api/rankings")
@Tag(name = "排行榜管理", description = "排行榜相关接口")
@RequiredArgsConstructor
public class RankController {

    private final RankService rankService;

    @GetMapping("/daily")
    @Operation(summary = "获取日榜")
    public ResultVO<RankingResponse> getDailyRanking(
            @Parameter(description = "统计指标 (read_count, recommend_votes, monthly_tickets, collection_count)", required = true)
            @RequestParam String statType,
            @Parameter(description = "查询日期 (格式: yyyy-MM-dd), 默认为昨天")
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
            @Parameter(description = "返回数量", example = "100")
            @RequestParam(defaultValue = "100") Integer limit) {

        if (date == null) {
            date = LocalDate.now().minusDays(1); // 如果不指定日期，默认查昨天
        }
        RankingResponse response = rankService.getDailyRanking(statType, date, limit);
        return ResultVO.success(response);
    }

    @GetMapping("/weekly")
    @Operation(summary = "获取周榜")
    public ResultVO<RankingResponse> getWeeklyRanking(
            @Parameter(description = "统计指标 (read_count, recommend_votes, monthly_tickets, collection_count)", required = true)
            @RequestParam String statType,
            @Parameter(description = "查询周的结束日期 (格式: yyyy-MM-dd), 默认为昨天")
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
            @Parameter(description = "返回数量", example = "100")
            @RequestParam(defaultValue = "100") Integer limit) {

//        if (date == null) {
//            date = LocalDate.now().minusDays(1);
//        }
        RankingResponse response = rankService.getWeeklyRanking(statType, date, limit);
        return ResultVO.success(response);
    }

    @GetMapping("/monthly")
    @Operation(summary = "获取月榜")
    public ResultVO<RankingResponse> getMonthlyRanking(
            @Parameter(description = "统计指标 (read_count, recommend_votes, monthly_tickets, collection_count)", required = true)
            @RequestParam String statType,
            @Parameter(description = "查询月的结束日期 (格式: yyyy-MM-dd), 默认为昨天")
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
            @Parameter(description = "返回数量", example = "100")
            @RequestParam(defaultValue = "100") Integer limit) {

//        if (date == null) {
//            date = LocalDate.now().minusDays(1);
//        }
        RankingResponse response = rankService.getMonthlyRanking(statType, date, limit);
        return ResultVO.success(response);
    }
    @GetMapping("/peak")
    @Operation(summary = "获取最新的巅峰榜", description = "获取最新生成的巅峰榜单，无需指定日期。")
    public ResultVO<RankingResponse> getPeakRanking(
            @Parameter(description = "频道筛选 (1: 男频, 0: 女频)，不传则为全站榜")
            @RequestParam(required = false) Integer channel,
            @Parameter(description = "返回数量", example = "10")
            @RequestParam(defaultValue = "10") Integer limit) {

        // 直接调用新的服务方法，不再处理日期
        RankingResponse response = rankService.getPeakRanking(channel, limit);
        return ResultVO.success(response);
    }
}
