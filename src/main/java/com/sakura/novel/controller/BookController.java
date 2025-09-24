package com.sakura.novel.controller;

import com.sakura.novel.DTO.Request.BookSearchReqDto;
import com.sakura.novel.DTO.Response.BookInfoRespDto;
import com.sakura.novel.core.common.vo.ResultVO;
import com.sakura.novel.entity.Book;
import com.sakura.novel.DTO.Response.PageResult;
import com.sakura.novel.DTO.Response.BookBasicDTO;
import com.sakura.novel.DTO.Response.BookDetailResponse;
import com.sakura.novel.service.BookService;
import com.sakura.novel.service.BookStatsRedisService;
import com.sakura.novel.service.impl.EsSearchServiceImpl;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 书籍控制器
 */
@RestController
@RequestMapping("/api/books")
@RequiredArgsConstructor
@Tag(name = "书籍管理", description = "书籍信息管理相关API")
public class BookController {

    private final BookService bookService;
    private final BookStatsRedisService bookStatsRedisService;
    private final EsSearchServiceImpl esSearchService;
    // ===== 基础 CRUD 操作 =====

    /**
     * 创建书籍
     */
    @PostMapping
    @Operation(summary = "创建新书籍", description = "创建一本新的书籍")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "创建成功"),
            @ApiResponse(responseCode = "400", description = "请求参数错误或书名已存在")
    })
    public ResultVO<Book> createBook(@RequestBody Book book) {
        try {
            Book createdBook = bookService.createBook(book);
            return ResultVO.success("创建书籍成功", createdBook);
        } catch (RuntimeException e) {
            return ResultVO.error(400, "请求参数错误或书名已存在");
        }
    }

    /**
     * 根据ID删除书籍
     */
    @DeleteMapping("/{id}")
    @Operation(summary = "删除书籍", description = "根据ID删除指定书籍")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "删除成功"),
            @ApiResponse(responseCode = "404", description = "书籍不存在"),
            @ApiResponse(responseCode = "400", description = "删除失败")
    })
    public ResultVO<Void> deleteBook(
            @Parameter(description = "书籍ID", required = true) @PathVariable Integer id) {
        try {
            boolean deleted = bookService.deleteById(id);
            if (deleted) {
                return ResultVO.success("删除书籍成功", null);
            } else {
                return ResultVO.error(404, "书籍不存在");
            }
        } catch (RuntimeException e) {
            return ResultVO.error(400, "删除失败");
        }
    }

    /**
     * 更新书籍
     */
    @PutMapping("/{id}")
    @Operation(summary = "更新书籍", description = "更新指定ID的书籍信息")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "更新成功"),
            @ApiResponse(responseCode = "404", description = "书籍不存在"),
            @ApiResponse(responseCode = "400", description = "请求参数错误")
    })
    public ResultVO<Book> updateBook(
            @Parameter(description = "书籍ID", required = true) @PathVariable Integer id,
            @RequestBody Book book) {
        try {
            book.setId(id);
            Book updatedBook = bookService.updateBook(book);
            return ResultVO.success("更新书籍成功", updatedBook);
        } catch (RuntimeException e) {
            return ResultVO.error(400, "请求参数错误");
        }
    }

    // ===== 新增聚合接口 =====

    /**
     * 获取书籍的完整聚合信息
     */
    @GetMapping("/{bookId}")
    @Operation(summary = "获取书籍的完整聚合信息",
               description = "一次性获取构建书籍详情页所需的所有核心信息。后端在服务层聚合了书籍、作者、分类和最新章节等多个数据源。")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "成功获取书籍的聚合详情"),
            @ApiResponse(responseCode = "404", description = "书籍未找到")
    })
    public ResultVO<BookDetailResponse> getBookDetailById(
            @Parameter(description = "要获取的书籍的唯一ID", required = true, example = "101")
            @PathVariable Integer bookId) {

        BookDetailResponse bookDetail = bookService.getBookDetailById(bookId);
        if (bookDetail == null) {
            return ResultVO.error(404, "书籍未找到");
        }
        bookStatsRedisService.incrementReadCount(Long.valueOf(bookId), 1);
        return ResultVO.success("获取书籍详情成功", bookDetail);
    }

    /**
     * 根据ID查询书籍详情（原始实体）
     */
    @GetMapping("/{id}/raw")
    @Operation(summary = "查询书籍原始信息", description = "根据ID查询书籍原始实体信息，主要用于编辑操作")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "查询成功"),
            @ApiResponse(responseCode = "404", description = "书籍不存在")
    })
    public ResultVO<Book> getBookById(
            @Parameter(description = "书籍ID", required = true) @PathVariable Integer id) {
        Book book = bookService.getById(id);
        return book != null ? ResultVO.success("查询书籍成功", book) : ResultVO.error(404, "书籍不存在");
    }

    // ===== 书籍列表显示接口（推荐使用） =====

    /**
     * 分页查询书籍基本信息列表
     */
    @GetMapping("/list")
    @Operation(summary = "分页查询书籍列表", description = "分页查询书籍基本信息列表，适用于书籍列表页面显示，性能优化的轻量级接口")
    @ApiResponse(responseCode = "200", description = "查询成功")
    public ResultVO<PageResult<BookBasicDTO>> getBookList(
            @Parameter(description = "页码，从1开始") @RequestParam(defaultValue = "1") Integer pageNum,
            @Parameter(description = "每页数量") @RequestParam(defaultValue = "10") Integer pageSize) {
        PageResult<BookBasicDTO> pageResult = bookService.getBookBasicsByPageHelper(pageNum, pageSize);
        return ResultVO.success("查询书籍列表成功", pageResult);
    }

    /**
     * 综合搜索书籍
     */
    @PostMapping("/search")
    @Operation(summary = "综合搜索书籍", description = "支持书名、频道、分类、字数范围、状态、VIP等所有搜索条件的组合搜索，返回轻量级书籍信息")
    @ApiResponse(responseCode = "200", description = "搜索成功")
    public ResultVO<PageResult<BookInfoRespDto>> searchBooks(
            @RequestBody BookSearchReqDto bookInfoRespDto) {
        PageResult<BookInfoRespDto> pageResult = esSearchService.searchBooks(bookInfoRespDto);
        return ResultVO.success("搜索书籍成功", pageResult);
    }
