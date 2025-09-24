package com.sakura.novel.controller;

import com.sakura.novel.DTO.Request.AddBookshelfRequest;
import com.sakura.novel.core.common.vo.ResultVO;
import com.sakura.novel.DTO.Response.BookshelfItemResponse;
import com.sakura.novel.DTO.Response.PageResult;
import com.sakura.novel.service.BookShelfService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/bookshelf")
@Tag(name = "书架管理", description = "书架相关接口")
public class BookShelfController {

    private final BookShelfService userBookshelfService;
    /**
     * 获取用户的书架列表（分页）
     *
     * @param pageNum  页码，默认为1
     * @param pageSize 每页数量，默认为10
     * @return 封装了分页信息的书架列表
     */
    @Operation(summary = "获取用户书架列表（分页）", description = "获取指定用户的书架列表，支持分页")
    @GetMapping("/list/{userId}")
    public ResultVO<PageResult<BookshelfItemResponse>> getUserBookshelfPage(
            @Parameter(description = "用户ID", example = "1", required = true)
            @PathVariable(value = "userId") Integer UserId,
            @Parameter(description = "页码", example = "1")
            @RequestParam(value = "pageNum", defaultValue = "1") Integer pageNum,

            @Parameter(description = "每页数量", example = "10")
            @RequestParam(value = "pageSize", defaultValue = "10") Integer pageSize
    ) {

        // 调用 Service 层获取分页数据
        PageResult<BookshelfItemResponse> bookshelfPage =
                userBookshelfService.getUserBookshelf(UserId, pageNum, pageSize);

        // 使用统一响应类包装并返回
        return ResultVO.success("获取用户书架列表成功", bookshelfPage);
    }
    /**
     * 将书籍添加到书架
     *
     * @param request 包含 userId, bookId 和可选的阅读进度信息的对象
     * @return 操作结果
     */
    @Operation(summary = "将书籍添加到书架", description = "将指定书籍添加到用户的书架中")
    @PostMapping("/add/{userId}")
    public ResultVO<?> addToBookshelf(
            @Parameter(description = "用户ID", example = "1", required = true)
            @PathVariable ("userId") Integer userId,
            @Valid @RequestBody AddBookshelfRequest request) {
        try {
            Boolean result = userBookshelfService.addToBookshelf(userId,request);

            if (result) {

                return new ResultVO<>(200, "书籍已添加到书架", null);
            } else {
                return new ResultVO<>(404, "添加失败", null);
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
    /**
     * 移除书架中的书籍
     *
     * @param userId 用户ID
     * @param bookId 书籍ID
     * @return 操作结果
     */
    @Operation(summary = "从书架移除书籍", description = "将指定书籍从用户的书架中移除")
    @DeleteMapping("/remove/{userId}/{bookId}")
    public ResultVO<?> removeFromBookshelf(
            @Parameter(description = "用户ID", example = "1", required = true)
            @PathVariable("userId") Integer userId,
            @Parameter(description = "书籍ID", example = "1001", required = true)
            @PathVariable("bookId") Integer bookId) {
        boolean removed = userBookshelfService.removeFromBookshelf(userId, bookId);
        if (removed) {
            return ResultVO.success("从书架移除书籍成功");
        } else {
            return ResultVO.error(500, "从书架移除书籍失败");
        }
    }

    @GetMapping("/exists/{userId}/{bookId}")
    @Operation(summary = "检查书籍是否在书架上", description = "检查指定书籍是否已存在于用户的书架中")

    public ResultVO<?> isBookInBookshelf(
            @Parameter(description = "用户ID", example = "1", required = true)
            @PathVariable("userId") Integer userId,
            @Parameter(description = "书籍ID", example = "1001", required = true)
            @PathVariable("bookId") Integer bookId) {
        boolean exist = userBookshelfService.isBookInBookshelf(userId, bookId);
        if (exist) {
            return ResultVO.success("书籍已在书架上", true);
        } else {
            return ResultVO.success("书籍不在书架上", null);
        }
    }

}
