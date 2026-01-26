package com.lagavulin.yoghee.service.auth.sso;

public abstract class SsoUserInfo {

    public abstract String getSsoId();

    public abstract String getEmail();

    public abstract String getPhoneNumber();

    public abstract String getGender();

    public abstract String getNickname();

    public abstract String getName();

    public abstract String getProfileImageUrl();

    public abstract String getProvider();

    /**
     * 닉네임 반환 (없으면 이름으로 대체, 이름도 없으면 기본값)
     */
    public String getValidNickname() {
        String nickname = getNickname();
        if (nickname != null && !nickname.trim().isEmpty()) {
            return nickname.trim();
        }

        String name = getName();
        if (name != null && !name.trim().isEmpty()) {
            return name.trim();
        }

        // 닉네임도 이름도 없으면 기본값 "요기니"
        return "요기니";
    }

    /**
     * 전화번호 반환 (빈 값이면 null 반환)
     */
    public String getValidPhoneNumber() {
        String phoneNumber = getPhoneNumber();
        return (phoneNumber != null && !phoneNumber.trim().isEmpty()) ? phoneNumber.trim() : null;
    }

    /**
     * 성별 반환 (빈 값이면 null 반환)
     */
    public String getValidGender() {
        String gender = getGender();
        return (gender != null && !gender.trim().isEmpty()) ? gender.trim() : null;
    }

    /**
     * 이름 반환 (빈 값이면 닉네임으로 대체, 닉네임도 없으면 기본값) ⭐ 카카오가 이름을 제공하지 않고 닉네임만 제공하는 경우를 처리
     */
    public String getValidName() {
        String name = getName();
        if (name != null && !name.trim().isEmpty()) {
            return name.trim();
        }

        // 이름이 없으면 닉네임을 이름으로 사용 (카카오가 닉네임만 제공하는 경우)
        String nickname = getNickname();
        if (nickname != null && !nickname.trim().isEmpty()) {
            return nickname.trim();
        }

        // 이름도 닉네임도 없으면 기본값 "요기니"
        return "요기니";
    }

    /**
     * 프로필 이미지 URL 반환 (빈 값이면 null 반환)
     */
    public String getValidProfileImageUrl() {
        String profileUrl = getProfileImageUrl();
        return (profileUrl != null && !profileUrl.trim().isEmpty()) ? profileUrl.trim() : null;
    }
}
