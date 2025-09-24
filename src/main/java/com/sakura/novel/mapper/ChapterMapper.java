package com.sakura.novel.mapper;

import com.sakura.novel.DTO.Response.ChapterSummary;
import com.sakura.novel.entity.Chapter;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * 章节表数据访问层
 */
@Mapper
public interface ChapterMapper {

    /**
     * 插入章节
     */
    int insert(Chapter chapter);

    /**
     * 根据ID删除章节
     */
    int deleteById(@Param("id") Integer id);

    /**
     * 根据书籍ID删除所有章节
     */
    int deleteByBookId(@Param("bookId") Integer bookId);

    /**
     * 更新章节信息
     */
    int updateById(Chapter chapter);

    /**
     * 根据ID查询章节
     */
    Chapter selectById(@Param("id") Integer id);

    /**
     * 根据书籍ID查询所有章节
     */
    List<Chapter> selectByBookId(@Param("bookId") Integer bookId);

    /**
     * 根据书籍ID和章节号查询章节
     */
    Chapter selectByBookIdAndChapterNumber(@Param("bookId") Integer bookId, @Param("chapterNumber") Integer chapterNumber);

    /**
     * 分页查询指定书籍的章节列表（不包含正文内容）
     */
    List<Chapter> selectChapterListByBookId(@Param("bookId") Integer bookId);

    /**
     * 查询指定书籍的章节总数
     */
    int countByBookId(@Param("bookId") Integer bookId);

    /**
     * 查询指定书籍的VIP章节数量
     */
    int countVipChaptersByBookId(@Param("bookId") Integer bookId);

    /**
     * 查询指定书籍的最新章节
     */
    Chapter selectLatestChapterByBookId(@Param("bookId") Integer bookId);

    /**
     * 批量插入章节
     */
    int batchInsert(@Param("chapters") List<Chapter> chapters);

    /**
     * 检查章节是否存在
     */
    boolean existsByBookIdAndChapterNumber(@Param("bookId") Integer bookId, @Param("chapterNumber") Integer chapterNumber);

    /**
     * 获取指定书籍的前一章节
     */
    Chapter selectPreviousChapter(@Param("bookId") Integer bookId, @Param("chapterNumber") Integer chapterNumber);

    /**
     * 获取指定书籍的下一章节
     */
    Chapter selectNextChapter(@Param("bookId") Integer bookId, @Param("chapterNumber") Integer chapterNumber);

    List<ChapterSummary> getChapterList(Integer bookId);
}
