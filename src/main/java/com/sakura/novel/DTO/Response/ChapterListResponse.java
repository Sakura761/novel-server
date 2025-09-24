package com.sakura.novel.DTO.Response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;

/**
 * 章节列表分页响应 DTO
 * 符合 OpenAPI 规范的章节列表响应
 */
@Data
@Schema(description = "章节列表分页响应")
public class ChapterListResponse {

    @Schema(description = "分页信息")
    private PaginationInfo pagination;

    @Schema(description = "章节列表")
    private List<ChapterSummary> chapters;

    @Data
    @Schema(description = "分页信息")
    public static class PaginationInfo {
        @Schema(description = "总章节数", example = "1646")
        private Integer totalChapters;

        @Schema(description = "当前页码", example = "1")
        private Integer currentPage;

        @Schema(description = "每页数量", example = "50")
        private Integer pageSize;

        @Schema(description = "总页数", example = "33")
        private Integer totalPages;
    }
}
