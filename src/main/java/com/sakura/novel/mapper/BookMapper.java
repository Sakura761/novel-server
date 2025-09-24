package com.sakura.novel.mapper;

import com.sakura.novel.DTO.Request.BookSearchReqDto;
import com.sakura.novel.DTO.Response.BookInfoRespDto;
import com.sakura.novel.DTO.es.BookDocument;
import com.sakura.novel.entity.Book;
import com.sakura.novel.DTO.Response.BookBasicDTO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * 书籍表数据访问层
 */
@Mapper
public interface BookMapper {

    // ===== 基础 CRUD 操作 =====

    /**
     * 插入书籍
     */
    int insert(Book book);

    /**
     * 根据ID删除书籍
     */
    int deleteById(@Param("id") Integer id);

    /**
     * 更新书籍信息
     */
    int updateById(Book book);

    /**
     * 根据ID查询书籍
     */
    Book selectById(@Param("id") Integer id);

    // ===== 管理功能 =====

    /**
     * 批量插入书籍
     */
    int batchInsert(@Param("books") List<Book> books);

    /**
     * 检查书名是否存在
     */
    boolean existsByTitle(@Param("title") String title);

    /**
     * 统计书籍总数
     */
    int count();

    // ===== BookBasicDTO 相关查询（轻量级） =====

    /**
     * 查询所有书籍基本信息列表
     */
    List<BookBasicDTO> selectBookBasicList();

    /**
     * 综合搜索书籍基本信息
     */
    List<BookBasicDTO> searchBookBasicWithAllConditions(@Param("title") String title,
                                                        @Param("channel") Integer channel,
                                                        @Param("categoryId") Integer categoryId,
                                                        @Param("authorId") Integer authorId,
                                                        @Param("minWordCount") Integer minWordCount,
                                                        @Param("maxWordCount") Integer maxWordCount,
                                                        @Param("status") Integer status,
                                                        @Param("isVip") Boolean isVip);
    List<BookInfoRespDto> searchBooks(@Param("bookSearchReqDto") BookSearchReqDto bookSearchReqDto);

    @Select("SELECT b.id, b.title, a.name as authorName, a.id as authorId, b.description, b.cover_image_url as coverImageUrl, b.category_id, c.name as categoryName, c.channel," +
            "            b.is_vip, b.status as status, b.word_count as wordCount,  " +
            "            bs.last_updated_chapter_id as latestChapterId, c2.title as latestChapterTitle, c2.published_time as latestChapterUpdateTime," +
            "            bs.view_count as viewCount, bs.rating_count as ratingCount, bs.rating_average as ratingAverage, bs.collection_count as collectionCount, bs.recommend_count as recommendCount" +
            "            FROM books b" +
            "            LEFT JOIN authors a ON b.author_id = a.id" +
            "            LEFT JOIN categories c ON b.category_id = c.id" +
            "            LEFT JOIN book_stats bs ON b.id = bs.book_id" +
            "            LEFT JOIN chapters c2 ON bs.last_updated_chapter_id = c2.id")
    List<BookDocument> selectAllBooks();
}

