package com.sakura.novel.service.impl;

import com.github.pagehelper.PageInfo;
import com.sakura.novel.DTO.Response.ChapterSummary;
import com.sakura.novel.entity.Chapter;
import com.sakura.novel.DTO.Response.PageResult;
import com.sakura.novel.mapper.ChapterMapper;
import com.sakura.novel.service.ChapterService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 章节服务实现类
 */
@Service
@RequiredArgsConstructor
public class ChapterServiceImpl implements ChapterService {

    private final ChapterMapper chapterMapper;

    // ===== 基础 CRUD 操作 =====

    @Override
    public Chapter createChapter(Chapter chapter) {
        // 设置创建时间
        chapter.setCreateTime(LocalDateTime.now());

        // 检查章节是否已存在
        if (existsByBookIdAndChapterNumber(chapter.getBookId(), chapter.getChapterNumber())) {
            throw new RuntimeException("该书籍的章节号已存在");
        }

        // 设置默认值
        if (chapter.getWordCount() == null) {
            chapter.setWordCount(0);
        }
        if (chapter.getIsVip() == null) {
            chapter.setIsVip(false);
        }
        if (chapter.getPublishedTime() == null) {
            chapter.setPublishedTime(LocalDateTime.now());
        }

        chapterMapper.insert(chapter);
        return chapter;
    }

    @Override
    public boolean deleteById(Integer id) {
        Chapter chapter = getById(id);
        if (chapter == null) {
            throw new RuntimeException("章节不存在");
        }
        return chapterMapper.deleteById(id) > 0;
    }

    @Override
    public boolean deleteByBookId(Integer bookId) {
        return chapterMapper.deleteByBookId(bookId) > 0;
    }

    @Override
    public Chapter updateChapter(Chapter chapter) {
        Chapter existingChapter = getById(chapter.getId());
        if (existingChapter == null) {
            throw new RuntimeException("章节不存在");
        }

        // 如果修改了章节号，检查是否重复
        if (!existingChapter.getChapterNumber().equals(chapter.getChapterNumber())) {
            if (existsByBookIdAndChapterNumber(chapter.getBookId(), chapter.getChapterNumber())) {
                throw new RuntimeException("该书籍的章节号已存在");
            }
        }

        chapterMapper.updateById(chapter);
        return chapter;
    }

    @Override
    public Chapter getById(Integer id) {
        return chapterMapper.selectById(id);
    }

    // ===== 章节查询功能 =====

    @Override
    public Chapter getByBookIdAndChapterNumber(Integer bookId, Integer chapterNumber) {
        return chapterMapper.selectByBookIdAndChapterNumber(bookId, chapterNumber);
    }

//    @Override
//    public PageResult<Chapter> getChapterList(Integer bookId, Integer pageNum, Integer pageSize) {
//        // 设置默认分页参数
//        if (pageNum == null || pageNum < 1) pageNum = 1;
//        if (pageSize == null || pageSize < 1) pageSize = 50;
//        if (pageSize > 200) pageSize = 200; // 限制最大页面大小
//
//        // 使用PageHelper进行分页查询
//        PageHelper.startPage(pageNum, pageSize);
//        List<Chapter> chapters = chapterMapper.selectChapterListByBookId(bookId);
//        PageInfo<Chapter> pageInfo = new PageInfo<>(chapters);
//
//        // 转换为自定义的分页结果
//        return convertToPageResult(pageInfo);
//    }
//
//    @Override
//    public ChapterListResponse getChapterListByBookId(Integer bookId, Integer pageNum, Integer pageSize) {
//        // 设置默认分页参数
//        if (pageNum == null || pageNum < 1) pageNum = 1;
//        if (pageSize == null || pageSize < 1) pageSize = 50;
//        if (pageSize > 200) pageSize = 200; // 限制最大页面大小
//
//        // 使用PageHelper进行分页查询章节摘要（轻量级，性能更好）
//        PageHelper.startPage(pageNum, pageSize);
//        List<ChapterSummary> chapterSummaries = chapterMapper.selectChapterSummaryByBookId(bookId);
//        PageInfo<ChapterSummary> pageInfo = new PageInfo<>(chapterSummaries);
//
//        // 构建响应对象
//        ChapterListResponse response = new ChapterListResponse();
//
//        // 设置分页信息
//        ChapterListResponse.PaginationInfo pagination = new ChapterListResponse.PaginationInfo();
//        pagination.setTotalChapters((int) pageInfo.getTotal());
//        pagination.setCurrentPage(pageInfo.getPageNum());
//        pagination.setPageSize(pageInfo.getPageSize());
//        pagination.setTotalPages(pageInfo.getPages());
//
//        response.setPagination(pagination);
//        response.setChapters(chapterSummaries);
//
//        return response;
//    }

