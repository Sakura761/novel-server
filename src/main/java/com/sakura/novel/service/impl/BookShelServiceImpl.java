package com.sakura.novel.service.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.sakura.novel.DTO.Request.AddBookshelfRequest;
import com.sakura.novel.DTO.Response.BookshelfItemResponse;
import com.sakura.novel.DTO.Response.PageResult;
import com.sakura.novel.entity.UserBookshelf;
import com.sakura.novel.service.BookShelfService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.sakura.novel.mapper.BookShelfMapper;
import org.springframework.util.CollectionUtils;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class BookShelServiceImpl implements BookShelfService {

    private final BookShelfMapper bookShelfMapper;
    @Override
    @Transactional // 建议写操作都加上事务注解
    public boolean addToBookshelf(Integer userId ,AddBookshelfRequest request) {
        Integer bookId = request.getBookId();

        // 1. 检查书架中是否已存在该书籍
        Boolean exist = this.isBookInBookshelf(userId, bookId);
        if (exist) {
            // 如果已经存在，直接返回 true，表示操作成功（书籍已在书架上）
            return false;
        }
        UserBookshelf bookshelf = new UserBookshelf();
        bookshelf.setUserId(userId);
        bookshelf.setBookId(bookId);
        // 设置阅读进度和时间，如果请求中提供了
        bookshelf.setLastReadChapterId(request.getLastReadChapterId());
        bookshelf.setLastReadTime(request.getLastReadTime());
            // 3. 如果不存在，则插入新记录
        bookshelf.setAddedTime(LocalDateTime.now()); // 设置加入书架时间
        bookShelfMapper.insert(bookshelf); // 插入后，bookshelf对象的id会被 MyBatis 填充
        return true;
    }

    @Override
    @Transactional
    public boolean removeFromBookshelf(Integer userId, Integer bookId) {
        return bookShelfMapper.deleteByUserIdAndBookId(userId, bookId) > 0;
    }

    @Override
    @Transactional
    public int batchRemoveFromBookshelf(Integer userId, List<Integer> bookIds) {
        // 如果列表为空，直接返回0，避免执行无效的SQL
        if (CollectionUtils.isEmpty(bookIds)) {
            return 0;
        }
        return bookShelfMapper.batchDeleteByBookIds(userId, bookIds);
    }

    @Override
    @Transactional
    public boolean updateReadingProgress(UserBookshelf bookshelfUpdate) {
        // 业务层强制设置最后阅读时间为当前时间
        bookshelfUpdate.setLastReadTime(LocalDateTime.now());
        return bookShelfMapper.updateReadingProgress(bookshelfUpdate) > 0;
    }

    @Override
    public PageResult<BookshelfItemResponse> getUserBookshelf(Integer userId, int pageNum, int pageSize) {
        // 1. 启动 PageHelper 分页
        PageHelper.startPage(pageNum, pageSize);

        // 2. 执行查询，MyBatis 会自动拦截并应用分页
        List<BookshelfItemResponse> list = bookShelfMapper.selectByUserId(userId);

        // 3. 用 PageInfo 包装查询结果，它包含了分页的所有详细信息
        PageInfo<BookshelfItemResponse> pageInfo = new PageInfo<>(list);

        // 4. 将 PageInfo 对象的数据转换成你自定义的 PageResult 对象
        PageResult<BookshelfItemResponse> pageResult = new PageResult<>();
        pageResult.setPageNum(pageInfo.getPageNum());
        pageResult.setPageSize(pageInfo.getPageSize());
        pageResult.setTotal(pageInfo.getTotal());
        pageResult.setPages(pageInfo.getPages());
        pageResult.setList(pageInfo.getList()); // 最重要的：设置数据列表

        // 设置 PageResult 中其他的布尔值和导航页码
        pageResult.setIsFirstPage(pageInfo.isIsFirstPage());
        pageResult.setIsLastPage(pageInfo.isIsLastPage());
        pageResult.setHasPreviousPage(pageInfo.isHasPreviousPage());
        pageResult.setHasNextPage(pageInfo.isHasNextPage());
        pageResult.setNavigatepageNums(pageInfo.getNavigatepageNums());
        return pageResult;
    }

    @Override
    public boolean isBookInBookshelf(Integer userId, Integer bookId) {
        return bookShelfMapper.existsInBookshelf(userId, bookId) > 0;
    }

}
