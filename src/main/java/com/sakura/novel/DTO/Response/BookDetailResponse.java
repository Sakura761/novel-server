package com.sakura.novel.DTO.Response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 为书籍详情页定制的聚合数据传输对象 (DTO)
 * 符合 OpenAPI 规范的书籍详情响应
 */
@Data
@Schema(description = "为书籍详情页定制的聚合数据传输对象 (DTO)")
public class BookDetailResponse {

    @Schema(description = "书籍ID", example = "101")
    private Integer id;

    @Schema(description = "书名", example = "斗破苍穹")
    private String title;

    @Schema(description = "作品简介", example = "这里是属于斗气的世界，没有花俏艳丽的魔法...")
    private String description;

    @Schema(description = "封面图片URL", example = "https://example.com/covers/dpcq.jpg")
    private String coverImageUrl;

    @Schema(description = "书籍状态", allowableValues = {"serializing", "completed"}, example = "completed")
    private String status;

    @Schema(description = "总字数", example = "5297000")
    private Integer wordCount;

    @Schema(description = "书籍最新更新时间", example = "2025-09-04T16:50:02")
    private LocalDateTime lastUpdateTime;

    @Schema(description = "作者信息")
    private AuthorInfo author;

    @Schema(description = "分类信息")
    private CategoryInfo category;

    @Schema(description = "最新章节信息")
    private LatestChapterInfo latestChapter;

    @Data
    @Schema(description = "作者信息")
    public static class AuthorInfo {
        @Schema(description = "作者ID", example = "42")
        private Integer id;

        @Schema(description = "作者名", example = "天蚕土豆")
        private String name;

        @Schema(description = "作者简介", example = "知名网络小说家，代表作《斗破苍穹》。")
        private String bio;

        @Schema(description = "作者头像URL", example = "https://example.com/avatars/tctd.jpg")
        private String avatarUrl;
    }

    @Data
    @Schema(description = "分类信息")
    public static class CategoryInfo {
        @Schema(description = "分类ID", example = "1")
        private Integer id;

        @Schema(description = "分类名称", example = "玄幻")
        private String name;
    }

    @Data
    @Schema(description = "最新章节信息")
    public static class LatestChapterInfo {
        @Schema(description = "最新章节ID", example = "1646")
        private Integer id;

        @Schema(description = "最新章节标题", example = "第1646章 五帝破空")
        private String title;

        @Schema(description = "最新章节更新时间", example = "2025-09-04 16:50:02")
        private LocalDateTime lastUpdateTime;
    }
}
