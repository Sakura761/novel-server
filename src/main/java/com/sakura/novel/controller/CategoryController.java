package com.sakura.novel.controller;

import com.sakura.novel.core.common.vo.ResultVO;
import com.sakura.novel.entity.Category;
import com.sakura.novel.service.CategoryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 分类控制器
 */
@RestController
@RequestMapping("/api/categories")
@RequiredArgsConstructor
@Tag(name = "分类管理", description = "书籍分类管理相关API")
public class CategoryController {

    private final CategoryService categoryService;

    /**
     * 创建分类
     */
    @PostMapping
    @Operation(summary = "创建新分类", description = "创建一个新的书籍分类")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "创建成功"),
            @ApiResponse(responseCode = "400", description = "请求参数错误")
    })
    public ResultVO<Category> createCategory(@RequestBody Category category) {
        try {
            Category createdCategory = categoryService.createCategory(category);
            return ResultVO.success("创建分类成功", createdCategory);
        } catch (RuntimeException e) {
            return ResultVO.error(400, "请求参数错误");
        }
    }

    /**
     * 根据ID删除分类
     */
    @DeleteMapping("/{id}")
    @Operation(summary = "删除分类", description = "根据ID删除指定分类")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "删除成功"),
            @ApiResponse(responseCode = "404", description = "分类不存在"),
            @ApiResponse(responseCode = "400", description = "该分类下存在子分类，无法删除")
    })
    public ResultVO<Void> deleteCategory(
            @Parameter(description = "分类ID", required = true) @PathVariable Integer id) {
        try {
            boolean deleted = categoryService.deleteById(id);
            if (deleted) {
                return ResultVO.success("删除分类成功", null);
            } else {
                return ResultVO.error(404, "分类不存在");
            }
        } catch (RuntimeException e) {
            return ResultVO.error(400, "该分类下存在子分类，无法删除");
        }
    }

    /**
     * 更新分类
     */
    @PutMapping("/{id}")
    @Operation(summary = "更新分类", description = "更新指定ID的分类信息")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "更新成功"),
            @ApiResponse(responseCode = "404", description = "分类不存在"),
            @ApiResponse(responseCode = "400", description = "请求参数错误")
    })
    public ResultVO<Category> updateCategory(
            @Parameter(description = "分类ID", required = true) @PathVariable Integer id,
            @RequestBody Category category) {
        try {
            category.setId(id);
            Category updatedCategory = categoryService.updateCategory(category);
            return ResultVO.success("更新分类成功", updatedCategory);
        } catch (RuntimeException e) {
            return ResultVO.error(400, "请求参数错误");
        }
    }

    /**
     * 根据ID查询分类
     */
    @GetMapping("/{id}")
    @Operation(summary = "查询分类详情", description = "根据ID查询分类详细信息")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "查询成功"),
            @ApiResponse(responseCode = "404", description = "分类不存在")
    })
    public ResultVO<Category> getCategoryById(
            @Parameter(description = "分类ID", required = true) @PathVariable Integer id) {
        Category category = categoryService.getById(id);
        return category != null ? ResultVO.success("查询分类成功", category) : ResultVO.error(404, "分类不存在");
    }

    /**
     * 查询所有分类
     */
    @GetMapping
    @Operation(summary = "查询所有分类", description = "获取系统中所有分类列表，按频道、父级ID、ID排序")
    @ApiResponse(responseCode = "200", description = "查询成功")
    public ResultVO<List<Category>> getAllCategories() {
        List<Category> categories = categoryService.getAllCategories();
        return ResultVO.success("查询所有分类成功", categories);
    }

    /**
     * 根据频道查询分类
     */
    @GetMapping("/channel/{channel}")
    @Operation(summary = "根据频道查询分类", description = "根据频道类型查询分类列表（1=男频，0=女频）")
    @ApiResponse(responseCode = "200", description = "查询成功")
    public ResultVO<List<Category>> getCategoriesByChannel(
            @Parameter(description = "频道类型：1=男频，0=女频", required = true) @PathVariable Integer channel) {
        List<Category> categories = categoryService.getCategoriesByChannel(channel);
        return ResultVO.success("查询频道分类成功", categories);
    }

    /**
     * 查询顶级分类
     */
    @GetMapping("/top")
    @Operation(summary = "查询顶级分类", description = "获取所有顶级分类（父级ID为空的分类）")
    @ApiResponse(responseCode = "200", description = "查询成功")
    public ResultVO<List<Category>> getTopCategories() {
        List<Category> categories = categoryService.getTopCategories();
        return ResultVO.success("查询顶级分类成功", categories);
    }

    /**
     * 根据频道查询顶级分类
     */
    @GetMapping("/top/channel/{channel}")
    @Operation(summary = "根据频道查询顶级分类", description = "根据频道类型查询顶级分类列表")
    @ApiResponse(responseCode = "200", description = "查询成功")
    public ResultVO<List<Category>> getTopCategoriesByChannel(
            @Parameter(description = "频道类型：1=男频，0=女频", required = true) @PathVariable Integer channel) {
        List<Category> categories = categoryService.getTopCategoriesByChannel(channel);
        return ResultVO.success("查询频道顶级分类成功", categories);
    }

    /**
     * 根据父级ID查询子分类
     */
    @GetMapping("/parent/{parentId}")
    @Operation(summary = "查询子分类", description = "根据父级分类ID查询所有子分类")
    @ApiResponse(responseCode = "200", description = "查询成功")
    public ResultVO<List<Category>> getChildCategories(
            @Parameter(description = "父级分类ID", required = true) @PathVariable Integer parentId) {
        List<Category> categories = categoryService.getChildCategories(parentId);
        return ResultVO.success("查询子分类成功", categories);
    }

    /**
     * 根据频道和父级ID查询子分类
     */
    @GetMapping("/channel/{channel}/parent/{parentId}")
    @Operation(summary = "根据频道和父级ID查询子分类", description = "根据频道类型和父级分类ID查询子分类")
    @ApiResponse(responseCode = "200", description = "查询成功")
    public ResultVO<List<Category>> getChildCategoriesByChannel(
            @Parameter(description = "频道类型：1=男频，0=女频", required = true) @PathVariable Integer channel,
            @Parameter(description = "父级分类ID", required = true) @PathVariable Integer parentId) {
        List<Category> categories = categoryService.getChildCategoriesByChannel(channel, parentId);
        return ResultVO.success("查询频道子分类成功", categories);
    }

    /**
     * 根据名称模糊查询分类
     */
    @GetMapping("/search")
    @Operation(summary = "搜索分类", description = "根据分类名称进行模糊搜索")
    @ApiResponse(responseCode = "200", description = "搜索成功")
    public ResultVO<List<Category>> searchCategoriesByName(
            @Parameter(description = "分类名称关键词", required = true) @RequestParam String name) {
        List<Category> categories = categoryService.searchCategoriesByName(name);
        return ResultVO.success("搜索分类成功", categories);
    }

    /**
     * 检查分类名是否存在
     */
    @GetMapping("/exists")
    @Operation(summary = "检查分类名是否存在", description = "检查指定的分类名称是否已存在")
    @ApiResponse(responseCode = "200", description = "检查完成")
    public ResultVO<Boolean> checkCategoryExists(
            @Parameter(description = "分类名称", required = true) @RequestParam String name) {
        boolean exists = categoryService.existsByName(name);
        return ResultVO.success("检查完成", exists);
    }

    /**
     * 检查在指定频道下分类名是否存在
     */
    @GetMapping("/exists/channel/{channel}")
    @Operation(summary = "检查频道下分类名是否存在", description = "检查在指定频道下分类名称是否已存在")
    @ApiResponse(responseCode = "200", description = "检查完成")
    public ResultVO<Boolean> checkCategoryExistsByChannel(
            @Parameter(description = "频道类型：1=男频，0=女频", required = true) @PathVariable Integer channel,
            @Parameter(description = "分类名称", required = true) @RequestParam String name) {
        boolean exists = categoryService.existsByChannelAndName(channel, name);
        return ResultVO.success("检查完成", exists);
    }

    /**
     * 检查是否有子分类
     */
    @GetMapping("/{id}/has-children")
    @Operation(summary = "检查是否有子分类", description = "检查指定分类下是否存在子分类")
    @ApiResponse(responseCode = "200", description = "检查完成")
    public ResultVO<Boolean> hasChildren(
            @Parameter(description = "分类ID", required = true) @PathVariable Integer id) {
        boolean hasChildren = categoryService.hasChildren(id);
        return ResultVO.success("检查完成", hasChildren);
    }

    /**
     * 统计分类总数
     */
    @GetMapping("/count")
    @Operation(summary = "统计分类总数", description = "获取系统中分类的总数量")
    @ApiResponse(responseCode = "200", description = "统计完成")
    public ResultVO<Integer> getTotalCount() {
        int count = categoryService.getTotalCount();
        return ResultVO.success("统计完成", count);
    }
}
