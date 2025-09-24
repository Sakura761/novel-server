package com.sakura.novel.service.impl;

import com.sakura.novel.DTO.Request.BookSearchReqDto;
import com.sakura.novel.DTO.Response.BookInfoRespDto;
import com.sakura.novel.DTO.Response.PageResult;
import com.sakura.novel.mapper.BookMapper;
import com.sakura.novel.service.SearchService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class DbSearchServiceImpl implements SearchService {
    private final BookMapper bookMapper;
    @Override
    public PageResult<BookInfoRespDto> searchBooks(BookSearchReqDto bookSearchReqDto) {
        return null;
    }
}
