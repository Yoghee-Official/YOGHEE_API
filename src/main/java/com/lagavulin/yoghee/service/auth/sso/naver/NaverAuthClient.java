package com.lagavulin.yoghee.service.auth.sso.naver;

import com.lagavulin.yoghee.service.auth.sso.naver.model.NaverToken;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "naver-auth", url = "https://nid.naver.com")
public interface NaverAuthClient {

    @PostMapping("/oauth2.0/token")
    NaverToken getAccessToken(
        @RequestParam("grant_type") String grantType,
        @RequestParam("client_id") String clientId,
        @RequestParam("client_secret") String clientSecret,
        @RequestParam("code") String code,
        @RequestParam("state") String state
    );
}
