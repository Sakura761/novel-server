package com.sakura.novel.entity;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 书籍统计表实体类
 * 存储书籍的动态统计数据，与books表一对一关联
 */
@Data
public class BookStats {

    private Integer bookId;
    private Long viewCount;
    private Integer ratingCount;
    private BigDecimal ratingAverage;
    private Integer collectionCount;
    private Integer commentCount;
    private Integer wordCount;
    private Integer recommendCount;
    private Integer lastUpdatedChapterId;
    private LocalDateTime lastUpdatedTime;
}
