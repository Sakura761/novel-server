package com.sakura.novel.mapper;

import com.sakura.novel.entity.Category;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 分类表数据访问层
 */
@Mapper
public interface CategoryMapper {

    /**
     * 插入分类
     */
    int insert(Category category);

    /**
     * 根据ID删除分类
     */
    int deleteById(@Param("id") Integer id);

    /**
     * 更新分类
     */
    int updateById(Category category);

    /**
     * 根据ID查询分类
     */
    Category selectById(@Param("id") Integer id);

    /**
     * 查询所有分类
     */
    List<Category> selectAll();

    /**
     * 根据父级ID查询子分类
     */
    List<Category> selectByParentId(@Param("parentId") Integer parentId);

    /**
     * 根据名称查询分类
     */
    Category selectByName(@Param("name") String name);

    /**
     * 查询顶级分类（父级ID为空）
     */
    List<Category> selectTopCategories();

    /**
     * 根据分类名模糊查询
     */
    List<Category> selectByNameLike(@Param("name") String name);

    /**
     * 统计分类总数
     */
    int count();

    /**
     * 检查分类名是否存在
     */
    boolean existsByName(@Param("name") String name);

    /**
     * 检查是否有子分类
     */
    boolean hasChildren(@Param("parentId") Integer parentId);

    /**
     * 根据频道查询分类
     */
    List<Category> selectByChannel(@Param("channel") Integer channel);

    /**
     * 根据频道和父级ID查询子分类
     */
    List<Category> selectByChannelAndParentId(@Param("channel") Integer channel, @Param("parentId") Integer parentId);

    /**
     * 根据频道查询顶级分类
     */
    List<Category> selectTopCategoriesByChannel(@Param("channel") Integer channel);

    /**
     * 根据频道和名称查询分类
     */
    Category selectByChannelAndName(@Param("channel") Integer channel, @Param("name") String name);

    /**
     * 检查在指定频道下分类名是否存在
     */
    boolean existsByChannelAndName(@Param("channel") Integer channel, @Param("name") String name);
}
