package com.lagavulin.yoghee.service.auth.google.model;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class GoogleUserInfo {
    private String sub;       // Google 계정의 고유 ID
    private String name;      // 사용자 이름
    private String email;     // 이메일
    private String picture;   // 프로필 이미지 URL
}
