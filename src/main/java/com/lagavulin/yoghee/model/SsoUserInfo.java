package com.lagavulin.yoghee.model;

public abstract class SsoUserInfo {
    public abstract String getSsoId();
    public abstract String getEmail();
    public abstract String getName();
    public abstract String getProfileImageUrl();
    public abstract String getProvider();
}
