package io.github.zcy427.rbac.common.exception;

import io.github.zcy427.rbac.common.result.Result;
import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.UUID;
import java.util.stream.Collectors;

// 全局异常处理器
@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<Result<Void>> handleBusinessException(BusinessException e) {
        ErrorCode errorCode = e.getErrorCode();

        return buildErrorResponse(
                errorCode.getHttpStatus(),
                errorCode.getCode(),
                e.getMessage()
        );
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Result<Void>> handleMethodArgumentNotValidException(
            MethodArgumentNotValidException e
    ) {
        String errorMessage = e.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(error -> error.getField() + ": " + error.getDefaultMessage())
                .collect(Collectors.joining("; "));

        return buildErrorResponse(
                ErrorCode.VALIDATION_ERROR.getHttpStatus(),
                ErrorCode.VALIDATION_ERROR.getCode(),
                errorMessage
        );
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<Result<Void>> handleConstraintViolationException(
            ConstraintViolationException e
    ) {
        String errorMessage = e.getConstraintViolations()
                .stream()
                .map(error -> error.getPropertyPath() + ": " + error.getMessage())
                .collect(Collectors.joining("; "));

        return buildErrorResponse(
                ErrorCode.VALIDATION_ERROR.getHttpStatus(),
                ErrorCode.VALIDATION_ERROR.getCode(),
                errorMessage
        );
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<Result<Void>> handleHttpMessageNotReadableException(
            HttpMessageNotReadableException e
    ) {
        return buildErrorResponse(
                ErrorCode.REQUEST_BODY_ERROR.getHttpStatus(),
                ErrorCode.REQUEST_BODY_ERROR.getCode(),
                ErrorCode.REQUEST_BODY_ERROR.getMessage()
        );
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Result<Void>> handleException(Exception e) {
        log.error("服务器内部异常: {}",e.getMessage(),e);
        return buildErrorResponse(
                ErrorCode.INTERNAL_ERROR.getHttpStatus(),
                ErrorCode.INTERNAL_ERROR.getCode(),
                ErrorCode.INTERNAL_ERROR.getMessage()
        );
    }

    // 统一构造异常响应，设置 HTTP 状态码并生成响应标识
    private ResponseEntity<Result<Void>> buildErrorResponse(
            HttpStatus status,
            String code,
            String message
    ) {
        return ResponseEntity.status(status)
                .body(Result.<Void>error(
                        code,
                        message,
                        UUID.randomUUID().toString()
                ));
    }
}
