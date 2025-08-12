package com.lagavulin.yoghee.service.auth;

public abstract class AbstractOAuthService {

    protected abstract SsoToken requestAccessToken(String code);
    protected abstract SsoUserInfo requestUserInfo(String accessToken);

    public SsoToken getAccessToken(String code){
        return requestAccessToken(code);
    }

    public SsoUserInfo getUserInfo(String accessToken){
        return requestUserInfo(accessToken);
    }
}
