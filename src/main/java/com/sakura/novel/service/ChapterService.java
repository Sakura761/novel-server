package com.sakura.novel.service;

import com.sakura.novel.DTO.Response.ChapterSummary;
import com.sakura.novel.entity.Chapter;
import com.sakura.novel.DTO.Response.PageResult;
import lombok.Data;

import java.util.List;

/**
 * 章节服务接口
 */
public interface ChapterService {

    // ===== 基础 CRUD 操作 =====

    /**
     * 创建章节
     */
    Chapter createChapter(Chapter chapter);

    /**
     * 根据ID删除章节
     */
    boolean deleteById(Integer id);

    /**
     * 根据书籍ID删除所有章节
     */
    boolean deleteByBookId(Integer bookId);

    /**
     * 更新章节
     */
    Chapter updateChapter(Chapter chapter);

    /**
     * 根据ID查询章节
     */
    Chapter getById(Integer id);

    // ===== 章节查询功能 =====

    /**
     * 根据书籍ID和章节号查询章节
     */
    Chapter getByBookIdAndChapterNumber(Integer bookId, Integer chapterNumber);

    /**
     * 分页查询指定书籍的章节列表（不包含正文内容，返回完整Chapter对象）
     */
//    PageResult<Chapter> getChapterList(Integer bookId, Integer pageNum, Integer pageSize);

    /**
     * 分页获取指定书籍的章节目录（轻量级，使用PageHelper，返回ChapterSummary）
     */
//    ChapterListResponse getChapterListByBookId(Integer bookId, Integer pageNum, Integer pageSize);

    /**
     * 查询指定书籍的所有章节
     */
    List<Chapter> getAllChaptersByBookId(Integer bookId);

    /**
     * 获取章节阅读信息（包含当前章节和前后章节导航）
     */
    ChapterReadInfo getChapterReadInfo(Integer bookId, Integer chapterNumber);

    // ===== 章节导航功能 =====

    /**
     * 获取指定书籍的前一章节
     */
    Chapter getPreviousChapter(Integer bookId, Integer chapterNumber);

    /**
     * 获取指定书籍的下一章节
     */
    Chapter getNextChapter(Integer bookId, Integer chapterNumber);

    /**
     * 获取指定书籍的最新章节
     */
    Chapter getLatestChapterByBookId(Integer bookId);

    // ===== 统计功能 =====

    /**
     * 统计指定书籍的章节总数
     */
    int getChapterCountByBookId(Integer bookId);

    /**
     * 统计指定书籍的VIP章节数
     */
    int getVipChapterCountByBookId(Integer bookId);

    // ===== 管理功能 =====

    /**
     * 批量创建章节
     */
    boolean batchCreateChapters(List<Chapter> chapters);

    /**
     * 检查章节是否存在
     */
    boolean existsByBookIdAndChapterNumber(Integer bookId, Integer chapterNumber);

    PageResult<ChapterSummary> getChaptersList(Integer bookId, Integer pageNum, Integer pageSize);

    // ===== 内部类定义 =====

    /**
     * 章节阅读信息（包含当前章节和前后章节导航）
     */
    @Data
    class ChapterReadInfo {
        private Chapter currentChapter;    // 当前章节
        private Chapter previousChapter;   // 前一章节
        private Chapter nextChapter;       // 下一章节
        private Integer totalChapters;     // 总章节数
        private boolean isFirst;           // 是否是第一章
        private boolean isLast;            // 是否是最后一章
    }
}
