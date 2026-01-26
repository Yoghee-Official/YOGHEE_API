package com.lagavulin.yoghee.util;

import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.time.Instant;
import java.util.Base64;
import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * 자격증 승인/거절 링크에 사용되는 보안 토큰 생성 및 검증 유틸리티
 */
@Slf4j
@Component
public class LicenseTokenUtil {

    private static final String HMAC_ALGORITHM = "HmacSHA256";
    private static final long TOKEN_VALIDITY_DAYS = 7; // 7일간 유효

    @Value("${yoghee.license.secret-key:yoghee-license-secre}")
    private String secretKey;

    /**
     * 자격증 승인/거절 토큰 생성
     *
     * @param licenseUuid 자격증 UUID
     * @return Base64 인코딩된 토큰 (licenseUuid:expiryTimestamp:signature)
     */
    public String generateToken(String licenseUuid) {
        try {
            long expiryTimestamp = Instant.now().plusSeconds(TOKEN_VALIDITY_DAYS * 24 * 60 * 60).getEpochSecond();
            String data = licenseUuid + ":" + expiryTimestamp;
            String signature = generateSignature(data);

            String token = data + ":" + signature;
            return Base64.getUrlEncoder().withoutPadding().encodeToString(token.getBytes(StandardCharsets.UTF_8));

        } catch (Exception e) {
            log.error("토큰 생성 실패: {}", e.getMessage(), e);
            throw new RuntimeException("토큰 생성에 실패했습니다.", e);
        }
    }

    /**
     * 토큰 검증 및 licenseUuid 추출
     *
     * @param token Base64 인코딩된 토큰
     * @return 검증된 licenseUuid
     * @throws IllegalArgumentException 토큰이 유효하지 않거나 만료된 경우
     */
    public String validateTokenAndGetLicenseUuid(String token) {
        try {
            // Base64 디코딩
            String decodedToken = new String(Base64.getUrlDecoder().decode(token), StandardCharsets.UTF_8);
            String[] parts = decodedToken.split(":");

            if (parts.length != 3) {
                throw new IllegalArgumentException("잘못된 토큰 형식입니다.");
            }

            String licenseUuid = parts[0];
            long expiryTimestamp = Long.parseLong(parts[1]);
            String providedSignature = parts[2];

            // 만료 확인
            if (Instant.now().getEpochSecond() > expiryTimestamp) {
                throw new IllegalArgumentException("만료된 토큰입니다.");
            }

            // 서명 검증
            String data = licenseUuid + ":" + expiryTimestamp;
            String expectedSignature = generateSignature(data);

            if (!expectedSignature.equals(providedSignature)) {
                throw new IllegalArgumentException("유효하지 않은 서명입니다.");
            }

            return licenseUuid;

        } catch (NumberFormatException e) {
            log.error("토큰 파싱 실패: {}", e.getMessage());
            throw new IllegalArgumentException("잘못된 토큰 형식입니다.", e);
        } catch (Exception e) {
            log.error("토큰 검증 실패: {}", e.getMessage());
            throw new IllegalArgumentException("토큰 검증에 실패했습니다.", e);
        }
    }

    /**
     * HMAC-SHA256 서명 생성
     */
    private String generateSignature(String data) throws NoSuchAlgorithmException, InvalidKeyException {
        Mac mac = Mac.getInstance(HMAC_ALGORITHM);
        SecretKeySpec secretKeySpec = new SecretKeySpec(secretKey.getBytes(StandardCharsets.UTF_8), HMAC_ALGORITHM);
        mac.init(secretKeySpec);

        byte[] signatureBytes = mac.doFinal(data.getBytes(StandardCharsets.UTF_8));
        return Base64.getUrlEncoder().withoutPadding().encodeToString(signatureBytes);
    }
}

