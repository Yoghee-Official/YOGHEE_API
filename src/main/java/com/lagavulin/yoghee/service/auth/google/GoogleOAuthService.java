package com.lagavulin.yoghee.service.auth.google;

import com.lagavulin.yoghee.service.auth.google.model.GoogleTokenReq;
import com.lagavulin.yoghee.service.auth.google.model.GoogleTokenRes;
import com.lagavulin.yoghee.service.auth.google.model.GoogleUserInfo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class GoogleOAuthService {

    @Value("${google.client_id}")
    private String clientId;

    @Value("${google.client_secret}")
    private String clientSecret;

    @Value("${google.redirect_uri}")
    private String redirectUri;

    private final GoogleAuthClient googleAuthClient;
    public GoogleUserInfo getUserInfo(String code) {

        GoogleTokenReq googleTokenReq = GoogleTokenReq.builder()
                                                      .code(code)
                                                      .clientId(clientId)
                                                      .clientSecret(clientSecret)
                                                      .redirectUri(redirectUri)
                                                      .build();

        ResponseEntity<GoogleTokenRes> response = googleAuthClient.getAccessToken(googleTokenReq);

        log.info(" [Google Service] Access Token ------> {}", response.getBody());

        return null;
    }
}