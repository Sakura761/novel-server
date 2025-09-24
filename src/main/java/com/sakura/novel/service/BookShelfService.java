package com.sakura.novel.service;

import com.sakura.novel.DTO.Request.AddBookshelfRequest;
import com.sakura.novel.DTO.Response.BookshelfItemResponse;
import com.sakura.novel.DTO.Response.PageResult;
import com.sakura.novel.entity.UserBookshelf;

import java.util.List;

public interface BookShelfService {
    /**
     * 将一本书添加到用户的书架
     *
     * @param request 包含 userId, bookId 和可选的阅读进度信息的对象
     * @return 操作是否成功
     */
    boolean addToBookshelf(Integer userId, AddBookshelfRequest request);

    /**
     * 从用户的书架移除一本书
     *
     * @param userId 用户ID
     * @param bookId 书籍ID
     * @return 操作是否成功
     */
    boolean removeFromBookshelf(Integer userId, Integer bookId);

    /**
     * 批量从用户的书架移除书籍
     *
     * @param userId  用户ID
     * @param bookIds 书籍ID列表
     * @return 影响的记录数
     */
    int batchRemoveFromBookshelf(Integer userId, List<Integer> bookIds);

    /**
     * 更新阅读进度
     *
     * @param bookshelfUpdate 包含 userId, bookId 和要更新的进度信息的对象
     * @return 操作是否成功
     */
    boolean updateReadingProgress(UserBookshelf bookshelfUpdate);

    /**
     * 分页获取用户的书架列表
     *
     * @param userId   用户ID
     * @param pageNum  页码 (从1开始)
     * @param pageSize 每页数量
     * @return 包含分页信息的书架列表
     */
    PageResult<BookshelfItemResponse> getUserBookshelf(Integer userId, int pageNum, int pageSize);

    /**
     * 检查某本书是否已在用户的书架上
     *
     * @param userId 用户ID
     * @param bookId 书籍ID
     * @return true 如果存在，false 如果不存在
     */
    boolean isBookInBookshelf(Integer userId, Integer bookId);

}
