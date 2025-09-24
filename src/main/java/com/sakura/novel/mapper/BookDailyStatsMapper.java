package com.sakura.novel.mapper;

import com.sakura.novel.entity.BookDailyStats;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.time.LocalDate;
import java.util.List;

/**
 * 书籍每日统计数据Mapper
 */
@Mapper
public interface BookDailyStatsMapper {

    /**
     * 插入或更新每日统计数据
     */
    int insertOrUpdate(BookDailyStats bookDailyStats);

    /**
     * 批量插入或更新每日统计数据
     */
    int batchInsertOrUpdate(@Param("list") List<BookDailyStats> statsList);

    /**
     * 根据书籍ID和日期查询统计数据
     */
    BookDailyStats selectByBookIdAndDate(@Param("bookId") Long bookId, @Param("statDate") LocalDate statDate);

    /**
     * 根据日期范围查询统计数据
     */
    List<BookDailyStats> selectByDateRange(@Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate);

    /**
     * 根据书籍ID查询指定日期范围的统计数据
     */
    List<BookDailyStats> selectByBookIdAndDateRange(
            @Param("bookId") Long bookId,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate
    );
}
