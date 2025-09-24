package com.sakura.novel.entity;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * 书评表实体类
 * 用户对书籍的评论和评分
 */
@Data
public class Review {

    private Integer id;
    private Integer userId;
    private Integer bookId;
    private Short rating;
    private String comment;
    private Integer likeCount;
    private Integer replyCount;
    private LocalDateTime createTime;
}
