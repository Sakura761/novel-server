package com.sakura.novel.entity;

import lombok.Data;
import lombok.NoArgsConstructor;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@Data
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true) // 忽略ES返回但此类中未定义的字段
public class BookDocument {

    private Integer id;
    private String title;
    private String authorName;
    private String description;
    private String coverImageUrl;
    private Integer wordCount;
    private Integer status;
}