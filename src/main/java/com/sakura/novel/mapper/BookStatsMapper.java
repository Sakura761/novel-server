package com.sakura.novel.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface BookStatsMapper {

    /**
     * 根据每日统计数据，累加更新作品的总统计数据
     *
     * @param bookId          书籍ID
     * @param readCount       当日新增的阅读量
     * @param recommendVotes  当日新增的推荐票数
     * @param collectionCount 当日新增的收藏数 (可能为负数)
     * @return a
     */
    int updateBookStats(
            @Param("bookId") Long bookId,
            @Param("readCount") Integer readCount,
            @Param("recommendVotes") Integer recommendVotes,
            @Param("collectionCount") Integer collectionCount
    );
}