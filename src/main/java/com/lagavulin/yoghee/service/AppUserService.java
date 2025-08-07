package com.lagavulin.yoghee.service;

import java.util.Optional;

import com.lagavulin.yoghee.entity.AppUser;
import com.lagavulin.yoghee.entity.UserSsoToken;
import com.lagavulin.yoghee.model.enums.SsoType;
import com.lagavulin.yoghee.repository.AppUserRepository;
import com.lagavulin.yoghee.repository.UserSsoTokenRepository;
import com.lagavulin.yoghee.model.SsoToken;
import com.lagavulin.yoghee.model.SsoUserInfo;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class AppUserService {
    private final AppUserRepository appUserRepository;
    private final UserSsoTokenRepository userSsoTokenRepository;
    public AppUser ssoUserLogin(SsoType ssoType, SsoToken ssoToken, SsoUserInfo ssoUserInfo) {
        Optional<UserSsoToken> userSsoToken = userSsoTokenRepository.findBySsoTypeAndSsoUserId(ssoType, ssoUserInfo.getSsoId());
        if(userSsoToken.isPresent()) {
            return userSsoToken.get().getUser();
        }

        AppUser newUser = AppUser.builder()
            .name(ssoUserInfo.getName())
            .email(ssoUserInfo.getEmail())
            .ssoType(ssoType)
            .build();

        appUserRepository.save(newUser);

        UserSsoToken newSsoToken = UserSsoToken.builder()
            .accessToken(ssoToken.getAccessToken())
            .accessTokenExpiresAt(ssoToken.getAccessTokenExpiresAt())
            .refreshToken(ssoToken.getRefreshToken())
            .refreshTokenExpiresAt(ssoToken.getRefreshTokenExpiresAt())
            .ssoType(ssoType)
            .ssoUserId(ssoUserInfo.getSsoId())
            .user(newUser)
            .build();
        userSsoTokenRepository.save(newSsoToken);

        return newUser;
    }
}
