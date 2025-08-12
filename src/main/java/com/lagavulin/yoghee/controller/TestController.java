package com.lagavulin.yoghee.controller;

import io.swagger.v3.oas.annotations.Hidden;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/test")
@Hidden
public class TestController {

    @RequestMapping("/")
    public String hello() {
        return "Hello";
    }
}
