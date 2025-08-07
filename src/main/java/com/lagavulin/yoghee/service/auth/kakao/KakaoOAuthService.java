package com.lagavulin.yoghee.service.auth.kakao;

import com.lagavulin.yoghee.service.auth.kakao.model.KakaoToken;
import com.lagavulin.yoghee.service.auth.kakao.model.KakaoUserInfo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class KakaoOAuthService {

    private final KakaoAuthClient kakaoAuthClient;
    private final KakaoUserClient kakaoUserClient;

    @Value("${kakao.client_id}")
    private String clientId;

    public KakaoToken getAccessToken(String code) {
        KakaoToken kakaoToken = kakaoAuthClient.getAccessToken("authorization_code", clientId, code);

        log.info(" [Kakao Service] Access Token ------> {}", kakaoToken.getAccessToken());
        log.info(" [Kakao Service] Refresh Token ------> {}", kakaoToken.getRefreshToken());
        log.info(" [Kakao Service] Id Token ------> {}", kakaoToken.getIdToken());
        log.info(" [Kakao Service] Scope ------> {}", kakaoToken.getScope());

        return kakaoToken;
    }

    public KakaoUserInfo getUserInfo(String accessToken) {
        KakaoUserInfo userInfo = kakaoUserClient.getUserInfo("Bearer " + accessToken);

        log.info("[ Kakao Service ] Auth ID ---> {} ", userInfo.getId());
        log.info("[ Kakao Service ] NickName ---> {} ", userInfo.getKakaoAccount().getProfile().getNickName());
        log.info("[ Kakao Service ] ProfileImageUrl ---> {} ", userInfo.getKakaoAccount().getProfile().getProfileImageUrl());

        return userInfo;
    }
}
