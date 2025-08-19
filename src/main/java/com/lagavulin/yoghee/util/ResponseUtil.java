package com.lagavulin.yoghee.util;

import com.lagavulin.yoghee.exception.ErrorCode;
import com.lagavulin.yoghee.model.ApiResponse;
import lombok.experimental.UtilityClass;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

@UtilityClass
public class ResponseUtil {

    public static <T> ResponseEntity<ApiResponse<T>> success(T data) {
        ApiResponse<T> response = ApiResponse.<T>builder()
                                             .code(200)
                                             .status("success")
                                             .data(data)
                                             .build();
        return ResponseEntity.ok(response);
    }

    public static <T> ResponseEntity<ApiResponse<String>> success(String message) {
        ApiResponse<String> response = ApiResponse.<String>builder()
                                                  .code(200)
                                                  .status("success")
                                                  .data(message)
                                                  .build();
        return ResponseEntity.ok(response);
    }

    public static ResponseEntity<ApiResponse<?>> fail(ErrorCode errorCode) {
        ApiResponse<Void> response = ApiResponse.<Void>builder()
                                                .code(errorCode.getHttpStatus().value())
                                                .status("fail")
                                                .errorCode(errorCode.getErrorCode())
                                                .errorMessage(errorCode.getErrorMessage())
                                                .build();
        return ResponseEntity.status(errorCode.getHttpStatus().value()).body(response);
    }
}