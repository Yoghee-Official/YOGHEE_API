package com.lagavulin.yoghee.exception;

import com.lagavulin.yoghee.model.ApiResponse;
import com.lagavulin.yoghee.util.ResponseUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.NoHandlerFoundException;
import org.springframework.web.servlet.resource.NoResourceFoundException;

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
        log.warn("No handler found for request: {}", e.getRequestURL());
        return ResponseUtil.fail(ErrorCode.RESOURCE_NOT_FOUND);
    }

    @ExceptionHandler(NoResourceFoundException.class)
    public ResponseEntity<?> handleNoResourceFound(NoResourceFoundException e) {
        String resourcePath = e.getResourcePath();

        // API 요청인 경우와 static resource 요청인 경우를 구분
        if (resourcePath.startsWith("api/")) {
            log.warn("API endpoint not found: {}", resourcePath);
            return ResponseUtil.fail(ErrorCode.RESOURCE_NOT_FOUND);
        } else if (resourcePath.equals("favicon.ico")) {
            // favicon 요청은 로그 레벨을 낮추고 조용히 처리
            log.debug("Favicon not found");
            return ResponseUtil.fail(ErrorCode.RESOURCE_NOT_FOUND);
        } else {
            // 기타 static resource 요청
            log.warn("Static resource not found: {}", resourcePath);
            return ResponseUtil.fail(ErrorCode.RESOURCE_NOT_FOUND);
        }
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse<?>> handleException(Exception e) {
        log.error("Unhandled Exception 발생: ", e);
        return ResponseUtil.fail(ErrorCode.INTERNAL_SERVER_ERROR);
    }
}
