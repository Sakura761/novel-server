package com.sakura.novel.DTO.Response;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Data
public class RankingResponse {

    private String rankType; // "daily", "weekly", "monthly"
    private String statType; // "read_count", "recommend_votes", etc.
    private LocalDate periodStart;
    private LocalDate periodEnd;
    private List<RankingItem> rankings;

    @Data
    @NoArgsConstructor
    public static class RankingItem {
        private Integer rank;
        private Integer bookId;
        private String title;
        private String description;
        private String authorName;
        private String categoryName; // 将会是 "父分类 • 子分类" 的格式
        private String coverImageUrl;
        private String statusText; // "连载中" 或 "已完结"
        private Integer wordCount;
        private BigDecimal score; // 热度值或票数值

        private String latestChapterTitle;

        @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
        private LocalDateTime lastUpdatedTime;
    }
}