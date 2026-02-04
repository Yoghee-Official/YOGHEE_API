package com.lagavulin.yoghee.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * favicon 요청을 처리하여 404 에러 로그를 방지하는 컨트롤러
 */
@Controller
public class FaviconController {

    @GetMapping("/favicon.ico")
    public ResponseEntity<Void> favicon() {
        // 204 No Content로 응답하여 브라우저에서 더 이상 favicon을 요청하지 않도록 함
        return ResponseEntity.noContent().build();
    }
}
