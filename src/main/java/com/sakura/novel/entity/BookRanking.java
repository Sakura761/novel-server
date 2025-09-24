package com.sakura.novel.entity;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 书籍排行榜实体类
 */
@Data
public class BookRanking {
    private Long id;
    private Long bookId;
    private String rankType; // daily, weekly, monthly
    private String statType; // read_count, recommend_votes, monthly_tickets, collection_count
    private Integer rankPosition;
    private BigDecimal score;
    private LocalDate periodStart;
    private LocalDate periodEnd;
    private LocalDateTime createTime;

    // 关联的书籍信息
    private String bookTitle;
    private String authorName;
    private String categoryName;
    private String coverImageUrl;

    public BookRanking(Long bookId, String rankType, String statType, Integer rankPosition,
                      BigDecimal score, LocalDate periodStart, LocalDate periodEnd) {
        this.bookId = bookId;
        this.rankType = rankType;
        this.statType = statType;
        this.rankPosition = rankPosition;
        this.score = score;
        this.periodStart = periodStart;
        this.periodEnd = periodEnd;
        this.createTime = LocalDateTime.now();
    }
}
