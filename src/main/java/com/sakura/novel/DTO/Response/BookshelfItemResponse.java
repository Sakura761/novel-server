package com.sakura.novel.DTO.Response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "书架项响应对象")
public class BookshelfItemResponse {
    /**
     *   id: number;
     *   userId: number;
     *   bookId: number;
     *   bookTitle: string;
     *   bookCover: string;
     *   authorName: string;
     *   categoryName: string;
     *   totalChapters: number;
     *   lastReadChapterNumber?: number;
     *   progress: number;
     *   lastReadAt: string;
     *   addedTime: string;
     *   readingStatus: string;
     */

    @Schema(description = "书架项ID", example = "1")
    private Integer id;
    @Schema(description = "用户ID", example = "1")
    private Integer userId;
    @Schema(description = "书籍ID", example = "1")
    private Integer bookId;
    @Schema(description = "书籍标题", example = "斗破苍穹")
    private String bookTitle;
    @Schema(description = "书籍封面URL", example = "https://example.com/cover.jpg")
    private String bookCover;
    @Schema(description = "书籍状态", example = "1")
    private Integer Status;
//    @Schema(description = "作者名称", example = "天蚕土豆")
//    private String authorName;
    @Schema(description = "分类名称", example = "玄幻")
    private String categoryName;
    @Schema(description = "总章节数", example = "500")
    private Integer totalChapters;
    @Schema(description = "最后阅读章节号", example = "150")
    private Integer lastReadChapterNumber;
    @Schema(description = "最后阅读时间", example = "2024-06-01T12:34:56")
    private String lastReadTime;
    @Schema(description = "加入书架时间", example = "2024-05-20T10:20:30")
    private String addedTime;
}
