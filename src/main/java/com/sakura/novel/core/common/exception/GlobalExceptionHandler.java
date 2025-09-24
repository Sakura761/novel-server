// src/main/java/com/sakura/novel/common/exception/GlobalExceptionHandler.java
package com.sakura.novel.core.common.exception;

import com.sakura.novel.core.common.vo.ResultVO;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

// @RestControllerAdvice 注解会自动扫描所有 @RestController
@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * 捕获所有未被处理的运行时异常
     */
    @ExceptionHandler(RuntimeException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR) // 设置HTTP状态码为500
    public ResultVO<String> handleRuntimeException(RuntimeException e) {
        // 实际项目中，这里应该记录日志
        // log.error("运行时异常: ", e);
        return ResultVO.error(500, "服务器内部错误: " + e.getMessage());
    }

    /**
     * 您还可以定义自定义异常，比如业务异常
     * @ExceptionHandler(BusinessException.class)
     * public ResultVO<String> handleBusinessException(BusinessException e) {
     *     return ResultVO.error(e.getCode(), e.getMessage());
     * }
     */
}