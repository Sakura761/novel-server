package com.sakura.novel.entity;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * 新闻公告表实体类
 * 用于发布网站新闻或公告
 */
@Data
public class News {

    private Integer id;
    private String title;
    private String content;
    private Integer authorId;
    private LocalDateTime publishedTime;
    private LocalDateTime createTime;
}
