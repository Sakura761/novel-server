package com.sakura.novel.DTO.Response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 章节列表中的单个章节摘要信息
 */
@Data
@Schema(description = "章节列表中的单个章节摘要信息")
public class ChapterSummary {

    @Schema(description = "章节ID", example = "1")
    private Integer id;

    @Schema(description = "章节序号", example = "1")
    private Integer chapterNumber;

    @Schema(description = "章节标题", example = "第1章 陨落的天才")
    private String title;
}
