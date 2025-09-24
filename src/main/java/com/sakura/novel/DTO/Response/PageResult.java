package com.sakura.novel.DTO.Response;

import com.github.pagehelper.PageInfo;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;

/**
 * 分页结果封装类
 */
@Data
@Schema(description = "分页结果")
public class PageResult<T> {

    @Schema(description = "当前页码", example = "1")
    private Integer pageNum;

    @Schema(description = "每页数量", example = "10")
    private Integer pageSize;

    @Schema(description = "总记录数", example = "100")
    private Long total;

    @Schema(description = "总页数", example = "10")
    private Integer pages;

    @Schema(description = "数据列表")
    private List<T> list;

    @Schema(description = "是否为第一页", example = "true")
    private Boolean isFirstPage;

    @Schema(description = "是否为最后一页", example = "false")
    private Boolean isLastPage;

    @Schema(description = "是否有前一页", example = "false")
    private Boolean hasPreviousPage;

    @Schema(description = "是否有下一页", example = "true")
    private Boolean hasNextPage;

    @Schema(description = "导航页码数组")
    private int[] navigatepageNums;

    public PageResult() {}
    public PageResult(PageInfo<T> pageInfo) {
        this.pageNum = pageInfo.getPageNum();
        this.pageSize = pageInfo.getPageSize();
        this.total = pageInfo.getTotal();
        this.pages = pageInfo.getPages();
        this.list = pageInfo.getList();
        this.hasPreviousPage = pageInfo.isHasPreviousPage();
        this.hasNextPage = pageInfo.isHasNextPage();
        this.isFirstPage = pageInfo.isIsFirstPage();
        this.isLastPage = pageInfo.isIsLastPage();
    }
    public PageResult(List<T> list) {
        this.list = list;
    }

    public PageResult(Integer pageNum, Integer pageSize, Long total, List<T> list) {
        this.pageNum = pageNum;
        this.pageSize = pageSize;
        this.total = total;
        this.list = list;
        this.pages = (int) ((total + pageSize - 1) / pageSize);
        this.isFirstPage = pageNum == 1;
        this.isLastPage = pageNum.equals(pages);
        this.hasPreviousPage = pageNum > 1;
        this.hasNextPage = pageNum < pages;
    }
}
