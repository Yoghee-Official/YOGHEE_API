package com.lagavulin.yoghee.util;


import lombok.AllArgsConstructor;
import lombok.Getter;

public class AddressParser {

    @Getter
    @AllArgsConstructor
    public static class ParsedAddress {

        private String depth1;
        private String depth2;
        private String depth3;
    }

    /**
     * 한국 주소를 depth1(시/도), depth2(시/군/구), depth3(동/읍/면)으로 파싱
     * <p>
     * 예시: - "서울특별시 강남구 역삼동" -> depth1: 서울, depth2: 강남구, depth3: 역삼동 - "대구광역시 동구 신서동" -> depth1: 대구, depth2: 동구, depth3: 신서동 - "경북 경주시 용강동" -> depth1:
     * 경북, depth2: 경주시, depth3: 용강동 - "충북 음성군 맹동면" -> depth1: 충북, depth2: 음성군, depth3: 맹동면 - "제주특별자치도 제주시 애월읍" -> depth1: 제주, depth2: 제주시, depth3:
     * 애월읍
     */
    public static ParsedAddress parse(String address) {
        if (address == null || address.isEmpty()) {
            return new ParsedAddress(null, null, null);
        }

        // 공백으로 분리
        String[] parts = address.trim().split("\\s+");

        if (parts.length < 3) {
            return new ParsedAddress(null, null, null);
        }

        String depth1 = normalizeDepth1(parts[0]);
        String depth2 = normalizeDepth2(parts[1]);
        String depth3 = normalizeDepth3(parts[2]);

        return new ParsedAddress(depth1, depth2, depth3);
    }

    /**
     * 시/도 정규화 서울특별시 -> 서울, 경상북도 -> 경북, 전라남도 -> 전남 등
     */
    private static String normalizeDepth1(String depth1) {
        // 특별시/광역시/특별자치시
        depth1 = depth1.replaceAll("특별시|광역시|특별자치시", "");

        // 도 제거
        depth1 = depth1.replaceAll("도$", "");

        // 특별자치도 처리
        if (depth1.contains("특별자치")) {
            depth1 = depth1.replace("특별자치", "");
        }

        // 도 약어 처리 (이미 약어면 그대로)
        if (depth1.length() == 2) {
            return depth1;
        }

        // 긴 이름 -> 약어 변환
        depth1 = depth1.replace("경상북", "경북")
                       .replace("경상남", "경남")
                       .replace("전라북", "전북")
                       .replace("전라남", "전남")
                       .replace("충청북", "충북")
                       .replace("충청남", "충남");

        return depth1;
    }

    /**
     * 시/군/구 정규화 그대로 유지
     */
    private static String normalizeDepth2(String depth2) {
        return depth2;
    }

    /**
     * 동/읍/면/리 정규화 그대로 유지
     */
    private static String normalizeDepth3(String depth3) {
        return depth3;
    }
}

