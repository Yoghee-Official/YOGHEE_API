package com.lagavulin.yoghee.controller;

import java.time.Duration;
import java.util.List;
import java.util.Map;

//import com.lagavulin.yoghee.service.ImageService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Tag(name = "Test", description = "테스트용 모음")
@RequestMapping("/test")
@RequiredArgsConstructor
@CrossOrigin(origins = "*") // 모든 도메인 허용 (개발용)
public class TestController {

//    private final ImageService imageService;
    @GetMapping("/")
    @Operation(summary = "인증 검증용 Hello API", description = "return Hello")
    @ApiResponse(responseCode = "200", description = "Hello")
    public String hello() {
        return "Hello";
    }

//    @PostMapping("/presign")
//    public ResponseEntity<?> presignMultiple(
//        @RequestBody PresignRequest request
//    ) {
//        List<Map<String, Object>> presignedList = imageService.generatePresignedUrls(
//            request.filenames, request.contentType, Duration.ofMinutes(10)
//        );
//        return ResponseEntity.ok(presignedList);
//    }
//
//    public static class PresignRequest {
//        public List<String> filenames;
//        public String contentType;
//    }
}
