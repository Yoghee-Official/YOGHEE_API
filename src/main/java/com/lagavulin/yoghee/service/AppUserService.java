package com.lagavulin.yoghee.service;

import java.util.Optional;

import com.lagavulin.yoghee.entity.AppUser;
import com.lagavulin.yoghee.exception.BusinessException;
import com.lagavulin.yoghee.exception.ErrorCode;
import com.lagavulin.yoghee.model.dto.RegistrationDto;
import com.lagavulin.yoghee.model.dto.ResetPasswordDto;
import com.lagavulin.yoghee.model.dto.VerificationDto;
import com.lagavulin.yoghee.model.enums.SsoType;
import com.lagavulin.yoghee.repository.AppUserRepository;
import com.lagavulin.yoghee.service.auth.SsoUserInfo;
import com.lagavulin.yoghee.util.JwtUtil;
import io.jsonwebtoken.Claims;
import lombok.AllArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@AllArgsConstructor
public class AppUserService {
    private final AppUserRepository appUserRepository;
    private final JwtUtil jwtUtil;
    private final PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

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

    public void register(RegistrationDto registrationDto) {
        if(registrationDto.validate() != null){
            throw new BusinessException(ErrorCode.INVALID_REQUEST, registrationDto.validate());
        }

        Optional<AppUser> existingUserOpt = appUserRepository.findAppUserByEmail(registrationDto.getEmail());
        if (existingUserOpt.isPresent()) {
            throw new BusinessException(ErrorCode.INVALID_REQUEST, "이미 가입된 계정 : " + registrationDto.getEmail());
        }

        idDuplicationCheck(registrationDto.getUserId());

        AppUser newUser = AppUser.builder()
            .userId(registrationDto.getUserId())
            .name(registrationDto.getName())
            .phoneNo(registrationDto.getPhoneNo())
            .email(registrationDto.getEmail())
            .password(passwordEncoder.encode(registrationDto.getPassword()))
            .sso("N")
            .build();

        appUserRepository.save(newUser);

        // TODO 추후 고도화 비동기로 환영 이메일 발송
    }

    public AppUser login(String userId, String rawPassword) {
        AppUser user = appUserRepository.findByUserId(userId)
                                        .orElseThrow(() -> new BusinessException(ErrorCode.UNAUTHORIZED));

        if (!passwordEncoder.matches(rawPassword, user.getPassword())) {
            throw new BusinessException(ErrorCode.UNAUTHORIZED);
        }

        return user;
    }

    public void idDuplicationCheck(String id) {
        Optional<AppUser> user = appUserRepository.findByUserId(id);
        if(user.isPresent()){
            throw new BusinessException(ErrorCode.INVALID_REQUEST, "사용중인 ID : " + id);
        }
    }

    public AppUser findUserByEmail(String email) {
        AppUser appUser = appUserRepository.findAppUserByEmail(email)
                                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND, "해당 이메일로 가입된 계정이 없습니다."));

        if(appUser.getSso().equals("Y")){
            throw new BusinessException(ErrorCode.INVALID_REQUEST, "비밀번호 재설정이 불가능한 계정입니다.");
        }
        return appUser;
    }

    public AppUser findUserByPhoneNo(String phoneNo) {
        AppUser appUser = appUserRepository.findAppUserByPhoneNo(phoneNo)
                                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND, "해당 전화번호로 가입된 계정이 없습니다."));

        if(appUser.getSso().equals("Y")){
            throw new BusinessException(ErrorCode.INVALID_REQUEST, "비밀번호 재설정이 불가능한 계정입니다.");
        }
        return appUser;
    }

    public void resetPassword(ResetPasswordDto resetPasswordDto) {
        Claims claims = jwtUtil.getClaimsFromResetPasswordToken(resetPasswordDto.getResetPasswordToken());
        String type = claims.get("type", String.class);
        String target = claims.get("target", String.class);

        if(!resetPasswordDto.validatePassword()){
            throw new BusinessException(ErrorCode.INVALID_REQUEST, "비밀번호는 8자 이상 15자 이하이며 영문, 숫자, 특수문자를 모두 포함해야 합니다.");
        }

        AppUser user = null;
        switch(type) {
            case "EMAIL" -> user = appUserRepository.findAppUserByEmail(target)
                                             .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND, "해당 이메일로 가입된 계정이 없습니다."));
            case "PHONE" -> user = appUserRepository.findAppUserByPhoneNo(target)
                                             .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND, "해당 전화번호로 가입된 계정이 없습니다."));
            default -> throw new BusinessException(ErrorCode.INVALID_REQUEST, "지원하지 않는 타입입니다.");
        }
        user.setPassword(passwordEncoder.encode(resetPasswordDto.getNewPassword()));
        appUserRepository.save(user);
    }
}
