package com.lagavulin.yoghee.controller;

import io.swagger.v3.oas.annotations.Hidden;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Tag(name = "Test", description = "테스트용 모음")
@RequestMapping("/api/v1/test")
public class TestController {

    @GetMapping("/")
    @Operation(summary = "인증 검증용 Hello API", description = "return Hello")
    @ApiResponse(responseCode = "200", description = "Hello")
    public String hello() {
        return "Hello";
    }
}
