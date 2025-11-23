package com.lagavulin.yoghee.controller;

import java.security.Principal;
import java.util.UUID;

import com.lagavulin.yoghee.service.CenterService;
import com.lagavulin.yoghee.util.ResponseUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/api/center")
@RequiredArgsConstructor
@Tag(name = "Center", description = "요가원 관련 API")
public class CenterController {
    private final CenterService centerService;

    @PostMapping("/favorite/")
    @Operation(summary = "요가원찜 API", description = "유저 JWT 토큰을 통해 요가원 찜")
    public ResponseEntity<?> favoriteClass(
        @Parameter(name = "Authorization", description = "[Header] 사용자 JWT 토큰") Principal principal,
        @Parameter(name = "centerId", description = "Center ID")
        @RequestBody String centerId){
        String userUuid = principal.getName();
        centerService.addFavoriteClass(userUuid, centerId);

        return ResponseUtil.success("Successfully added favorite center " + centerId);
    }
}
