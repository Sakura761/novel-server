package com.sakura.novel.entity;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * 书籍章节表实体类
 * 存储书籍的具体章节内容
 */
@Data
public class Chapter {

    private Integer id;
    private Integer bookId;
    private Integer chapterNumber;
    private String title;
    private String content;
    private Integer wordCount;
    private Boolean isVip;
    private LocalDateTime publishedTime;
    private LocalDateTime createTime;
}
