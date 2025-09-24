package com.sakura.novel.service.impl;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch._types.SortOrder;
import co.elastic.clients.elasticsearch.core.*;
import co.elastic.clients.elasticsearch.core.search.Hit;
import co.elastic.clients.elasticsearch.core.search.TotalHits;
import com.sakura.novel.DTO.Request.BookSearchReqDto;
import com.sakura.novel.DTO.Response.BookInfoRespDto;
import com.sakura.novel.DTO.Response.PageResult;
import com.sakura.novel.DTO.es.BookDocument;
import com.sakura.novel.core.constant.EsConsts;
import com.sakura.novel.mapper.BookMapper;
import com.sakura.novel.service.SearchService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.io.IOException;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@Service
@RequiredArgsConstructor
@Slf4j
public class EsSearchServiceImpl implements SearchService {
    private final ElasticsearchClient client;
    private final BookMapper bookMapper;
    @Override
    public PageResult<BookInfoRespDto> searchBooks(BookSearchReqDto bookSearchReqDto) {
        try {
            // 1. 构建搜索请求
            SearchRequest searchRequest = SearchRequest.of(s -> {
                s.index(EsConsts.BookIndex.INDEX_NAME);

                // 1.1 构建 bool 查询条件 (由 buildSearchCondition 方法负责)
                buildSearchCondition(bookSearchReqDto, s);

                // 1.2 【已修正】构建排序条件
                String sortParam = bookSearchReqDto.getSort();
                if (StringUtils.hasText(sortParam)) {
                    // 如果前端传递了排序字段
                    String[] sortParts = sortParam.split(" ");
                    String field = sortParts[0];
                    // 默认降序，只有明确指定 asc 时才升序
                    SortOrder order = (sortParts.length > 1 && "asc".equalsIgnoreCase(sortParts[1]))
                            ? SortOrder.Asc : SortOrder.Desc;
                    s.sort(so -> so.field(f -> f.field(field).order(order)));
                } else {
                    // 如果没有传递排序字段，设置一个合理的默认排序
                    if (StringUtils.hasText(bookSearchReqDto.getKeyword())) {
                        // 对于关键词搜索，默认按相关性得分(_score)排序
                        s.sort(so -> so.score(sc -> sc.order(SortOrder.Desc)));
                    } else {
                        // 对于纯筛选，默认按最新章节更新时间排序
                        s.sort(so -> so.field(f -> f
                                .field(EsConsts.BookIndex.FIELD_LAST_CHAPTER_UPDATE_TIME)
                                .order(SortOrder.Desc)
                        ));
                    }
                }

                // 1.3 构建分页条件
                s.from((bookSearchReqDto.getPageNum() - 1) * bookSearchReqDto.getPageSize())
                        .size(bookSearchReqDto.getPageSize());

                // 1.4 构建高亮 (仅当有关键词时才有意义)
                if (StringUtils.hasText(bookSearchReqDto.getKeyword())) {
                    s.highlight(h -> h
                            .fields(EsConsts.BookIndex.FIELD_BOOK_NAME, hf -> hf
                                    .preTags("<em style='color:red'>")
                                    .postTags("</em>")
                            )
                            .fields(EsConsts.BookIndex.FIELD_AUTHOR_NAME, hf -> hf
                                    .preTags("<em style='color:red'>")
                                    .postTags("</em>")
                            )
                    );
                }
                return s;
            });

            // 2. 执行搜索
            SearchResponse<BookDocument> response = client.search(searchRequest, BookDocument.class);

            // 3. 解析并处理结果
            List<BookInfoRespDto> list = new ArrayList<>();
            List<Hit<BookDocument>> hits = response.hits().hits();

            for (Hit<BookDocument> hit : hits) {
                BookDocument book = hit.source();
                if (book == null) {
                    continue; // 安全起见，虽然很少发生
                }

                // 3.1 【已修正】安全地处理高亮
                Map<String, List<String>> highlightFields = hit.highlight();
                List<String> bookNameHighlights = highlightFields.get(EsConsts.BookIndex.FIELD_BOOK_NAME);
                if (bookNameHighlights != null && !bookNameHighlights.isEmpty()) {
                    book.setTitle(bookNameHighlights.getFirst());
                }

                List<String> authorNameHighlights = highlightFields.get(EsConsts.BookIndex.FIELD_AUTHOR_NAME);
                if (authorNameHighlights != null && !authorNameHighlights.isEmpty()) {
                    book.setAuthorName(authorNameHighlights.getFirst());
                }

                // 3.2 【已修正】安全地进行 Date 到 LocalDateTime 的转换
                LocalDateTime updateTime = null;
                if (book.getLatestChapterUpdateTime() != null) {
                    updateTime = LocalDateTime.ofInstant(
                            book.getLatestChapterUpdateTime().toInstant(),
                            ZoneId.systemDefault()
                    );
                }

                // 3.3 【已完善】将 BookDocument 转换为最终的 BookInfoRespDto
                list.add(BookInfoRespDto.builder()
                        .id(Long.valueOf(book.getId())) // Integer to Long
                        .bookName(book.getTitle())
                        .categoryId(book.getCategoryId())
                        .categoryName(book.getCategoryName())
                        .coverImageUrl(book.getCoverImageUrl())
                        .authorId(book.getAuthorId())
                        .authorName(book.getAuthorName())
                        .bookDesc(book.getDescription())
                        .bookStatus(book.getStatus())
                        .visitCount(book.getViewCount())
                        .wordCount(book.getWordCount())
                        .commentCount(null) // BookDocument 中没有 commentCount 字段，暂设为null
                        .lastChapterId(book.getLatestChapterId() != null ? Long.valueOf(book.getLatestChapterId()) : null)
                        .lastChapterName(book.getLatestChapterTitle())
                        .lastChapterUpdateTime(updateTime) // 使用转换后的时间
                        .build());
            }

            // 4. 【已完善】构建并返回完整的分页结果对象
            TotalHits total = response.hits().total();
            long totalCount = total != null ? total.value() : 0;

            return new PageResult<>(
                    bookSearchReqDto.getPageNum(),
                    bookSearchReqDto.getPageSize(),
                    totalCount,
                    list
            );

        } catch (IOException e) {
            log.error("Elasticsearch search failed with request: {}", bookSearchReqDto, e);
            // 封装成业务异常或直接抛出运行时异常
            throw new RuntimeException("搜索服务异常，请稍后重试", e);
        }
    }

