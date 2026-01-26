package com.lagavulin.yoghee.service.auth.sso;

import com.lagavulin.yoghee.exception.BusinessException;
import com.lagavulin.yoghee.exception.ErrorCode;
import com.lagavulin.yoghee.model.enums.SsoType;
import com.lagavulin.yoghee.service.auth.sso.google.GoogleOAuthService;
import com.lagavulin.yoghee.service.auth.sso.kakao.KakaoOAuthService;
import com.lagavulin.yoghee.service.auth.sso.naver.NaverOAuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

/**
 * OAuth 서비스 팩토리 SSO 타입에 따라 적절한 OAuth 서비스를 반환
 */
@Component
@RequiredArgsConstructor
public class OAuthServiceFactory {

    private final GoogleOAuthService googleOAuthService;
    private final KakaoOAuthService kakaoOAuthService;
    private final NaverOAuthService naverOAuthService;

    /**
     * SSO 타입에 따라 OAuth 서비스를 반환
     *
     * @param ssoType SSO 타입
     * @return 해당 OAuth 서비스
     * @throws BusinessException 지원하지 않는 SSO 타입인 경우
     */
    public AbstractOAuthService getOAuthService(SsoType ssoType) {
        return switch (ssoType) {
            case GOOGLE -> googleOAuthService;
            case KAKAO -> kakaoOAuthService;
            case NAVER -> naverOAuthService;
            case APPLE -> throw new BusinessException(ErrorCode.INVALID_REQUEST, "Apple OAuth는 아직 구현되지 않았습니다.");
            default -> throw new BusinessException(ErrorCode.INVALID_REQUEST, "지원하지 않는 SSO 타입: " + ssoType.getVendor());
        };
    }

    /**
     * 지원하는 모든 SSO 타입 반환
     *
     * @return 지원하는 SSO 타입 배열
     */
    public SsoType[] getSupportedSsoTypes() {
        return new SsoType[]{SsoType.GOOGLE, SsoType.KAKAO, SsoType.NAVER};
    }
}
