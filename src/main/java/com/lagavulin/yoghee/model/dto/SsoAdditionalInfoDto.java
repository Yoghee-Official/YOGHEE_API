package com.lagavulin.yoghee.model.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * SSO 회원가입 시 추가 정보 입력 DTO
 */
@Data
@Schema(description = "SSO 회원가입 추가 정보")
public class SsoAdditionalInfoDto {

    @Schema(description = "전화번호", example = "010-1234-5678")
    private String phoneNumber;

    @Schema(description = "성별", example = "M", allowableValues = {"M", "F", "O"})
    private String gender;

    @Schema(description = "닉네임", example = "요가러버")
    private String nickname;

    /**
     * 데이터 유효성 검증
     */
    public String validate() {
        // 전화번호 형식 검증 (선택사항)
        if (phoneNumber != null && !phoneNumber.trim().isEmpty()) {
            String cleanPhoneNumber = phoneNumber.replaceAll("[^0-9]", "");
            if (cleanPhoneNumber.length() < 10 || cleanPhoneNumber.length() > 11) {
                return "전화번호 형식이 올바르지 않습니다.";
            }
        }

        // 성별 검증 (선택사항)
        if (gender != null && !gender.trim().isEmpty()) {
            if (!gender.matches("^[MFO]$")) {
                return "성별은 M(남성), F(여성), O(기타) 중 하나여야 합니다.";
            }
        }

        // 닉네임 검증 (선택사항)
        if (nickname != null && !nickname.trim().isEmpty()) {
            if (nickname.length() > 20) {
                return "닉네임은 20자를 초과할 수 없습니다.";
            }
        }

        return null; // 검증 통과
    }

    /**
     * 전화번호를 정규화 (숫자만 남기고 하이픈 등 제거)
     */
    public String getNormalizedPhoneNumber() {
        if (phoneNumber == null || phoneNumber.trim().isEmpty()) {
            return null;
        }
        return phoneNumber.replaceAll("[^0-9]", "");
    }

    /**
     * 닉네임을 정리 (앞뒤 공백 제거)
     */
    public String getTrimmedNickname() {
        if (nickname == null || nickname.trim().isEmpty()) {
            return null;
        }
        return nickname.trim();
    }

    /**
     * 성별을 정리 (대문자로 변환)
     */
    public String getNormalizedGender() {
        if (gender == null || gender.trim().isEmpty()) {
            return null;
        }
        return gender.trim().toUpperCase();
    }
}
