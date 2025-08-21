package com.lagavulin.yoghee.service;

import java.util.Optional;

import com.lagavulin.yoghee.entity.AppUser;
import com.lagavulin.yoghee.exception.BusinessException;
import com.lagavulin.yoghee.exception.ErrorCode;
import com.lagavulin.yoghee.model.dto.RegistrationDto;
import com.lagavulin.yoghee.model.enums.SsoType;
import com.lagavulin.yoghee.repository.AppUserRepository;
import com.lagavulin.yoghee.service.auth.SsoUserInfo;
import lombok.AllArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@AllArgsConstructor
public class AppUserService {
    private final AppUserRepository appUserRepository;
    private final PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
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

    public void register(RegistrationDto registerationDto) {
        Optional<AppUser> existingUserOpt = appUserRepository.findAppUserByEmail(registerationDto.getEmail());
        if (existingUserOpt.isPresent()) {
            throw new BusinessException(ErrorCode.INVALID_REQUEST, "이미 가입된 계정 : " + registerationDto.getEmail());
        }

        AppUser newUser = AppUser.builder()
            .email(registerationDto.getEmail())
            .password(passwordEncoder.encode(registerationDto.getPassword()))
            .sso("N")
            .build();

        appUserRepository.save(newUser);
    }

    public AppUser login(String email, String rawPassword) {
        AppUser user = appUserRepository.findAppUserByEmail(email)
                                        .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));

        if (!passwordEncoder.matches(rawPassword, user.getPassword())) {
            throw new BusinessException(ErrorCode.UNAUTHORIZED);
        }

        return user;
    }
}
