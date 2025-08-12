package com.lagavulin.yoghee.service.auth.google;

import com.lagavulin.yoghee.service.auth.google.model.GoogleToken;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(value = "googleAuthClient", url="https://oauth2.googleapis.com")
public interface GoogleAuthClient {
    @PostMapping(value = "/token", consumes = "application/x-www-form-urlencoded")
    GoogleToken getAccessToken(
            @RequestParam("code") String code,
            @RequestParam("client_id") String clientId,
            @RequestParam("client_secret") String clientSecret,
            @RequestParam("redirect_uri") String redirectUri,
            @RequestParam("grant_type") String grantType
        );
}
