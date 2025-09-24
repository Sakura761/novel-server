package com.sakura.novel.entity;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 书籍分类表实体类
 * 存储书籍的分类信息，区分不同频道
 */
@Data
@Schema(description = "书籍分类信息")
public class Category {
    
    @Schema(description = "分类ID", example = "1")
    private Integer id;
    
    @Schema(description = "分类名称", example = "都市", required = true)
    private String name;
    
    @Schema(description = "父级分类ID", example = "null")
    private Integer parentId;
    
    @Schema(description = "频道类型：1=男频，0=女频", example = "1", required = true)
    private Integer channel; // 频道: 1=男频, 0=女频
    
    @Schema(description = "创建时间", example = "2023-12-01T10:00:00")
    private LocalDateTime createTime;
    
    // 频道常量
    public static final int CHANNEL_MALE = 1;   // 男频
    public static final int CHANNEL_FEMALE = 0; // 女频

    /**
     * 获取频道描述
     */
    public String getChannelDescription() {
        if (channel == null) {
            return "未知";
        }
        return channel == CHANNEL_MALE ? "男频" : "女频";
    }

    /**
     * 是否为男频
     */
    public boolean isMaleChannel() {
        return channel != null && channel == CHANNEL_MALE;
    }

    /**
     * 是否为女频
     */
    public boolean isFemaleChannel() {
        return channel != null && channel == CHANNEL_FEMALE;
    }
}
