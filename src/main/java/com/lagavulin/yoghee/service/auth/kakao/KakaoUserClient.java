package com.lagavulin.yoghee.service.auth.kakao;

import com.lagavulin.yoghee.service.auth.kakao.model.KakaoUserInfo;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;

@FeignClient(name = "kakaoUserClient", url = "https://kapi.kakao.com")
public interface KakaoUserClient {

    @GetMapping(value = "/v2/user/me", consumes = "application/x-www-form-urlencoded")
    KakaoUserInfo getUserInfo(@RequestHeader("Authorization") String authorization);
}
