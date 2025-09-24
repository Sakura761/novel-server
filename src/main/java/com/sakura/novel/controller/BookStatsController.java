package com.sakura.novel.controller;

import com.sakura.novel.core.common.vo.ResultVO;
import com.sakura.novel.service.BookStatsRedisService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 书籍统计数据控制器
 */
@RestController
@RequestMapping("/api/book-stats")
@CrossOrigin(origins = "*")
@RequiredArgsConstructor
@Tag(name = "书籍统计管理", description = "书籍统计数据相关的API接口，包括阅读量、推荐票、收藏等统计功能")
public class BookStatsController {

    private final BookStatsRedisService bookStatsRedisService;
//    private final BookStatsPersistenceService bookStatsPersistenceService;

    /**
     * 增加书籍阅读量
     */
    @Operation(
        summary = "增加书籍阅读量",
        description = "为指定书籍增加阅读量，数据先存储在Redis中"
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "操作成功"),
        @ApiResponse(responseCode = "400", description = "请求参数错误"),
        @ApiResponse(responseCode = "500", description = "服务器内部错误")
    })
    @PostMapping("/read/{bookId}")
    public ResultVO<?> incrementReadCount(
        @Parameter(description = "书籍ID", required = true, example = "1")
        @PathVariable Long bookId,
        @Parameter(description = "增加数量", example = "1")
        @RequestParam(defaultValue = "1") int count
    ) {
        try {
            bookStatsRedisService.incrementReadCount(bookId, count);
            return ResultVO.success("阅读量增加成功");
        } catch (Exception e) {
            return ResultVO.error(500, "增加阅读量失败: " + e.getMessage());
        }
    }

    /**
     * 增加书籍推荐票
     */
    @Operation(
        summary = "增加书籍推荐票",
        description = "为指定书籍增加推荐票数量"
    )
    @PostMapping("/recommend/{bookId}")
    public ResultVO<?> incrementRecommendVotes(
        @Parameter(description = "书籍ID", required = true, example = "1")
        @PathVariable Long bookId,
        @Parameter(description = "增加数量", example = "1")
        @RequestParam(defaultValue = "1") int count
    ) {
        try {
            bookStatsRedisService.incrementRecommendVotes(bookId, count);
            return ResultVO.success("推荐票增加成功");
        } catch (Exception e) {
            return ResultVO.error(500, "增加推荐票失败: " + e.getMessage());
        }
    }

    /**
     * 增加书籍月票
     */
    @Operation(
        summary = "增加书籍月票",
        description = "为指定书籍增加月票数量"
    )
    @PostMapping("/monthly-ticket/{bookId}")
    public ResultVO<?> incrementMonthlyTickets(
        @Parameter(description = "书籍ID", required = true, example = "1")
        @PathVariable Long bookId,
        @Parameter(description = "增加数量", example = "1")
        @RequestParam(defaultValue = "1") int count
    ) {
        try {
            bookStatsRedisService.incrementMonthlyTickets(bookId, count);
            return ResultVO.success("月票增加成功");
        } catch (Exception e) {
            return ResultVO.error(500, "增加月票失败: " + e.getMessage());
        }
    }

    /**
     * 增加或减少书籍收藏量
     */
    @Operation(
        summary = "增加或减少书籍收藏量",
        description = "为指定书籍增加或减少收藏数量，支持负数（取消收藏）"
    )
    @PostMapping("/collection/{bookId}")
    public ResultVO<?> incrementCollectionCount(
        @Parameter(description = "书籍ID", required = true, example = "1")
        @PathVariable Long bookId,
        @Parameter(description = "变化数量，正数为收藏，负数为取消收藏", example = "1")
        @RequestParam int count
    ) {
        try {
            bookStatsRedisService.incrementCollectionCount(bookId, count);
            String message = count > 0 ? "收藏成功" : "取消收藏成功";
            return ResultVO.success(message);
        } catch (Exception e) {
            return ResultVO.error(500, "操作失败: " + e.getMessage());
        }
    }

    /**
     * 获取书籍今日统计数据
     */
    @Operation(
        summary = "获取书籍今日统计数据",
        description = "从Redis获取指定书籍的今日统计数据"
    )
    @GetMapping("/today/{bookId}")
    public ResultVO<Map<String, Integer>> getTodayStats(
        @Parameter(description = "书籍ID", required = true, example = "1")
        @PathVariable Long bookId
    ) {
        try {
            Map<String, Integer> stats = bookStatsRedisService.getTodayStats(bookId);
            return ResultVO.success("获取统计数据成功", stats);
        } catch (Exception e) {
            return ResultVO.error(500, "获取统计数据失败: " + e.getMessage());
        }
    }

    /**
     * 获取今日所有书籍统计数据
     */
    @Operation(
        summary = "获取今日所有书籍统计数据",
        description = "从Redis获取今日所有有统计数据的书籍"
    )
    @GetMapping("/today/all")
    public ResultVO<Map<Long, Map<String, Integer>>> getAllTodayStats() {
        try {
            Map<Long, Map<String, Integer>> allStats = bookStatsRedisService.getAllBooksStatsForDate(LocalDate.now());
            return ResultVO.success("获取统计数据成功", allStats);
        } catch (Exception e) {
            return ResultVO.error(500, "获取统计数据失败: " + e.getMessage());
        }
    }

    /**
     * 手动触发数据持久化
     */
