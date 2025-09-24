package com.sakura.novel.DTO.Response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 书籍基本信息DTO
 * 用于API响应，包含书籍基本信息，不包含统计信息和章节信息
 */
@Data
@Schema(description = "书籍基本信息")
public class BookBasicDTO {

    @Schema(description = "书籍ID", example = "1")
    private Integer id;

    @Schema(description = "书籍标题", example = "斗破苍穹")
    private String title;

    @Schema(description = "作者ID", example = "1")
    private Integer authorId;

    @Schema(description = "作者名称", example = "天蚕土豆")
    private String authorName;

    @Schema(description = "作者简介", example = "知名网络作家")
    private String authorBio;

    @Schema(description = "分类ID", example = "1")
    private Integer categoryId;

    @Schema(description = "分类名称", example = "玄幻")
    private String categoryName;

    @Schema(description = "频道类型：1=男频，0=女频", example = "1")
    private Integer channel;

    @Schema(description = "频道描述", example = "男频")
    private String channelDescription;

    @Schema(description = "书籍简介", example = "这是一本玄幻小说...")
    private String description;

    @Schema(description = "封面图片URL", example = "https://example.com/cover.jpg")
    private String coverImageUrl;

    @Schema(description = "书籍状态：1=连载中，0=已完结", example = "1")
    private Integer status;

    @Schema(description = "状态描述", example = "连载中")
    private String statusDescription;

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

    /**
     * 获取状态描述
     */
    public String getStatusDescription() {
        if (status == null) {
            return "未知";
        }
        return status == 1 ? "连载中" : "已完结";
    }

    /**
     * 获取频道描述
     */
    public String getChannelDescription() {
        if (channel == null) {
            return "未知";
        }
        return channel == 1 ? "男频" : "女频";
    }
}
