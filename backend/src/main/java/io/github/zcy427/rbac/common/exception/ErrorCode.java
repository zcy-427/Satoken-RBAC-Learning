package io.github.zcy427.rbac.common.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;

// 统一错误码及默认提示
@Getter
@AllArgsConstructor
public enum ErrorCode {

    VALIDATION_ERROR("VALIDATION_ERROR", "参数校验失败"),
    REQUEST_BODY_ERROR("REQUEST_BODY_ERROR", "请求体格式错误"),
    INTERNAL_ERROR("INTERNAL_ERROR", "服务器内部错误");

    private final String code;
    private final String message;
}
