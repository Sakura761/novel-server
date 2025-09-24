package com.sakura.novel.entity;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 书籍每日统计数据实体类
 */
@Data
@Schema(name = "BookDailyStats", description = "书籍每日统计数据")
public class BookDailyStats {

    @Schema(description = "主键ID")
    private Long id;

    @Schema(description = "作品ID", required = true)
    private Long bookId;

    @Schema(description = "统计日期", required = true)
    private LocalDate statDate;

    @Schema(description = "当日阅读量")
    private Integer readCount = 0;

    @Schema(description = "当日获得的推荐票数")
    private Integer recommendVotes = 0;

    @Schema(description = "当日获得的月票数")
    private Integer monthlyTickets = 0;

    @Schema(description = "当日新增收藏数")
    private Integer collectionCount = 0;

    @Schema(description = "记录创建时间")
    private LocalDateTime createdTime;

    @Schema(description = "记录更新时间")
    private LocalDateTime updatedTime;
}
