package com.example.common.exception;

import lombok.Getter;

/**
 * 业务异常 — 所有业务逻辑中的错误都抛这个
 * 类比前端：类似 throw new Error('用户不存在')
 */
@Getter
public class BusinessException extends RuntimeException {

    private final int code;

    public BusinessException(String message) {
        super(message);
        this.code = 400;
    }

    public BusinessException(int code, String message) {
        super(message);
        this.code = code;
    }
}
