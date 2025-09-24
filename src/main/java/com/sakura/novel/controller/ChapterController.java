package com.sakura.novel.controller;

import com.sakura.novel.DTO.Response.ChapterSummary;
import com.sakura.novel.core.common.vo.ResultVO;
import com.sakura.novel.entity.Chapter;
import com.sakura.novel.DTO.Response.PageResult;
import com.sakura.novel.service.ChapterService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 章节控制器
 */
@RestController
@RequestMapping("/api/chapters")
@RequiredArgsConstructor
@Tag(name = "章节管理", description = "章节信息管理相关API")
public class ChapterController {

    private final ChapterService chapterService;

    // ===== 基础 CRUD 操作 =====

    /**
     * 创建章节
     */
    @PostMapping
    @Operation(summary = "创建新章节", description = "创建一个新的章节")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "创建成功"),
            @ApiResponse(responseCode = "400", description = "请求参数错误或章节号已存在")
    })
    public ResultVO<Chapter> createChapter(@RequestBody Chapter chapter) {
        try {
            Chapter createdChapter = chapterService.createChapter(chapter);
            return ResultVO.success("创建章节成功", createdChapter);
        } catch (RuntimeException e) {
            return ResultVO.error(400, "请求参数错误或章节号已存在");
        }
    }

    /**
     * 根据ID删除章节
     */
    @DeleteMapping("/{id}")
    @Operation(summary = "删除章节", description = "根据ID删除指定章节")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "删除成功"),
            @ApiResponse(responseCode = "404", description = "章节不存在"),
            @ApiResponse(responseCode = "400", description = "删除失败")
    })
    public ResultVO<Void> deleteChapter(
            @Parameter(description = "章节ID", required = true) @PathVariable Integer id) {
        try {
            boolean deleted = chapterService.deleteById(id);
            if (deleted) {
                return ResultVO.success("删除章节成功", null);
            } else {
                return ResultVO.error(404, "章节不存在");
            }
        } catch (RuntimeException e) {
            return ResultVO.error(400, "删除失败");
        }
    }

    /**
     * 根据书籍ID删除所有章节
     */
    @DeleteMapping("/book/{bookId}")
    @Operation(summary = "删除书籍所有章节", description = "根据书籍ID删除该书籍的所有章节")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "删除成功"),
            @ApiResponse(responseCode = "400", description = "删除失败")
    })
    public ResultVO<Void> deleteChaptersByBookId(
            @Parameter(description = "书籍ID", required = true) @PathVariable Integer bookId) {
        try {
            boolean deleted = chapterService.deleteByBookId(bookId);
            if (deleted) {
                return ResultVO.success("删除书籍所有章节成功", null);
            } else {
                return ResultVO.error(400, "删除失败");
            }
        } catch (RuntimeException e) {
            return ResultVO.error(400, "删除失败");
        }
    }

    /**
     * 更新章节
     */
    @PutMapping("/{id}")
    @Operation(summary = "更新章节", description = "更新指定ID的章节信息")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "更新成功"),
            @ApiResponse(responseCode = "404", description = "章节不存在"),
            @ApiResponse(responseCode = "400", description = "请求参数错误")
    })
    public ResultVO<Chapter> updateChapter(
            @Parameter(description = "章节ID", required = true) @PathVariable Integer id,
            @RequestBody Chapter chapter) {
        try {
            chapter.setId(id);
            Chapter updatedChapter = chapterService.updateChapter(chapter);
            return ResultVO.success("更新章节成功", updatedChapter);
        } catch (RuntimeException e) {
            return ResultVO.error(400, "请求参数错误");
        }
    }

    /**
     * 根据ID查询章节详情
     */
    @GetMapping("/{id}")
    @Operation(summary = "查询章节详情", description = "根据ID查询章节详细信息，包含正文内容")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "查询成功"),
            @ApiResponse(responseCode = "404", description = "章节不存在")
    })
    public ResultVO<Chapter> getChapterById(
            @Parameter(description = "章节ID", required = true) @PathVariable Integer id) {
        Chapter chapter = chapterService.getById(id);
        return chapter != null ? ResultVO.success("查询章节成功", chapter) : ResultVO.error(404, "章节不存在");
    }

    // ===== 核心查询接口 =====

    /**
     * 根据书籍ID和章节号查询章节
     */
    @GetMapping("/book/{bookId}/chapter/{chapterNumber}")
    @Operation(summary = "根据书籍ID和章节号查询章节", description = "根据书籍ID和章节号查询章节详细信息")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "查询成功"),
            @ApiResponse(responseCode = "404", description = "章节不存在")
    })
    public ResultVO<Chapter> getChapterByBookIdAndNumber(
            @Parameter(description = "书籍ID", required = true) @PathVariable Integer bookId,
            @Parameter(description = "章节号", required = true) @PathVariable Integer chapterNumber) {
        Chapter chapter = chapterService.getByBookIdAndChapterNumber(bookId, chapterNumber);
        return chapter != null ? ResultVO.success("查询章节成功", chapter) : ResultVO.error(404, "章节不存在");
    }

    /**
     * 查询书籍所有章节
     */
    @GetMapping("/book/{bookId}/all")
    @Operation(summary = "查询书籍所有章节", description = "查询指定书籍的所有章节，不分页")
    @ApiResponse(responseCode = "200", description = "查询成功")
    public ResultVO<List<Chapter>> getAllChaptersByBookId(
            @Parameter(description = "书籍ID", required = true) @PathVariable Integer bookId) {
        List<Chapter> chapters = chapterService.getAllChaptersByBookId(bookId);
        return ResultVO.success("查询所有章节成功", chapters);
    }

    /**
     * 获取章节阅读信息
     */
    @GetMapping("/book/{bookId}/chapter/{chapterNumber}/read")
    @Operation(summary = "获取章节阅读信息", description = "获取章节阅读信息，包含当前章节、前后章节导航信息")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "查询成功"),
            @ApiResponse(responseCode = "404", description = "章节不存在")
    })
    public ResultVO<ChapterService.ChapterReadInfo> getChapterReadInfo(
            @Parameter(description = "书籍ID", required = true) @PathVariable Integer bookId,
            @Parameter(description = "章节号", required = true) @PathVariable Integer chapterNumber) {
        try {
            ChapterService.ChapterReadInfo readInfo = chapterService.getChapterReadInfo(bookId, chapterNumber);
            return ResultVO.success("获取章节阅读信息成功", readInfo);
        } catch (RuntimeException e) {
            return ResultVO.error(404, "章节不存在");
        }
    }
    @GetMapping("book/{bookId}/list")
    @Operation(summary = "分页查询书籍章节列表", description = "分页查询指定书籍的章节列表")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "查询成功"),
            @ApiResponse(responseCode = "400", description = "请求参数错误")
    })
    public ResultVO<PageResult<ChapterSummary>> getChaptersByBookId(
            @Parameter(description = "书籍ID", required = true) @PathVariable Integer bookId,
            @Parameter(description = "页码", required = false) @RequestParam(defaultValue = "1") Integer pageNum,
            @Parameter(description = "每页数量", required = false) @RequestParam(defaultValue = "200") Integer pageSize) {
        if (pageNum <= 0 || pageSize <= 0) {
            return ResultVO.error(400, "请求参数错误");
        }
        PageResult<ChapterSummary> pageResult = chapterService.getChaptersList(bookId, pageNum, pageSize);
        return ResultVO.success("查询章节列表成功", pageResult);
    }

    // ===== 统计和管理接口 =====

    /**
     * 获取书籍章节总数
     */
    @GetMapping("/book/{bookId}/count")
    @Operation(summary = "获取章节总数", description = "获取指定书籍的章节总数")
    @ApiResponse(responseCode = "200", description = "查询成功")
    public ResultVO<Integer> getChapterCountByBookId(
            @Parameter(description = "书籍ID", required = true) @PathVariable Integer bookId) {
        int count = chapterService.getChapterCountByBookId(bookId);
        return ResultVO.success("获取章节总数成功", count);
    }

    /**
     * 获取书籍VIP章节数量
     */
    @GetMapping("/book/{bookId}/vip-count")
    @Operation(summary = "获取VIP章节数量", description = "获取指定书籍的VIP章节数量")
    @ApiResponse(responseCode = "200", description = "查询成功")
    public ResultVO<Integer> getVipChapterCountByBookId(
            @Parameter(description = "书籍ID", required = true) @PathVariable Integer bookId) {
        int count = chapterService.getVipChapterCountByBookId(bookId);
        return ResultVO.success("获取VIP章节数量成功", count);
    }

    /**
     * 获取最新章节
     */
    @GetMapping("/book/{bookId}/latest")
    @Operation(summary = "获取最新章节", description = "获取指定书籍的最新章节")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "查询成功"),
            @ApiResponse(responseCode = "404", description = "没有章节")
    })
    public ResultVO<Chapter> getLatestChapterByBookId(
            @Parameter(description = "书籍ID", required = true) @PathVariable Integer bookId) {
        Chapter chapter = chapterService.getLatestChapterByBookId(bookId);
        return chapter != null ? ResultVO.success("获取最新章节成功", chapter) : ResultVO.error(404, "没有章节");
    }

    /**
     * 批量创建章节
     */
    @PostMapping("/batch")
    @Operation(summary = "批量创建章节", description = "批量创建多个章节")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "批量创建成功"),
            @ApiResponse(responseCode = "400", description = "批量创建失败")
    })
    public ResultVO<Void> batchCreateChapters(@RequestBody List<Chapter> chapters) {
        try {
            boolean created = chapterService.batchCreateChapters(chapters);
            if (created) {
                return ResultVO.success("批量创建章节成功", null);
            } else {
                return ResultVO.error(400, "批量创建失败");
            }
        } catch (RuntimeException e) {
            return ResultVO.error(400, "批量创建失败");
        }
    }

    /**
     * 检查章节是否存在
     */
    @GetMapping("/exists")
    @Operation(summary = "检查章节是否存在", description = "检查指定书籍的指定章节号是否已存在")
    @ApiResponse(responseCode = "200", description = "检查完成")
    public ResultVO<Boolean> checkChapterExists(
            @Parameter(description = "书籍ID", required = true) @RequestParam Integer bookId,
            @Parameter(description = "章节号", required = true) @RequestParam Integer chapterNumber) {
        boolean exists = chapterService.existsByBookIdAndChapterNumber(bookId, chapterNumber);
        return ResultVO.success("检查完成", exists);
    }
}
