package com.sakura.novel.service.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.sakura.novel.DTO.Response.PageResult;
import com.sakura.novel.entity.*;
import com.sakura.novel.DTO.Response.BookBasicDTO;
import com.sakura.novel.DTO.Response.BookDetailResponse;
import com.sakura.novel.mapper.BookMapper;
import com.sakura.novel.mapper.AuthorMapper;
import com.sakura.novel.mapper.CategoryMapper;
import com.sakura.novel.mapper.ChapterMapper;
import com.sakura.novel.service.BookService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 书籍服务实现类
 */
@Service
@RequiredArgsConstructor
public class BookServiceImpl implements BookService {
    private final BookMapper bookMapper;
    private final AuthorMapper authorMapper;
    private final CategoryMapper categoryMapper;
    private final ChapterMapper chapterMapper;
    // ===== 注入新的 ElasticsearchClient =====
//    private final ElasticsearchClient esClient;
    // ===== 基础 CRUD 操作 =====

    @Override
    public Book createBook(Book book) {
        // 设置创建时间和更新时间
        LocalDateTime now = LocalDateTime.now();
        book.setCreateTime(now);
        book.setUpdateTime(now);

        // 检查书名是否已存在
        if (existsByTitle(book.getTitle())) {
            throw new RuntimeException("书名已存在");
        }

        // 设置默认值
        if (book.getWordCount() == null) {
            book.setWordCount(0);
        }
        if (book.getIsVip() == null) {
            book.setIsVip(false);
        }

        bookMapper.insert(book);
        return book;
    }

    @Override
    public boolean deleteById(Integer id) {
        Book book = getById(id);
        if (book == null) {
            throw new RuntimeException("书籍不存在");
        }
        return bookMapper.deleteById(id) > 0;
    }

    @Override
    public Book updateBook(Book book) {
        Book existingBook = getById(book.getId());
        if (existingBook == null) {
            throw new RuntimeException("书籍不存在");
        }

        // 如果修改了书名，检查是否重复
        if (!existingBook.getTitle().equals(book.getTitle())) {
            if (existsByTitle(book.getTitle())) {
                throw new RuntimeException("书名已存在");
            }
        }

        // 更新修改时间
        book.setUpdateTime(LocalDateTime.now());

        bookMapper.updateById(book);
        return book;
    }

    @Override
    public Book getById(Integer id) {
        return bookMapper.selectById(id);
    }

    // ===== 管理功能 =====

    @Override
    public boolean batchCreateBooks(List<Book> books) {
        if (books == null || books.isEmpty()) {
            return false;
        }

        // 设置创建时间和更新时间
        LocalDateTime now = LocalDateTime.now();
        for (Book book : books) {
            book.setCreateTime(now);
            book.setUpdateTime(now);

            // 设置默认值
            if (book.getWordCount() == null) {
                book.setWordCount(0);
            }
            if (book.getIsVip() == null) {
                book.setIsVip(false);
            }
        }

        return bookMapper.batchInsert(books) > 0;
    }

    @Override
    public boolean existsByTitle(String title) {
        return bookMapper.existsByTitle(title);
    }

    @Override
    public int getTotalCount() {
        return bookMapper.count();
    }


    // ===== 书籍列表显示（轻量级） =====

    @Override
    public PageResult<BookBasicDTO> getBookBasicsByPageHelper(Integer pageNum, Integer pageSize) {
        // 设置默认值
        if (pageNum == null || pageNum < 1) pageNum = 1;
        if (pageSize == null || pageSize < 1) pageSize = 10;

        // 开启分页
        PageHelper.startPage(pageNum, pageSize);
        List<BookBasicDTO> bookBasics = bookMapper.selectBookBasicList();

        // 获取分页信息
        PageInfo<BookBasicDTO> pageInfo = new PageInfo<>(bookBasics);

        // 转换为自定义的分页结果
        return new PageResult<>(pageInfo);
    }

    @Override
    public PageResult<BookBasicDTO> searchBookBasicWithPageHelper(String title, Integer channel, Integer categoryId,
                                                                 Integer authorId, Integer minWordCount, Integer maxWordCount,
                                                                 Integer status, Boolean isVip, Integer pageNum, Integer pageSize) {
        // 设置默认值
        if (pageNum == null || pageNum < 1) pageNum = 1;
        if (pageSize == null || pageSize < 1) pageSize = 10;

        // 开启分页
        PageHelper.startPage(pageNum, pageSize);

        // 调用综合搜索方法
        List<BookBasicDTO> bookBasics = bookMapper.searchBookBasicWithAllConditions(title, channel, categoryId, authorId,
                                                                                    minWordCount, maxWordCount, status, isVip);

        // 获取分页信息
        PageInfo<BookBasicDTO> pageInfo = new PageInfo<>(bookBasics);

        // 转换为自定义的分页结果
        return new PageResult<>(pageInfo);
    }


    // ===== 新增聚合接口实现（服务层聚合，避免JOIN） =====

    @Override
    public BookDetailResponse getBookDetailById(Integer bookId) {
        // 1. 获取书籍基本信息
        Book book = bookMapper.selectById(bookId);
        if (book == null) {
            return null;
        }

        // 2. 分别获取作者信息
        Author author = authorMapper.selectById(book.getAuthorId());

        // 3. 分别获取分类信息
        Category category = categoryMapper.selectById(book.getCategoryId());

        // 4. 分别获取最新章节信息
        Chapter latestChapter = chapterMapper.selectLatestChapterByBookId(bookId);

        // 5. 在服务层进行聚合
        BookDetailResponse response = new BookDetailResponse();

        // 设置书籍基本信息
        response.setId(book.getId());
        response.setTitle(book.getTitle());
        response.setDescription(book.getDescription());
        response.setCoverImageUrl(book.getCoverImageUrl());
        response.setStatus(book.getStatus() == 1 ? "serializing" : "completed");
        response.setWordCount(book.getWordCount());
        response.setLastUpdateTime(book.getUpdateTime());

        // 聚合作者信息
        if (author != null) {
            BookDetailResponse.AuthorInfo authorInfo = new BookDetailResponse.AuthorInfo();
            authorInfo.setId(author.getId());
            authorInfo.setName(author.getName());
            authorInfo.setBio(author.getBio());
            // TODO: 需要添加 avatarUrl 字段到 Author 实体或从 User 表获取
             authorInfo.setAvatarUrl(author.getAvatarUrl());
            response.setAuthor(authorInfo);
        }

        // 聚合分类信息
        if (category != null) {
            BookDetailResponse.CategoryInfo categoryInfo = new BookDetailResponse.CategoryInfo();
            categoryInfo.setId(category.getId());
            categoryInfo.setName(category.getName());
            response.setCategory(categoryInfo);
        }

        // 聚合最新章节信息
        if (latestChapter != null) {
            BookDetailResponse.LatestChapterInfo latestChapterInfo = new BookDetailResponse.LatestChapterInfo();
            latestChapterInfo.setId(latestChapter.getId());
            latestChapterInfo.setTitle(latestChapter.getTitle());
            latestChapterInfo.setLastUpdateTime(latestChapter.getPublishedTime());
            response.setLatestChapter(latestChapterInfo);

        }

        return response;
    }

}
