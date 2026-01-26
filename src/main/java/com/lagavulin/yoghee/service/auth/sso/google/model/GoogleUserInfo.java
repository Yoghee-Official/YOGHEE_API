package com.lagavulin.yoghee.service.auth.sso.google.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.lagavulin.yoghee.model.enums.SsoType;
import com.lagavulin.yoghee.service.auth.sso.SsoUserInfo;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class GoogleUserInfo extends SsoUserInfo {

    private String sub;               // Google 계정의 고유 ID
    private String name;              // 사용자 이름
    private String email;             // 이메일
    private String picture;           // 프로필 이미지 URL

    @JsonProperty("given_name")
    private String givenName;         // 이름

    @JsonProperty("family_name")
    private String familyName;        // 성

    private String locale;            // 로케일 (ko, en 등)

    @JsonProperty("email_verified")
    private Boolean emailVerified;    // 이메일 인증 여부

    private String gender;            // 성별 (Google+에서만 제공, 현재는 제한적)

    // SSO 가입 시 추가 정보 (클라이언트에서 입력)
    private String phoneNumber;       // 전화번호 (클라이언트에서 입력)
    private String userGender;        // 성별 (클라이언트에서 입력)
    private String userNickname;      // 닉네임 (클라이언트에서 입력)

    @Override
    public String getSsoId() {
        return sub;
    }

    @Override
    public String getEmail() {
        return email;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public String getPhoneNumber() {
        // 클라이언트에서 입력한 전화번호 우선 사용
        return phoneNumber;
    }

    @Override
    public String getGender() {
        // 클라이언트에서 입력한 성별 우선 사용
        if (userGender != null && !userGender.trim().isEmpty()) {
            return userGender;
        }
        // 구글에서 제공한 성별 사용 (현재는 제한적)
        return gender;
    }

    @Override
    public String getNickname() {
        // 클라이언트에서 입력한 닉네임 우선 사용
        return userNickname;
    }

    @Override
    public String getProfileImageUrl() {
        return picture;
    }

    @Override
    public String getProvider() {
        return SsoType.GOOGLE.getVendor();
    }

    /**
     * 클라이언트에서 추가 정보 설정
     */
    public void setAdditionalInfo(String phoneNumber, String gender, String nickname) {
        this.phoneNumber = phoneNumber;
        this.userGender = gender;
        this.userNickname = nickname;
    }
}
