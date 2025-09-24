package com.sakura.novel.controller;

import com.sakura.novel.DTO.Request.BookSearchReqDto;
import com.sakura.novel.core.common.vo.ResultVO;
import com.sakura.novel.service.impl.EsSearchServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

@RestController
@RequestMapping("api/es")
@RequiredArgsConstructor
public class EsController {

    private final EsSearchServiceImpl searchService;

    @GetMapping("/insertAll")
    public ResultVO<?> insertAll() throws IOException {
        searchService.syncAllBooksToEs();
        return ResultVO.success();
    }
    @PostMapping("/search")
    public ResultVO<?> searchBooks(@RequestBody BookSearchReqDto bookSearchReqDto) {
        return ResultVO.success(searchService.searchBooks(bookSearchReqDto));
    }
}
