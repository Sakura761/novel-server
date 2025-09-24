package com.sakura.novel.entity;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 用户书架表实体类
 * 记录用户收藏的书籍和阅读进度
 */
@Data
public class UserBookshelf {

    private Integer id;
    private Integer userId;
    private Integer bookId;
    private Integer lastReadChapterId;
    private LocalDateTime lastReadTime;
    private LocalDateTime addedTime;
}
