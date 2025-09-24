package com.sakura.novel.controller;

import com.sakura.novel.core.common.vo.ResultVO;
import com.sakura.novel.entity.Author;
import com.sakura.novel.DTO.Response.PageResult;
import com.sakura.novel.service.AuthorService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 作者控制器
 */
@RestController
@RequestMapping("/api/authors")
@RequiredArgsConstructor
@Tag(name = "作者管理", description = "作者信息管理相关API")
public class AuthorController {

    private final AuthorService authorService;

    // ===== 基础 CRUD 操作 =====

    /**
     * 创建作者
     */
    @PostMapping
    @Operation(summary = "创建新作者", description = "创建一个新的作者")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "创建成功"),
            @ApiResponse(responseCode = "400", description = "请求参数错误或作者名称已存在")
    })
    public ResultVO<Author> createAuthor(@RequestBody Author author) {
        try {
            Author createdAuthor = authorService.createAuthor(author);
            return ResultVO.success("创建作者成功", createdAuthor);
        } catch (RuntimeException e) {
            return ResultVO.error(400, "请求参数错误或作者名称已存在");
        }
    }

    /**
     * 根据ID删除作者
     */
    @DeleteMapping("/{id}")
    @Operation(summary = "删除作者", description = "根据ID删除指定作者")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "删除成功"),
            @ApiResponse(responseCode = "404", description = "作者不存在"),
            @ApiResponse(responseCode = "400", description = "删除失败")
    })
    public ResultVO<Void> deleteAuthor(
            @Parameter(description = "作者ID", required = true) @PathVariable Integer id) {
        try {
            boolean deleted = authorService.deleteById(id);
            if (deleted) {
                return ResultVO.success("删除作者成功", null);
            } else {
                return ResultVO.error(404, "作者不存在");
            }
        } catch (RuntimeException e) {
            return ResultVO.error(400, "删除失败");
        }
    }

    /**
     * 更新作者信息
     */
    @PutMapping("/{id}")
    @Operation(summary = "更新作者", description = "更新指定ID的作者信息")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "更新成功"),
            @ApiResponse(responseCode = "404", description = "作者不存在"),
            @ApiResponse(responseCode = "400", description = "请求参数错误")
    })
    public ResultVO<Author> updateAuthor(
            @Parameter(description = "作者ID", required = true) @PathVariable Integer id,
            @RequestBody Author author) {
        try {
            author.setId(id);
            Author updatedAuthor = authorService.updateAuthor(author);
            return ResultVO.success("更新作者成功", updatedAuthor);
        } catch (RuntimeException e) {
            return ResultVO.error(400, "请求参数错误");
        }
    }

    /**
     * 根据ID查询作者详情
     */
    @GetMapping("/{id}")
    @Operation(summary = "查询作者详情", description = "根据ID查询作者详细信息")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "查询成功"),
            @ApiResponse(responseCode = "404", description = "作者不存在")
    })
    public ResultVO<Author> getAuthorById(
            @Parameter(description = "作者ID", required = true) @PathVariable Integer id) {
        Author author = authorService.getById(id);
        return author != null ? ResultVO.success("查询作者成功", author) : ResultVO.error(404, "作者不存在");
    }

    // ===== 核心查询接口 =====

    /**
     * 根据用户ID查询作者信息
     */
    @GetMapping("/user/{userId}")
    @Operation(summary = "根据用户ID查询作者", description = "根据用户ID查询关联的作者信息")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "查询成功"),
            @ApiResponse(responseCode = "404", description = "该用户不是作者")
    })
    public ResultVO<Author> getAuthorByUserId(
            @Parameter(description = "用户ID", required = true) @PathVariable Integer userId) {
        Author author = authorService.getByUserId(userId);
        return author != null ? ResultVO.success("查询作者成功", author) : ResultVO.error(404, "该用户不是作者");
    }

    /**
     * 分页查询作者列表（支持多条件搜索）
     */
    @GetMapping("/search")
    @Operation(summary = "分页查询作者列表", description = "支持多条件搜索的分页查询作者列表")
    @ApiResponse(responseCode = "200", description = "查询成功")
    public ResultVO<PageResult<Author>> searchAuthors(
            @Parameter(description = "作者名称（支持模糊搜索）") @RequestParam(required = false) String name,
            @Parameter(description = "最小等级") @RequestParam(required = false) Short minLevel,
            @Parameter(description = "最大等级") @RequestParam(required = false) Short maxLevel,
            @Parameter(description = "最小作品数") @RequestParam(required = false) Integer minTotalBooks,
            @Parameter(description = "最大作品数") @RequestParam(required = false) Integer maxTotalBooks,
            @Parameter(description = "最小字数") @RequestParam(required = false) Long minWordCount,
            @Parameter(description = "最大字数") @RequestParam(required = false) Long maxWordCount,
            @Parameter(description = "页码，从1开始") @RequestParam(defaultValue = "1") Integer pageNum,
            @Parameter(description = "每页数量") @RequestParam(defaultValue = "10") Integer pageSize) {
        PageResult<Author> pageResult = authorService.getAuthorsWithConditions(
                name, minLevel, maxLevel, minTotalBooks, maxTotalBooks,
                minWordCount, maxWordCount, pageNum, pageSize);
        return ResultVO.success("搜索作者成功", pageResult);
    }

    /**
     * 根据名称搜索作者
     */
    @GetMapping("/search/name")
    @Operation(summary = "根据名称搜索作者", description = "根据作者名称进行模糊搜索")
    @ApiResponse(responseCode = "200", description = "搜索成功")
    public ResultVO<List<Author>> searchAuthorsByName(
            @Parameter(description = "作者名称关键词", required = true) @RequestParam String name) {
        List<Author> authors = authorService.searchByName(name);
        return ResultVO.success("搜索作者成功", authors);
    }

    /**
     * 查询所有作者
     */
    @GetMapping("/all")
    @Operation(summary = "查询所有作者", description = "查询所有作者信息，不分页")
    @ApiResponse(responseCode = "200", description = "查询成功")
    public ResultVO<List<Author>> getAllAuthors() {
        List<Author> authors = authorService.getAllAuthors();
        return ResultVO.success("查询所有作者成功", authors);
    }

    // ===== 统计和排行接口 =====

    /**
     * 获取作者总数
     */
    @GetMapping("/count")
    @Operation(summary = "获取作者总数", description = "获取系统中的作者总数")
    @ApiResponse(responseCode = "200", description = "查询成功")
    public ResultVO<Integer> getAuthorCount() {
        int count = authorService.getTotalCount();
        return ResultVO.success("获取作者总数成功", count);
    }

    /**
     * 根据等级统计作者数量
     */
    @GetMapping("/count/level/{level}")
    @Operation(summary = "根据等级统计作者数量", description = "统计指定等级的作者数量")
    @ApiResponse(responseCode = "200", description = "统计成功")
    public ResultVO<Integer> getAuthorCountByLevel(
            @Parameter(description = "作者等级", required = true) @PathVariable Short level) {
        int count = authorService.getCountByLevel(level);
        return ResultVO.success("统计成功", count);
    }

    /**
     * 获取热门作者排行榜
     */
    @GetMapping("/popular")
    @Operation(summary = "获取热门作者排行榜", description = "按粉丝数排序获取热门作者列表")
    @ApiResponse(responseCode = "200", description = "查询成功")
    public ResultVO<List<Author>> getPopularAuthors(
            @Parameter(description = "返回数量限制") @RequestParam(defaultValue = "10") Integer limit) {
        List<Author> authors = authorService.getPopularAuthors(limit);
        return ResultVO.success("获取热门作者成功", authors);
    }

    /**
     * 获取高产作者排行榜
     */
    @GetMapping("/productive")
    @Operation(summary = "获取高产作者排行榜", description = "按作品数排序获取高产作者列表")
    @ApiResponse(responseCode = "200", description = "查询成功")
    public ResultVO<List<Author>> getProductiveAuthors(
            @Parameter(description = "返回数量限制") @RequestParam(defaultValue = "10") Integer limit) {
        List<Author> authors = authorService.getProductiveAuthors(limit);
        return ResultVO.success("获取高产作者成功", authors);
    }

    // ===== 作者统计和管理接口 =====

    /**
     * 获取作者详细统计信息
     */
    @GetMapping("/{id}/stats")
    @Operation(summary = "获取作者统计信息", description = "获取作者的详细统计信息，包括作品数、字数、粉丝数等")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "查询成功"),
            @ApiResponse(responseCode = "404", description = "作者不存在")
    })
    public ResultVO<AuthorService.AuthorStatsInfo> getAuthorStats(
            @Parameter(description = "作者ID", required = true) @PathVariable Integer id) {
        try {
            AuthorService.AuthorStatsInfo statsInfo = authorService.getAuthorStatsInfo(id);
            return ResultVO.success("获取作者统计信息成功", statsInfo);
        } catch (RuntimeException e) {
            return ResultVO.error(404, "作者不存在");
        }
    }

    /**
     * 关注作者
     */
    @PostMapping("/{id}/follow")
    @Operation(summary = "关注作者", description = "关注指定作者，增加其粉丝数")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "关注成功"),
            @ApiResponse(responseCode = "404", description = "作者不存在"),
            @ApiResponse(responseCode = "400", description = "关注失败")
    })
    public ResultVO<Void> followAuthor(
            @Parameter(description = "作者ID", required = true) @PathVariable Integer id) {
        try {
            boolean followed = authorService.followAuthor(id);
            if (followed) {
                return ResultVO.success("关注作者成功", null);
            } else {
                return ResultVO.error(400, "关注失败");
            }
        } catch (RuntimeException e) {
            return ResultVO.error(404, "作者不存在");
        }
    }

    /**
     * 取消关注作者
     */
    @DeleteMapping("/{id}/follow")
    @Operation(summary = "取消关注作者", description = "取消关注指定作者，减少其粉丝数")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "取消关注成功"),
            @ApiResponse(responseCode = "404", description = "作者不存在"),
            @ApiResponse(responseCode = "400", description = "操作失败")
    })
    public ResultVO<Void> unfollowAuthor(
            @Parameter(description = "作者ID", required = true) @PathVariable Integer id) {
        try {
            boolean unfollowed = authorService.unfollowAuthor(id);
            if (unfollowed) {
                return ResultVO.success("取消关注成功", null);
            } else {
                return ResultVO.error(400, "操作失败");
            }
        } catch (RuntimeException e) {
            return ResultVO.error(404, "作者不存在");
        }
    }

    /**
     * 升级作者等级
     */
    @PutMapping("/{id}/level")
    @Operation(summary = "升级作者等级", description = "更新作者等级")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "升级成功"),
            @ApiResponse(responseCode = "404", description = "作者不存在"),
            @ApiResponse(responseCode = "400", description = "升级失败")
    })
    public ResultVO<Void> upgradeAuthorLevel(
            @Parameter(description = "作者ID", required = true) @PathVariable Integer id,
            @Parameter(description = "新等级", required = true) @RequestParam Short newLevel) {
        try {
            boolean upgraded = authorService.upgradeAuthorLevel(id, newLevel);
            if (upgraded) {
                return ResultVO.success("升级作者等级成功", null);
            } else {
                return ResultVO.error(400, "升级失败");
            }
        } catch (RuntimeException e) {
            return ResultVO.error(404, "作者不存在");
        }
    }

    /**
     * 更新作者统计信息
     */
    @PutMapping("/{id}/stats")
    @Operation(summary = "更新作者统计信息", description = "更新作者的字数和作品数统计")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "更新成功"),
            @ApiResponse(responseCode = "404", description = "作者不存在"),
            @ApiResponse(responseCode = "400", description = "更新失败")
    })
    public ResultVO<Void> updateAuthorStats(
            @Parameter(description = "作者ID", required = true) @PathVariable Integer id,
            @Parameter(description = "总字数", required = true) @RequestParam Long totalWordCount,
            @Parameter(description = "作品总数", required = true) @RequestParam Integer totalBooks) {
        try {
            boolean updated = authorService.updateAuthorStats(id, totalWordCount, totalBooks);
            if (updated) {
                return ResultVO.success("更新作者统计信息成功", null);
            } else {
                return ResultVO.error(400, "更新失败");
            }
        } catch (RuntimeException e) {
            return ResultVO.error(404, "作者不存在");
        }
    }

    /**
     * 批量创建作者
     */
    @PostMapping("/batch")
    @Operation(summary = "批量创建作者", description = "批量创建多个作者")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "批量创建成功"),
            @ApiResponse(responseCode = "400", description = "批量创建失败")
    })
    public ResultVO<Void> batchCreateAuthors(@RequestBody List<Author> authors) {
        try {
            boolean created = authorService.batchCreateAuthors(authors);
            if (created) {
                return ResultVO.success("批量创建作者成功", null);
            } else {
                return ResultVO.error(400, "批量创建失败");
            }
        } catch (RuntimeException e) {
            return ResultVO.error(400, "批量创建失败");
        }
    }

    /**
     * 检查作者名称是否存在
     */
    @GetMapping("/exists/name")
    @Operation(summary = "检查作者名称是否存在", description = "检查指定的作者名称是否已存在")
    @ApiResponse(responseCode = "200", description = "检查完成")
    public ResultVO<Boolean> checkAuthorNameExists(
            @Parameter(description = "作者名称", required = true) @RequestParam String name) {
        boolean exists = authorService.existsByName(name);
        return ResultVO.success("检查完成", exists);
    }

    /**
     * 检查用户是否已是作者
     */
    @GetMapping("/exists/user/{userId}")
    @Operation(summary = "检查用户是否已是作者", description = "检查指定用户是否已注册为作者")
    @ApiResponse(responseCode = "200", description = "检查完成")
    public ResultVO<Boolean> checkUserIsAuthor(
            @Parameter(description = "用户ID", required = true) @PathVariable Integer userId) {
        boolean exists = authorService.existsByUserId(userId);
        return ResultVO.success("检查完成", exists);
    }
}
