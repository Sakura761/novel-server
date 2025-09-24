package com.sakura.novel.service;

import com.sakura.novel.entity.Category;

import java.util.List;

/**
 * 分类服务接口
 */
public interface CategoryService {

    /**
     * 创建分类
     */
    Category createCategory(Category category);

    /**
     * 根据ID删除分类
     */
    boolean deleteById(Integer id);

    /**
     * 更新分类
     */
    Category updateCategory(Category category);

    /**
     * 根据ID查询分类
     */
    Category getById(Integer id);

    /**
     * 查询所有分类
     */
    List<Category> getAllCategories();

    /**
     * 根据频道查询分类
     */
    List<Category> getCategoriesByChannel(Integer channel);

    /**
     * 根据父级ID查询子分类
     */
    List<Category> getChildCategories(Integer parentId);

    /**
     * 根据频道和父级ID查询子分类
     */
    List<Category> getChildCategoriesByChannel(Integer channel, Integer parentId);

    /**
     * 查询顶级分类
     */
    List<Category> getTopCategories();

    /**
     * 根据频道查询顶级分类
     */
    List<Category> getTopCategoriesByChannel(Integer channel);

    /**
     * 根据名称模糊查询分类
     */
    List<Category> searchCategoriesByName(String name);

    /**
     * 检查分类名是否存在
     */
    boolean existsByName(String name);

    /**
     * 检查在指定频道下分类名是否存在
     */
    boolean existsByChannelAndName(Integer channel, String name);

    /**
     * 检查是否有子分类
     */
    boolean hasChildren(Integer parentId);

    /**
     * 统计分类总数
     */
    int getTotalCount();
}
