package com.sakura.novel.entity;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 交易表实体类
 * 记录用户的消费和打赏等交易信息
 */
@Data
public class Transaction {

    private Integer id;
    private Integer userId;
    private Integer bookId;
    private Integer chapterId;
    private BigDecimal amount;
    private TransactionType type;
    private LocalDateTime createTime;

    public enum TransactionType {
        REWARD("reward"),
        SUBSCRIPTION("subscription"),
        PURCHASE("purchase");

        private final String value;

        TransactionType(String value) {
            this.value = value;
        }

        public String getValue() {
            return value;
        }
    }
}
