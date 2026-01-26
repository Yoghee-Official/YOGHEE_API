package com.lagavulin.yoghee.service.auth.sso.naver;

import com.lagavulin.yoghee.service.auth.sso.naver.model.NaverUserInfo;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;

@FeignClient(name = "naver-user", url = "https://openapi.naver.com")
public interface NaverUserClient {

    @GetMapping("/v1/nid/me")
    NaverUserInfo getUserInfo(
        @RequestHeader("Authorization") String authorization
    );
}
