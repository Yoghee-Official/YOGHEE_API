package com.lagavulin.yoghee.service.auth.google;

import com.lagavulin.yoghee.service.auth.google.model.GoogleTokenReq;
import com.lagavulin.yoghee.service.auth.google.model.GoogleTokenRes;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(value = "googleAuthClient", url="https://oauth2.googleapis.com")
public interface GoogleAuthClient {
    @PostMapping("/token")
    ResponseEntity<GoogleTokenRes> getAccessToken(@RequestBody GoogleTokenReq googleTokenReq);
}
