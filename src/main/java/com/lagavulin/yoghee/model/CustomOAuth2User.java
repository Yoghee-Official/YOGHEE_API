package com.lagavulin.yoghee.model;

import lombok.Getter;

@Getter
public class CustomOAuth2User {

    public CustomOAuth2User(String userId){
        this.userId = userId;
    }
    private final String userId;
}