package com.lagavulin.yoghee.model;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class TokenResponse {

    @Schema(description = "액세스 토큰", example = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...")
    private String accessToken;

    @Schema(description = "리프레시 토큰", example = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...")
    private String refreshToken;

    @Schema(description = "액세스 토큰 만료 시간(초) / 60분", example = "3600")
    private Long accessTokenExpiresIn;

    @Schema(description = "리프레시 토큰 만료 시간(초) / 30일", example = "2592000")
    private Long refreshTokenExpiresIn;

    private TokenResponse(Builder builder) {
        this.accessToken = builder.accessToken;
        this.refreshToken = builder.refreshToken;
        this.accessTokenExpiresIn = 3600L; // 1시간
        this.refreshTokenExpiresIn = 2592000L; // 30일
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
