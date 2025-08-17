package com.lagavulin.yoghee.util;

import com.lagavulin.yoghee.model.ApiResponse;
import lombok.experimental.UtilityClass;
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

    public static <T> ResponseEntity<ApiResponse<T>> error_400(T data) {
        ApiResponse<T> response = ApiResponse.<T>builder()
                                             .code(400)
                                             .status("fail")
                                             .errorCode("400_1")
                                             .errorMessage("잘못된 요청입니다.")

                                             .build();
        return ResponseEntity.badRequest().body(response);
    }

    public static <T> ResponseEntity<ApiResponse<T>> error_500(T data) {
        ApiResponse<T> response = ApiResponse.<T>builder()
                                             .code(500)
                                             .status("fail")
                                             .data(data)
                                             .errorCode("500_1")
                                             .build();
        return ResponseEntity.internalServerError().body(response);
    }
}