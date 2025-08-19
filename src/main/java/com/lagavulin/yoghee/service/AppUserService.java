package com.lagavulin.yoghee.service;

import java.util.Optional;

import com.lagavulin.yoghee.entity.AppUser;
import com.lagavulin.yoghee.model.enums.SsoType;
import com.lagavulin.yoghee.repository.AppUserRepository;
import com.lagavulin.yoghee.service.auth.SsoUserInfo;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@AllArgsConstructor
public class AppUserService {
    private final AppUserRepository appUserRepository;
//    private final UserSsoTokenRepository userSsoTokenRepository;

    @Transactional(rollbackFor = Exception.class)
    public AppUser ssoUserLogin(SsoType ssoType, SsoUserInfo ssoUserInfo) {
        Optional<AppUser> existingUserOpt = appUserRepository.findAppUserByEmail(ssoUserInfo.getEmail());
        if( existingUserOpt.isPresent()) {
            return existingUserOpt.get();
        }

        // 신규 가입
        AppUser newUser = AppUser.builder()
            .name(ssoUserInfo.getName())
            .email(ssoUserInfo.getEmail())
            .sso("Y")
            .ssoType(ssoType)
            .build();

        appUserRepository.save(newUser);

        return newUser;
    }
}
