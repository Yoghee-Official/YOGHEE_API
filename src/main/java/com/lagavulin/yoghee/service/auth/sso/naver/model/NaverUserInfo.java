package com.lagavulin.yoghee.service.auth.sso.naver.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.lagavulin.yoghee.model.enums.SsoType;
import com.lagavulin.yoghee.service.auth.sso.SsoUserInfo;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class NaverUserInfo extends SsoUserInfo {

    @JsonProperty("resultcode")
    private String resultCode;

    @JsonProperty("message")
    private String message;

    @JsonProperty("response")
    private NaverAccount response;

    // SSO 가입 시 추가 정보 (클라이언트에서 입력)
    private String phoneNumber;       // 전화번호 (클라이언트에서 입력)
    private String userGender;        // 성별 (클라이언트에서 입력)
    private String userNickname;      // 닉네임 (클라이언트에서 입력)

    @Override
    public String getSsoId() {
        return response != null ? response.getId() : null;
    }

    @Override
    public String getEmail() {
        return response != null ? response.getEmail() : null;
    }

    @Override
    public String getName() {
        return response != null ? response.getName() : null;
    }

    @Override
    public String getProfileImageUrl() {
        return response != null ? response.getProfileImage() : null;
    }

    @Override
    public String getPhoneNumber() {
        // 클라이언트에서 입력한 전화번호 우선 사용
        if (phoneNumber != null && !phoneNumber.trim().isEmpty()) {
            return phoneNumber;
        }
        // 네이버에서 제공한 전화번호 사용
        return response != null ? response.getMobile() : null;
    }

    @Override
    public String getGender() {
        // 클라이언트에서 입력한 성별 우선 사용
        if (userGender != null && !userGender.trim().isEmpty()) {
            return userGender;
        }
        // 네이버에서 제공한 성별 사용
        return response != null ? response.getGender() : null;
    }

    @Override
    public String getNickname() {
        // 클라이언트에서 입력한 닉네임 우선 사용
        if (userNickname != null && !userNickname.trim().isEmpty()) {
            return userNickname;
        }
        // 네이버에서 제공한 닉네임 사용
        return response != null ? response.getNickname() : null;
    }

    @Override
    public String getProvider() {
        return SsoType.NAVER.getVendor();
    }

    /**
     * 클라이언트에서 추가 정보 설정
     */
    public void setAdditionalInfo(String phoneNumber, String gender, String nickname) {
        this.phoneNumber = phoneNumber;
        this.userGender = gender;
        this.userNickname = nickname;
    }

    @Getter
    @NoArgsConstructor
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class NaverAccount {

        @JsonProperty("id")
        private String id;

        @JsonProperty("email")
        private String email;

        @JsonProperty("name")
        private String name;

        @JsonProperty("nickname")
        private String nickname;

        @JsonProperty("profile_image")
        private String profileImage;

        @JsonProperty("age")
        private String age;

        @JsonProperty("gender")
        private String gender;

        @JsonProperty("birthday")
        private String birthday;

        @JsonProperty("birthyear")
        private String birthyear;

        @JsonProperty("mobile")
        private String mobile;
    }
}
