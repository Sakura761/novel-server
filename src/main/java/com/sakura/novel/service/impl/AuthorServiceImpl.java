package com.sakura.novel.service.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.sakura.novel.entity.Author;
import com.sakura.novel.DTO.Response.PageResult;
import com.sakura.novel.mapper.AuthorMapper;
import com.sakura.novel.service.AuthorService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 作者服务实现类
 */
@Service
@RequiredArgsConstructor
public class AuthorServiceImpl implements AuthorService {

    private final AuthorMapper authorMapper;

    @Override
    public Author createAuthor(Author author) {
        // 设置创建时间
        LocalDateTime now = LocalDateTime.now();
        author.setCreateTime(now);

        // 检查作者名称是否已存在
        if (existsByName(author.getName())) {
            throw new RuntimeException("作者名称已存在");
        }

        // 检查用户是否已是作者
        if (author.getUserId() != null && existsByUserId(author.getUserId())) {
            throw new RuntimeException("该用户已是作者");
        }

        // 设置默认值
        if (author.getLevel() == null) {
            author.setLevel((short) 1);
        }
        if (author.getTotalWordCount() == null) {
            author.setTotalWordCount(0L);
        }
        if (author.getTotalBooks() == null) {
            author.setTotalBooks(0);
        }
        if (author.getFollowerCount() == null) {
            author.setFollowerCount(0);
        }

        authorMapper.insert(author);
        return author;
    }

    @Override
    public boolean deleteById(Integer id) {
        Author author = getById(id);
        if (author == null) {
            throw new RuntimeException("作者不存在");
        }
        return authorMapper.deleteById(id) > 0;
    }

    @Override
    public Author updateAuthor(Author author) {
        Author existingAuthor = getById(author.getId());
        if (existingAuthor == null) {
            throw new RuntimeException("作者不存在");
        }

        // 如果更新了作者名称，需要检查新名称是否与其他作者冲突
        if (!existingAuthor.getName().equals(author.getName())) {
            if (existsByName(author.getName())) {
                throw new RuntimeException("作者名称已存在");
            }
        }

        // 如果更新了用户ID，需要检查新用户ID是否与其他作者冲突
        if (author.getUserId() != null && !author.getUserId().equals(existingAuthor.getUserId())) {
            if (existsByUserId(author.getUserId())) {
                throw new RuntimeException("该用户已是作者");
            }
        }

        int updated = authorMapper.updateById(author);
        if (updated == 0) {
            throw new RuntimeException("更新作者失败");
        }
        return getById(author.getId());
    }

    @Override
    public Author getById(Integer id) {
        return authorMapper.selectById(id);
    }

    @Override
    public Author getByUserId(Integer userId) {
        return authorMapper.selectByUserId(userId);
    }

    @Override
    public List<Author> searchByName(String name) {
        return authorMapper.selectByNameLike(name);
    }

    @Override
    public PageResult<Author> getAuthorsWithConditions(String name, Short minLevel, Short maxLevel,
                                                     Integer minTotalBooks, Integer maxTotalBooks,
                                                     Long minWordCount, Long maxWordCount,
                                                     Integer pageNum, Integer pageSize) {
        PageHelper.startPage(pageNum, pageSize);
        List<Author> authors = authorMapper.selectAuthorsWithConditions(name, minLevel, maxLevel,
                minTotalBooks, maxTotalBooks, minWordCount, maxWordCount);
        PageInfo<Author> pageInfo = new PageInfo<>(authors);

        PageResult<Author> pageResult = new PageResult<>();
        pageResult.setList(authors);
        pageResult.setTotal(pageInfo.getTotal());
        pageResult.setPageNum(pageInfo.getPageNum());
        pageResult.setPageSize(pageInfo.getPageSize());
        pageResult.setPages(pageInfo.getPages());

        return pageResult;
    }

    @Override
    public List<Author> getAllAuthors() {
        return authorMapper.selectAll();
    }

    @Override
    public boolean batchCreateAuthors(List<Author> authors) {
        if (authors == null || authors.isEmpty()) {
            return false;
        }

        // 设置创建时间和默认值
        LocalDateTime now = LocalDateTime.now();
        for (Author author : authors) {
            author.setCreateTime(now);
            if (author.getLevel() == null) {
                author.setLevel((short) 1);
            }
            if (author.getTotalWordCount() == null) {
                author.setTotalWordCount(0L);
            }
            if (author.getTotalBooks() == null) {
                author.setTotalBooks(0);
            }
            if (author.getFollowerCount() == null) {
                author.setFollowerCount(0);
            }
        }

        return authorMapper.batchInsert(authors) > 0;
    }

    @Override
    public boolean existsByName(String name) {
        return authorMapper.existsByName(name);
    }

    @Override
    public boolean existsByUserId(Integer userId) {
        return authorMapper.existsByUserId(userId);
    }

    @Override
    public int getTotalCount() {
        return authorMapper.count();
    }

    @Override
    public int getCountByLevel(Short level) {
        return authorMapper.countByLevel(level);
    }

    @Override
    public List<Author> getPopularAuthors(Integer limit) {
        return authorMapper.selectPopularAuthors(limit);
    }

    @Override
    public List<Author> getProductiveAuthors(Integer limit) {
        return authorMapper.selectProductiveAuthors(limit);
    }

    @Override
    public boolean updateAuthorStats(Integer id, Long totalWordCount, Integer totalBooks) {
        Author author = getById(id);
        if (author == null) {
            throw new RuntimeException("作者不存在");
        }
        return authorMapper.updateAuthorStats(id, totalWordCount, totalBooks) > 0;
    }

    @Override
    public boolean followAuthor(Integer authorId) {
        Author author = getById(authorId);
        if (author == null) {
            throw new RuntimeException("作者不存在");
        }
        return authorMapper.increaseFollowerCount(authorId) > 0;
    }

    @Override
    public boolean unfollowAuthor(Integer authorId) {
        Author author = getById(authorId);
        if (author == null) {
            throw new RuntimeException("作者不存在");
        }
        return authorMapper.decreaseFollowerCount(authorId) > 0;
    }

    @Override
    public boolean updateFollowerCount(Integer id, Integer followerCount) {
        Author author = getById(id);
        if (author == null) {
            throw new RuntimeException("作者不存在");
        }
        return authorMapper.updateFollowerCount(id, followerCount) > 0;
    }

    @Override
    public boolean upgradeAuthorLevel(Integer id, Short newLevel) {
        Author author = getById(id);
        if (author == null) {
            throw new RuntimeException("作者不存在");
        }

        // 创建更新对象，只更新等级
        Author updateAuthor = new Author();
        updateAuthor.setId(id);
        updateAuthor.setLevel(newLevel);
        updateAuthor.setName(author.getName()); // 保持必要字段

        return authorMapper.updateById(updateAuthor) > 0;
    }

    @Override
    public AuthorStatsInfo getAuthorStatsInfo(Integer id) {
        Author author = getById(id);
        if (author == null) {
            throw new RuntimeException("作者不存在");
        }

        AuthorStatsInfo statsInfo = new AuthorStatsInfo();
        statsInfo.setAuthor(author);
        statsInfo.setBookCount(author.getTotalBooks());
        statsInfo.setTotalWordCount(author.getTotalWordCount());
        statsInfo.setFollowerCount(author.getFollowerCount());

        // 这里可以扩展，从其他服务获取平均评分和总收藏数等信息
        // statsInfo.setAverageRating(bookService.getAverageRatingByAuthor(id));
        // statsInfo.setTotalFavorites(bookService.getTotalFavoritesByAuthor(id));

        return statsInfo;
    }
}
