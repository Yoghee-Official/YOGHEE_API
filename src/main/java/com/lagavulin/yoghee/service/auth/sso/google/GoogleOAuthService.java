package com.lagavulin.yoghee.service.auth.sso.google;

import com.lagavulin.yoghee.service.auth.sso.AbstractOAuthService;
import com.lagavulin.yoghee.service.auth.sso.google.model.GoogleToken;
import com.lagavulin.yoghee.service.auth.sso.google.model.GoogleUserInfo;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class GoogleOAuthService extends AbstractOAuthService {

    @Value("${google.client_id}")
    private String clientId;

    @Value("${google.client_secret}")
    private String clientSecret;

    @Value("${google.redirect_uri}")
    private String redirectUri;

    private final GoogleAuthClient googleAuthClient;
    private final GoogleUserClient googleUserClient;

    protected GoogleToken requestAccessToken(String code) {
        return googleAuthClient.getAccessToken(code, clientId, clientSecret, redirectUri, "authorization_code");
    }

    @Override
    protected GoogleUserInfo requestUserInfo(String accessToken) {
        return googleUserClient.getUserInfo("Bearer " + accessToken);
    }
}