package com.lagavulin.yoghee.exception;

import com.lagavulin.yoghee.util.ResponseUtil;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class ErrorController implements org.springframework.boot.web.servlet.error.ErrorController {

    @RequestMapping("/error")
    public ResponseEntity<?> error(HttpServletRequest request) {
        return ResponseUtil.fail(ErrorCode.RESOURCE_NOT_FOUND);
    }
}