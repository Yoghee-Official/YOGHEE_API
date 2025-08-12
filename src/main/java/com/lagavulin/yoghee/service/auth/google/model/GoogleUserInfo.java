package com.lagavulin.yoghee.service.auth.google.model;

import com.lagavulin.yoghee.service.auth.SsoUserInfo;
import com.lagavulin.yoghee.model.enums.SsoType;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class GoogleUserInfo extends SsoUserInfo {
    private String sub;       // Google 계정의 고유 ID
    private String name;      // 사용자 이름
    private String email;     // 이메일
    private String picture;   // 프로필 이미지 URL


    @Override
    public String getSsoId() {
        return sub;
    }

    @Override
    public String getProfileImageUrl() {
        return picture;
    }

    @Override
    public String getProvider() {
        return SsoType.GOOGLE.getVendor();
    }
}
