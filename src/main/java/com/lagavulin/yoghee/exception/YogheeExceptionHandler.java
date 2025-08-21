package com.lagavulin.yoghee.exception;

import com.lagavulin.yoghee.model.ApiResponse;
import com.lagavulin.yoghee.util.ResponseUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.NoHandlerFoundException;

@Slf4j
@RestControllerAdvice
public class YogheeExceptionHandler {

    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<?> businessException(BusinessException e) {
        log.error("BusinessException: {}", e.getMessage());
        return ResponseUtil.fail(e);
    }
    @ExceptionHandler(NoHandlerFoundException.class)
    public ResponseEntity<?> handleNotFound(NoHandlerFoundException e) {
        return ResponseUtil.fail(ErrorCode.RESOURCE_NOT_FOUND);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse<?>> handleException(Exception e) {
        log.error("Unhandled Exception 발생: ", e);
        return ResponseUtil.fail(ErrorCode.INTERNAL_SERVER_ERROR);
    }
}
