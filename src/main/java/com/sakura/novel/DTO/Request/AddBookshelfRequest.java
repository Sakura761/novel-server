package com.sakura.novel.DTO.Request;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class AddBookshelfRequest {
    @NotNull(message = "书籍ID不能为空")
    private Integer bookId;
    private Integer lastReadChapterId; // 可选
    private LocalDateTime lastReadTime; // 可选，ISO 8601 格式的字符串会自动转换为 LocalDateTime

}
