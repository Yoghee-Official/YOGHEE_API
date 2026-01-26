package com.lagavulin.yoghee.service.auth.sso.kakao;

import com.lagavulin.yoghee.service.auth.sso.AbstractOAuthService;
import com.lagavulin.yoghee.service.auth.sso.kakao.model.KakaoToken;
import com.lagavulin.yoghee.service.auth.sso.kakao.model.KakaoUserInfo;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class KakaoOAuthService extends AbstractOAuthService {

    private final KakaoAuthClient kakaoAuthClient;
    private final KakaoUserClient kakaoUserClient;

    @Value("${kakao.client_id}")
    private String clientId;

    @Override
    protected KakaoToken requestAccessToken(String code) {
        return kakaoAuthClient.getAccessToken("authorization_code", clientId, code);
    }

    @Override
    protected KakaoUserInfo requestUserInfo(String accessToken) {
        return kakaoUserClient.getUserInfo("Bearer " + accessToken);
    }
}
