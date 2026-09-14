package io.github.zcy427.rbac.common.result;

import lombok.Data;

import java.io.Serializable;
import java.time.OffsetDateTime;

// API 统一响应对象
@Data
public class Result<T> implements Serializable {

    private static final long serialVersionUID = 1L;

    private String code;
    private String message;
    private T data;
    private String traceId;
    private OffsetDateTime timestamp;

    public static <T> Result<T> success(T data, String traceId) {
        Result<T> result = new Result<>();
        result.code = "SUCCESS";
        result.message = "操作成功";
        result.data = data;
        result.traceId = traceId;
        result.timestamp = OffsetDateTime.now();
        return result;
    }

    public static <T> Result<T> error(
            String code,
            String message,
            String traceId
    ) {
        Result<T> result = new Result<>();
        result.code = code;
        result.message = message;
        result.traceId = traceId;
        result.timestamp = OffsetDateTime.now();
        return result;
    }
}