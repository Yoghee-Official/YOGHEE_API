package com.lagavulin.yoghee.service.auth.google;

import com.lagavulin.yoghee.service.auth.google.model.GoogleUserInfo;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(value = "googleUserClient", url="https://www.googleapis.com")
public interface GoogleUserClient {
    @GetMapping("/userinfo/v2/me")
    ResponseEntity<GoogleUserInfo> getAccessToken(@RequestParam String accessToken);
}
