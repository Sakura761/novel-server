package com.sakura.novel.entity;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 书籍信息表实体类
 * 存储书籍的基本信息
 */
@Data
@Schema(description = "书籍信息")
public class Book {

    @Schema(description = "书籍ID", example = "1")
    private Integer id;

    @Schema(description = "书籍标题", example = "斗破苍穹")
    private String title;

    @Schema(description = "作者ID", example = "1")
    private Integer authorId;

    @Schema(description = "分类ID", example = "1")
    private Integer categoryId;

    @Schema(description = "书籍简介", example = "这是一本玄幻小说...")
    private String description;

    @Schema(description = "封面图片URL", example = "https://example.com/cover.jpg")
    private String coverImageUrl;

    @Schema(description = "书籍状态：1=连载中，0=已完结", example = "1")
    private Integer status; // 状态: 1=连载中, 0=已完结

    @Schema(description = "是否VIP书籍", example = "false")
    private Boolean isVip;

    @Schema(description = "总字数", example = "1000000")
    private Integer wordCount;

    @Schema(description = "发布时间", example = "2023-12-01T10:00:00")
    private LocalDateTime publishedTime;

    @Schema(description = "创建时间", example = "2023-12-01T10:00:00")
    private LocalDateTime createTime;

    @Schema(description = "更新时间", example = "2023-12-01T10:00:00")
    private LocalDateTime updateTime;

    // 状态常量
    public static final int STATUS_SERIALIZING = 1; // 连载中
    public static final int STATUS_COMPLETED = 0;   // 已完结
}