//    @GetMapping("/search")
//    @Operation(summary = "综合搜索书籍", description = "支持书名、频道、分类、字数范围、状态、VIP等所有搜索条件的组合搜索，返回轻量级书籍信息")
//    @ApiResponse(responseCode = "200", description = "搜索成功")
//    public ResultVO<PageResult<BookBasicDTO>> searchBooks(
//            @Parameter(description = "书名关键词") @RequestParam(required = false) String title,
//            @Parameter(description = "频道：1=男频，0=女频") @RequestParam(required = false) Integer channel,
//            @Parameter(description = "分类ID") @RequestParam(required = false) Integer categoryId,
//            @Parameter(description = "作者ID") @RequestParam(required = false) Integer authorId,
//            @Parameter(description = "最小字数") @RequestParam(required = false) Integer minWordCount,
//            @Parameter(description = "最大字数") @RequestParam(required = false) Integer maxWordCount,
//            @Parameter(description = "状态：1=连载中，0=已完结") @RequestParam(required = false) Integer status,
//            @Parameter(description = "是否VIP：true=VIP，false=免费") @RequestParam(required = false) Boolean isVip,
//            @Parameter(description = "页码，从1开始") @RequestParam(defaultValue = "1") Integer pageNum,
//            @Parameter(description = "每页数量") @RequestParam(defaultValue = "10") Integer pageSize) {
//
//        PageResult<BookBasicDTO> pageResult = bookService.searchBookBasicWithPageHelper(
//                title, channel, categoryId, authorId, minWordCount, maxWordCount, status, isVip, pageNum, pageSize);
//        return ResultVO.success("搜索书籍成功", pageResult);
//    }




    // ===== 管理功能接口 =====

    /**
     * 批量创建书籍
     */
    @PostMapping("/batch")
    @Operation(summary = "批量创建书籍", description = "批量创建多本书籍")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "批量创建成功"),
            @ApiResponse(responseCode = "400", description = "批量创建失败")
    })
    public ResultVO<Void> batchCreateBooks(@RequestBody List<Book> books) {
        try {
            boolean created = bookService.batchCreateBooks(books);
            if (created) {
                return ResultVO.success("批量创建书籍成功", null);
            } else {
                return ResultVO.error(400, "批量创建失败");
            }
        } catch (RuntimeException e) {
            return ResultVO.error(400, "批量创建失败");
        }
    }

    /**
     * 检查书名是否存在
     */
    @GetMapping("/exists")
    @Operation(summary = "检查书名是否存在", description = "检查指定的书名是否已存在")
    @ApiResponse(responseCode = "200", description = "检查完成")
    public ResultVO<Boolean> checkBookExists(
            @Parameter(description = "书名", required = true) @RequestParam String title) {
        boolean exists = bookService.existsByTitle(title);
        return ResultVO.success("检查完成", exists);
    }

    /**
     * 统计书籍总数
     */
    @GetMapping("/count")
    @Operation(summary = "统计书籍总数", description = "获取系统中书籍的总数量")
    @ApiResponse(responseCode = "200", description = "统计完成")
    public ResultVO<Integer> getTotalCount() {
        int count = bookService.getTotalCount();
        return ResultVO.success("统计完成", count);
    }
}
