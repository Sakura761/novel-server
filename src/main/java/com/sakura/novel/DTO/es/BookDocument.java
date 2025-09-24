package com.sakura.novel.DTO.es;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.*;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;

@Data
@Document(indexName = "novel_books")
public class BookDocument {

    @Id
    private Integer id;

    @Field(type = FieldType.Text, analyzer = "ik_max_word", searchAnalyzer = "ik_max_word")
    private String title;

    @Field(type = FieldType.Text, analyzer = "ik_max_word")
    private String description;

    @Field(type = FieldType.Text)
    private String coverImageUrl;

    @Field(type = FieldType.Integer)
    private Integer channel; // 1 男频 / 0 女频

    @Field(type = FieldType.Keyword)
    private String authorName;

    @Field(type = FieldType.Integer)
    private Integer authorId;

    @Field(type = FieldType.Integer)
    private Integer categoryId;

    @Field(type = FieldType.Keyword)
    private String categoryName;

    @Field(type = FieldType.Integer)
    private Integer status; // 0 完结 / 1 连载

    @Field(type = FieldType.Integer)
    private Integer isVip;

    @Field(type = FieldType.Integer)
    private Integer wordCount;

    /** 最新章节 ID */
    @Field(type = FieldType.Integer)
    private Integer latestChapterId;

    /** 最新章节标题 */
    @Field(type = FieldType.Text, analyzer = "ik_max_word")
    private String latestChapterTitle;

    /** 最新章节更新时间 */
//    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss", timezone = "Asia/Shanghai")
    @Field(type =  FieldType.Date)
    private Date latestChapterUpdateTime;

    @Field(type = FieldType.Integer)
    private Long viewCount;

    @Field(type = FieldType.Integer)
    private Long recommendCount;

    @Field(type = FieldType.Integer)
    private Long monthlyTickets;

    @Field(type = FieldType.Integer)
    private Long collectionCount;

    @Field(type = FieldType.Float)
    private Float ratingAverage;

}

