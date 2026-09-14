package io.github.zcy427.rbac.common.exception;

import lombok.Getter;

// 业务异常
@Getter
public class BusinessException extends RuntimeException {

    private final String code;

    public BusinessException(String code, String message) {
        super(message);
        this.code = code;
    }
}