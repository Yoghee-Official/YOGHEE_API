package com.lagavulin.yoghee.model;

import com.lagavulin.yoghee.util.JwtUtil;
import lombok.Data;

@Data
public class TokenResponse {
    private String accessToken;
    private String refreshToken;
    private Long accessTokenExpiresIn;
    private Long refreshTokenExpiresIn;

    private TokenResponse(Builder builder) {
        this.accessToken = builder.accessToken;
        this.refreshToken = builder.refreshToken;
        this.accessTokenExpiresIn = JwtUtil.ACCESS_TOKEN_VALIDITY / 1000;
        this.refreshTokenExpiresIn = JwtUtil.REFRESH_TOKEN_VALIDITY / 1000;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static final class Builder {

        private String accessToken;
        private String refreshToken;

        public Builder accessToken(String accessToken) {
            this.accessToken = accessToken;
            return this;
        }

        public Builder refreshToken(String refreshToken) {
            this.refreshToken = refreshToken;
            return this;
        }

        public TokenResponse build() {
            return new TokenResponse(this);
        }
    }
}
