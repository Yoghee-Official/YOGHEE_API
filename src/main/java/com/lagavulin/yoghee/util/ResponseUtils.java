package com.lagavulin.yoghee.util;

import lombok.experimental.UtilityClass;
import org.springframework.http.ResponseEntity;

@UtilityClass
public class ResponseUtils {
    public static <T> ResponseEntity<T> success(T data) {
        return ResponseEntity.ok(data);
    }

    public static <T> ResponseEntity<T> error_400(T data) {
        return ResponseEntity.badRequest().body(data);
    }

    public static <T> ResponseEntity<T> error_500(T data) {
        return ResponseEntity.internalServerError().body(data);
    }
}