    /**
     * 构建检索条件的最终版本
     * 严格遵守用户指定的 .number() 和 .date() 语法，并彻底解决“无关键词则无结果”的问题
     *
     * @param bookSearchReqDto 前端传入的搜索/筛选条件
     * @param searchBuilder ES搜索请求的构建器
     */
    private void buildSearchCondition(BookSearchReqDto bookSearchReqDto,
                                      SearchRequest.Builder searchBuilder) {

        searchBuilder.query(q -> q.bool(b -> {

            // ======================================================================
            // Part 1: Query Context (全文搜索) - 只有在提供 keyword 时才生效
            // ======================================================================
            if (StringUtils.hasText(bookSearchReqDto.getKeyword())) {
                String keyword = bookSearchReqDto.getKeyword();

                b.must(mustQuery -> mustQuery
                        .bool(shouldBool -> shouldBool
                                .should(s -> s.match(m -> m.field(EsConsts.BookIndex.FIELD_BOOK_NAME).query(keyword).boost(10.0f)))
                                .should(s -> s.match(m -> m.field(EsConsts.BookIndex.FIELD_AUTHOR_NAME).query(keyword).boost(5.0f)))
                                .should(s -> s.match(m -> m.field(EsConsts.BookIndex.FIELD_BOOK_DESC).query(keyword).boost(0.1f)))
                                .minimumShouldMatch("1")
                        )
                );
            }

            // ======================================================================
            // Part 2: Filter Context (精确过滤) - 独立于关键词，不影响评分
            // 这里的每一个if都是独立的，可以自由组合
            // ======================================================================

            // 2.1 按作品方向筛选
            if (Objects.nonNull(bookSearchReqDto.getChannel())) {
                b.filter(f -> f.term(t -> t
                        .field(EsConsts.BookIndex.FIELD_WORK_DIRECTION)
                        .value(bookSearchReqDto.getChannel())
                ));
            }

            // 2.2 按分类ID筛选
            if (Objects.nonNull(bookSearchReqDto.getCategoryId())) {
                b.filter(f -> f.term(t -> t
                        .field(EsConsts.BookIndex.FIELD_CATEGORY_ID)
                        .value(bookSearchReqDto.getCategoryId())
                ));
            }

            // 2.3 范围过滤：最小字数（>=）- 【严格遵守 .number() 语法】
            if (Objects.nonNull(bookSearchReqDto.getWordCountMin())) {
                b.filter(f -> f.range(r -> r
                        .number(n -> n
                                .field(EsConsts.BookIndex.FIELD_WORD_COUNT)
                                .gte(bookSearchReqDto.getWordCountMin().doubleValue())
                        )
                ));
            }

            // 2.4 范围过滤：最大字数 (<) - 【严格遵守 .number() 语法】
            if (Objects.nonNull(bookSearchReqDto.getWordCountMax())) {
                b.filter(f -> f.range(r -> r
                        .number(n -> n
                                .field(EsConsts.BookIndex.FIELD_WORD_COUNT)
                                .lt(bookSearchReqDto.getWordCountMax().doubleValue())
                        )
                ));
            }

            // 2.5 范围过滤：更新时间 (>=) - 【严格遵守 .date() 语法】
            if (Objects.nonNull(bookSearchReqDto.getUpdateTimeMin())) {
                String isoDateTime = Instant.ofEpochMilli(bookSearchReqDto.getUpdateTimeMin().getTime()).toString();
                b.filter(f -> f.range(r -> r
                        .date(d -> d
                                .field(EsConsts.BookIndex.FIELD_LAST_CHAPTER_UPDATE_TIME)
                                .gte(isoDateTime)
                        )
                ));
            }

            return b; // 返回构建好的 BoolQuery.Builder
        }));
    }
    public void syncAllBooksToEs() throws IOException {
        List<BookDocument> books = bookMapper.selectAllBooks();

        // 2. 创建 BulkRequest.Builder
        BulkRequest.Builder bulkRequestBuilder = new BulkRequest.Builder();

        // 3. 遍历书籍列表，为每本书创建一个索引操作
        for (BookDocument book : books) {
            // 使用 .index() 操作来构建 BulkOperation
            // 客户端会自动使用 Jackson 将 book 对象序列化为 JSON
            bulkRequestBuilder.operations(op -> op
                    .index(idx -> idx
                            .index("books") // 设置索引名称
                            .id(String.valueOf(book.getId())) // 设置文档ID
                            .document(book) // 直接传递对象，客户端负责序列化
                    )
            );
        }

        // 4. 执行批量请求
        BulkResponse result = client.bulk(bulkRequestBuilder.build());

        // 5. （可选）检查批量操作的结果
        if (result.errors()) {
            log.error("Bulk operation had errors.");
            result.items().forEach(item -> {
                if (item.error() != null) {

                    log.error("Error for document ID {}: {}", item.id(), item.error().reason());
                }
            });
            // 可以选择抛出异常来中断流程
            // throw new IOException("Error occurred during Elasticsearch bulk operation.");
        } else {
            log.info("Successfully synced {} books to Elasticsearch.", books.size());
        }
    }
}
