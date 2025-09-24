package com.sakura.novel.mapper;

import com.sakura.novel.DTO.Response.BookshelfItemResponse;
import com.sakura.novel.entity.UserBookshelf;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface BookShelfMapper {
    // --- 用户端常用操作 (新增) ---

    /**
     * 根据用户ID分页查询其书架列表，按最后阅读时间降序排序
     *
     * @param userId 用户ID
     * @return 该用户在指定页的书架列表
     */
    List<BookshelfItemResponse> selectByUserId(@Param("userId") Integer userId);

    /**
     * 统计指定用户的书架上有多少本书 (用于分页)
     *
     * @param userId 用户ID
     * @return 书籍总数
     */
    int countByUserId(@Param("userId") Integer userId);

    /**
     * 更新用户的阅读进度和时间 (动态更新)
     * 只更新传入对象中不为 null 的字段，更加高效
     *
     * @param bookshelfUpdate 包含 userId, bookId 和需要更新的字段的对象
     * @return 影响的行数
     */
    int updateReadingProgress(UserBookshelf bookshelfUpdate);

    /**
     * 向书架中插入一条新记录
     * @param userBookshelf 待插入的书架实体
     * @return 影响的行数，通常为 1
     */
    int insert(UserBookshelf userBookshelf);
    /**
     * 根据用户ID和书籍ID从书架移除一本书
     *
     * @param userId 用户ID
     * @param bookId 书籍ID
     * @return 影响的行数
     */
    int deleteByUserIdAndBookId(@Param("userId") Integer userId, @Param("bookId") Integer bookId);

    /**
     * 批量从书架移除书籍
     *
     * @param userId  用户ID
     * @param bookIds 要移除的书籍ID列表
     * @return 影响的行数
     */
    int batchDeleteByBookIds(@Param("userId") Integer userId, @Param("bookIds") List<Integer> bookIds);

    /**
     * 检查某本书是否已在用户的书架上
     *
     * @param userId 用户ID
     * @param bookId 书籍ID
     * @return 存在的记录数 (0 或 1)
     */
    int existsInBookshelf(@Param("userId") Integer userId, @Param("bookId") Integer bookId);

}
