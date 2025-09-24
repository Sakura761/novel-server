package com.sakura.novel.entity;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class Author {

    private Integer id;
    private String name;
    private String bio;
    private Integer userId;
    private Short level;
    private Long totalWordCount;
    private Integer totalBooks;
    private Integer followerCount;
    private LocalDateTime createTime;
    private String avatarUrl;
}