    @Override
    public List<Chapter> getAllChaptersByBookId(Integer bookId) {
        return chapterMapper.selectByBookId(bookId);
    }

    @Override
    public ChapterReadInfo getChapterReadInfo(Integer bookId, Integer chapterNumber) {
        // 获取当前章节
        Chapter currentChapter = getByBookIdAndChapterNumber(bookId, chapterNumber);
        if (currentChapter == null) {
            throw new RuntimeException("章节不存在");
        }

        // 获取前一章节和下一章节
        Chapter previousChapter = getPreviousChapter(bookId, chapterNumber);
        Chapter nextChapter = getNextChapter(bookId, chapterNumber);

        // 获取总章节数
        int totalChapters = getChapterCountByBookId(bookId);

        // 构建章节阅读信息
        ChapterReadInfo readInfo = new ChapterReadInfo();
        readInfo.setCurrentChapter(currentChapter);
        readInfo.setPreviousChapter(previousChapter);
        readInfo.setNextChapter(nextChapter);
        readInfo.setTotalChapters(totalChapters);
        readInfo.setFirst(previousChapter == null);
        readInfo.setLast(nextChapter == null);

        return readInfo;
    }

    // ===== 章节导航功能 =====

    @Override
    public Chapter getPreviousChapter(Integer bookId, Integer chapterNumber) {
        return chapterMapper.selectPreviousChapter(bookId, chapterNumber);
    }

    @Override
    public Chapter getNextChapter(Integer bookId, Integer chapterNumber) {
        return chapterMapper.selectNextChapter(bookId, chapterNumber);
    }

    @Override
    public Chapter getLatestChapterByBookId(Integer bookId) {
        return chapterMapper.selectLatestChapterByBookId(bookId);
    }

    // ===== 统计功能 =====

    @Override
    public int getChapterCountByBookId(Integer bookId) {
        return chapterMapper.countByBookId(bookId);
    }

    @Override
    public int getVipChapterCountByBookId(Integer bookId) {
        return chapterMapper.countVipChaptersByBookId(bookId);
    }

    // ===== 管理功能 =====

    @Override
    public boolean batchCreateChapters(List<Chapter> chapters) {
        if (chapters == null || chapters.isEmpty()) {
            return false;
        }

        // 设置创建时间和默认值
        LocalDateTime now = LocalDateTime.now();
        for (Chapter chapter : chapters) {
            chapter.setCreateTime(now);

            if (chapter.getWordCount() == null) {
                chapter.setWordCount(0);
            }
            if (chapter.getIsVip() == null) {
                chapter.setIsVip(false);
            }
            if (chapter.getPublishedTime() == null) {
                chapter.setPublishedTime(now);
            }
        }

        return chapterMapper.batchInsert(chapters) > 0;
    }

    @Override
    public boolean existsByBookIdAndChapterNumber(Integer bookId, Integer chapterNumber) {
        return chapterMapper.existsByBookIdAndChapterNumber(bookId, chapterNumber);
    }

    @Override
    public PageResult<ChapterSummary> getChaptersList(Integer bookId, Integer pageNum, Integer pageSize) {
        // 设置默认分页参数
        if (pageNum == null || pageNum < 1) pageNum = 1;
        if (pageSize == null || pageSize < 1) pageSize = 50;
        if (pageSize > 200) pageSize = 200; // 限制最大页面大小

        // 使用PageHelper进行分页查询章节摘要（轻量级，性能更好）
        com.github.pagehelper.PageHelper.startPage(pageNum, pageSize);
        List<ChapterSummary> chapterSummaries = chapterMapper.getChapterList(bookId);
        PageInfo<ChapterSummary> pageInfo = new PageInfo<>(chapterSummaries);

        return new PageResult<>(pageInfo);
    }

}
