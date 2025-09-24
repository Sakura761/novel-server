package com.sakura.novel.service;

import com.sakura.novel.DTO.Request.BookSearchReqDto;
import com.sakura.novel.DTO.Response.BookInfoRespDto;
import com.sakura.novel.DTO.Response.PageResult;

public interface SearchService {
    PageResult<BookInfoRespDto> searchBooks(BookSearchReqDto bookSearchReqDto);
}
