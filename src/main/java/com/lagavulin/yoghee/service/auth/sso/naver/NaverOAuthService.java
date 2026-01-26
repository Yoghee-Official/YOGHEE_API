package com.lagavulin.yoghee.service.auth.sso.naver;

import com.lagavulin.yoghee.service.auth.sso.AbstractOAuthService;
import com.lagavulin.yoghee.service.auth.sso.naver.model.NaverToken;
import com.lagavulin.yoghee.service.auth.sso.naver.model.NaverUserInfo;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class NaverOAuthService extends AbstractOAuthService {

    private final NaverAuthClient naverAuthClient;
    private final NaverUserClient naverUserClient;

    @Value("${naver.client_id}")
    private String clientId;

    @Value("${naver.client_secret}")
    private String clientSecret;

    @Value("${naver.state:naver_oauth_state}")
    private String state;

    @Override
    protected NaverToken requestAccessToken(String code) {
        return naverAuthClient.getAccessToken(
            "authorization_code",
            clientId,
            clientSecret,
            code,
            state
        );
    }

    @Override
    protected NaverUserInfo requestUserInfo(String accessToken) {
        return naverUserClient.getUserInfo("Bearer " + accessToken);
    }
}
