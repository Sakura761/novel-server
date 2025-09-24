package com.sakura.novel.service;

import com.sakura.novel.DTO.Response.BookDetailResponse;
import com.sakura.novel.entity.Book;
import com.sakura.novel.DTO.Response.PageResult;
import com.sakura.novel.DTO.Response.BookBasicDTO;

import java.util.List;

/**
 * 书籍服务接口
 */
public interface BookService {

    // ===== 基础 CRUD 操作 =====

    /**
     * 创建书籍
     */
    Book createBook(Book book);

    /**
     * 根据ID删除书籍
     */
    boolean deleteById(Integer id);

    /**
     * 更新书籍
     */
    Book updateBook(Book book);

    /**
     * 根据ID查询书籍
     */
    Book getById(Integer id);

    // ===== 管理功能 =====

    /**
     * 批量创建书籍
     */
    boolean batchCreateBooks(List<Book> books);

    /**
     * 检查书名是否存在
     */
    boolean existsByTitle(String title);

    /**
     * 统计书籍总数
     */
    int getTotalCount();

    // ===== 书籍列表显示（轻量级） =====

    /**
     * 分页查询书籍基本信息
     */
    PageResult<BookBasicDTO> getBookBasicsByPageHelper(Integer pageNum, Integer pageSize);

    /**
     * 综合搜索书籍基本信息
     */
    PageResult<BookBasicDTO> searchBookBasicWithPageHelper(String title, Integer channel, Integer categoryId,
                                                          Integer authorId, Integer minWordCount, Integer maxWordCount,
                                                          Integer status, Boolean isVip, Integer pageNum, Integer pageSize);

    // ===== 书籍详情显示（完整信息） =====


    BookDetailResponse getBookDetailById(Integer bookId);
}

