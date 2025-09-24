// src/main/java/com/sakura/novel_server/common/vo/ResultVO.java

package com.sakura.novel.core.common.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * 统一API响应结果封装类
 * @param <T> 响应数据的类型
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ResultVO<T> implements Serializable {

    /**
     * 业务状态码 (例如: 200-成功, 401-未认证, 500-服务器内部错误)
     */
    private Integer code;

    /**
     * 响应消息
     */
    private String message;

    /**
     * 响应数据
     */
    private T data;

    /**
     * 成功的静态工厂方法 (无数据)
     */
    public static <T> ResultVO<T> success() {
        return new ResultVO<>(200, "操作成功", null);
    }

    /**
     * 成功的静态工厂方法 (有数据)
     */
    public static <T> ResultVO<T> success(T data) {
        return new ResultVO<>(200, "操作成功", data);
    }

    /**
     * 成功的静态工厂方法 (自定义消息和数据)
     */
    public static <T> ResultVO<T> success(String message, T data) {
        return new ResultVO<>(200, message, data);
    }

    /**
     * 失败的静态工厂方法
     */
    public static <T> ResultVO<T> error(Integer code, String message) {
        return new ResultVO<>(code, message, null);
    }

    /**
     * 失败的静态工厂方法 (自定义数据)
     */
    public static <T> ResultVO<T> error(Integer code, String message, T data) {
        return new ResultVO<>(code, message, data);
    }
}