//    @Operation(
//        summary = "手动触发数据持久化",
//        description = "手动将今日的Redis统计数据持久化到MySQL数据库"
//    )
//    @PostMapping("/persist/today")
//    public ResultVO<Map<String, Object>> persistTodayStats() {
//        try {
//            int count = bookStatsPersistenceService.persistTodayStats();
//            Map<String, Object> result = new HashMap<>();
//            result.put("persistedCount", count);
//            return ResultVO.success("数据持久化成功", result);
//        } catch (Exception e) {
//            return ResultVO.error(500, "数据持久化失败: " + e.getMessage());
//        }
//    }

    /**
     * 手动触发昨日数据持久化并清理
     */
//    @Operation(
//        summary = "手动触发昨日数据持久化并清理",
//        description = "手动将昨日的Redis统计数据持久化到MySQL数据库并清理Redis中的数据"
//    )
//    @PostMapping("/persist/yesterday")
//    public ResultVO<Map<String, Object>> persistAndCleanYesterdayStats() {
//        try {
//            int count = bookStatsPersistenceService.persistAndCleanYesterdayStats();
//            Map<String, Object> result = new HashMap<>();
//            result.put("persistedCount", count);
//            return ResultVO.success("昨日数据持久化并清理成功", result);
//        } catch (Exception e) {
//            return ResultVO.error(500, "昨日数据持久化并清理失败: " + e.getMessage());
//        }
//    }

    /**
     * 获取当前有统计数据的书籍列表
     */
    @Operation(
        summary = "获取当前有统计数据的书籍列表",
        description = "获取Redis中当前有统计数据的所有书籍ID列表"
    )
    @GetMapping("/books")
    public ResultVO<Map<String, Object>> getAllBookIds() {
        try {
            List<Long> bookIds = bookStatsRedisService.getAllBookIds();
            Map<String, Object> result = new HashMap<>();
            result.put("bookIds", bookIds);
            result.put("count", bookIds.size());
            return ResultVO.success("获取书籍列表成功", result);
        } catch (Exception e) {
            return ResultVO.error(500, "获取书籍列表失败: " + e.getMessage());
        }
    }

    /**
     * 测试Redis连接状态
     */
    @GetMapping("/redis-status")
    @Operation(summary = "检查Redis连接状态", description = "测试Redis连接和基本操作是否正常")
    public ResultVO<Map<String, Object>> checkRedisStatus() {
        Map<String, Object> status = new HashMap<>();

        try {
            // 1. 测试基本连接和写入操作
            Long testBookId = 999999L;
            bookStatsRedisService.incrementReadCount(testBookId, 1);
            status.put("writeTest", "成功");

            // 2. 测试读取操作
            Map<String, Integer> stats = bookStatsRedisService.getTodayStats(testBookId);
            status.put("readTest", "成功");
            status.put("testData", stats);

            // 3. 测试其他操作
            bookStatsRedisService.incrementRecommendVotes(testBookId, 1);
            bookStatsRedisService.incrementMonthlyTickets(testBookId, 1);
            bookStatsRedisService.incrementCollectionCount(testBookId, 1);
            status.put("multipleOpsTest", "成功");

            // 4. 获取最终数据
            Map<String, Integer> finalStats = bookStatsRedisService.getTodayStats(testBookId);
            status.put("finalStats", finalStats);

            return ResultVO.success("Redis连接正常，所有操作成功", status);

        } catch (Exception e) {
            status.put("error", e.getMessage());
            status.put("errorType", e.getClass().getSimpleName());

            // 判断具体的错误类型
            String errorMsg;
            if (e.getMessage().contains("Connection refused")) {
                errorMsg = "Redis服务未启动或端口错误";
            } else if (e.getMessage().contains("WRONGPASS") || e.getMessage().contains("AUTH")) {
                errorMsg = "Redis密码错误，请检查密码配置";
            } else if (e.getMessage().contains("timeout")) {
                errorMsg = "Redis连接超时，请检查网络或Redis配置";
            } else {
                errorMsg = "Redis连接失败: " + e.getMessage();
            }

            return ResultVO.error(500, errorMsg);
        }
    }
}