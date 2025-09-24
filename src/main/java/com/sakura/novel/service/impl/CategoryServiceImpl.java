package com.sakura.novel.service.impl;

import com.sakura.novel.entity.Category;
import com.sakura.novel.mapper.CategoryMapper;
import com.sakura.novel.service.CategoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 分类服务实现类
 */
@Service
@RequiredArgsConstructor
public class CategoryServiceImpl implements CategoryService {

    private final CategoryMapper categoryMapper;

    @Override
    public Category createCategory(Category category) {
        // 设置创建时间
        category.setCreateTime(LocalDateTime.now());

        // 检查分类名是否已存在
        if (existsByChannelAndName(category.getChannel(), category.getName())) {
            throw new RuntimeException("该频道下分类名已存在");
        }

        categoryMapper.insert(category);
        return category;
    }

    @Override
    public boolean deleteById(Integer id) {
        // 检查是否有子分类
        if (hasChildren(id)) {
            throw new RuntimeException("该分类下存在子分类，无法删除");
        }

        return categoryMapper.deleteById(id) > 0;
    }

    @Override
    public Category updateCategory(Category category) {
        Category existingCategory = getById(category.getId());
        if (existingCategory == null) {
            throw new RuntimeException("分类不存在");
        }

        // 如果修改了名称，检查是否重复
        if (!existingCategory.getName().equals(category.getName())) {
            if (existsByChannelAndName(category.getChannel(), category.getName())) {
                throw new RuntimeException("该频道下分类名已存在");
            }
        }

        categoryMapper.updateById(category);
        return category;
    }

    @Override
    public Category getById(Integer id) {
        return categoryMapper.selectById(id);
    }

    @Override
    public List<Category> getAllCategories() {
        return categoryMapper.selectAll();
    }

    @Override
    public List<Category> getCategoriesByChannel(Integer channel) {
        return categoryMapper.selectByChannel(channel);
    }

    @Override
    public List<Category> getChildCategories(Integer parentId) {
        return categoryMapper.selectByParentId(parentId);
    }

    @Override
    public List<Category> getChildCategoriesByChannel(Integer channel, Integer parentId) {
        return categoryMapper.selectByChannelAndParentId(channel, parentId);
    }

    @Override
    public List<Category> getTopCategories() {
        return categoryMapper.selectTopCategories();
    }

    @Override
    public List<Category> getTopCategoriesByChannel(Integer channel) {
        return categoryMapper.selectTopCategoriesByChannel(channel);
    }

    @Override
    public List<Category> searchCategoriesByName(String name) {
        return categoryMapper.selectByNameLike(name);
    }

    @Override
    public boolean existsByName(String name) {
        return categoryMapper.existsByName(name);
    }

    @Override
    public boolean existsByChannelAndName(Integer channel, String name) {
        return categoryMapper.existsByChannelAndName(channel, name);
    }

    @Override
    public boolean hasChildren(Integer parentId) {
        return categoryMapper.hasChildren(parentId);
    }

    @Override
    public int getTotalCount() {
        return categoryMapper.count();
    }
}
