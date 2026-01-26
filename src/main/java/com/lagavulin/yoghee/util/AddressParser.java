package com.lagavulin.yoghee.util;

import com.lagavulin.yoghee.entity.YogaCenter;
import lombok.extern.slf4j.Slf4j;

/**
 * 주소 파싱 유틸리티 클래스 카카오 주소 API나 기타 주소 문자열을 3단계 주소로 파싱합니다.
 */
@Slf4j
public class AddressParser {

    public static AddressDepth parseAddress(String fullAddress) {
        if (fullAddress == null || fullAddress.isBlank()) {
            return new AddressDepth(null, null, null);
        }

        String[] parts = fullAddress.trim().split("\\s+");

        String depth1 = null;
        String depth2 = null;
        String depth3 = null;

        if (parts.length >= 1) {
            depth1 = parts[0]; // 시/도
        }

        if (parts.length >= 2) {
            // 경기도 성남시 분당구 케이스
            if (parts.length >= 3
                && parts[1].endsWith("시")
                && parts[2].endsWith("구")) {

                depth2 = parts[1] + " " + parts[2];

                // 동/읍/면이 있으면 depth3
                if (parts.length >= 4 && isEupMyeonDong(parts[3])) {
                    depth3 = parts[3];
                }
            }
            // 서울 / 광역시 (구)
            else if (parts[1].endsWith("구") || parts[1].endsWith("군") || parts[1].endsWith("시")) {
                depth2 = parts[1];

                if (parts.length >= 3 && isEupMyeonDong(parts[2])) {
                    depth3 = parts[2];
                }
            }
        }

        return new AddressDepth(depth1, depth2, depth3);
    }

    private static boolean isEupMyeonDong(String value) {
        return value.endsWith("동")
            || value.endsWith("읍")
            || value.endsWith("면");
    }

    /**
     * YogaCenter 엔티티 빌더에 주소 정보 설정
     */
    public static YogaCenter.YogaCenterBuilder applyAddress(
        YogaCenter.YogaCenterBuilder builder,
        String fullAddress,
        String roadAddress,
        String jibunAddress,
        String zonecode,
        String addressDetail) {

        AddressDepth depth = parseAddress(fullAddress);

        return builder
            .addressDepth1(depth.depth1)
            .addressDepth2(depth.depth2)
            .addressDepth3(depth.depth3)
            .fullAddress(fullAddress)
            .roadAddress(roadAddress)
            .jibunAddress(jibunAddress)
            .zonecode(zonecode)
            .addressDetail(addressDetail);
    }

    /**
     * 카카오 주소 API 응답에서 주소 추출
     */
    public static String extractFullAddressFromKakao(String address, String bname, String buildingName) {
        StringBuilder fullAddress = new StringBuilder(address);

        if (bname != null && !bname.isEmpty()) {
            fullAddress.append(" (").append(bname).append(")");
        }
        if (buildingName != null && !buildingName.isEmpty()) {
            fullAddress.append(", ").append(buildingName);
        }

        return fullAddress.toString();
    }

    /**
     * 3단계 주소 구조를 담는 DTO
     */
    public static class AddressDepth {

        public final String depth1; // 시/도
        public final String depth2; // 시/군/구
        public final String depth3; // 동/읍/면

        public AddressDepth(String depth1, String depth2, String depth3) {
            this.depth1 = depth1;
            this.depth2 = depth2;
            this.depth3 = depth3;
        }
    }

    /**
     * 3단계 주소를 하나의 문자열로 조합
     */
    public static String combineAddress(String depth1, String depth2, String depth3) {
        StringBuilder address = new StringBuilder();

        if (depth1 != null && !depth1.isEmpty()) {
            address.append(depth1);
        }
        if (depth2 != null && !depth2.isEmpty()) {
            if (address.length() > 0) {
                address.append(" ");
            }
            address.append(depth2);
        }
        if (depth3 != null && !depth3.isEmpty()) {
            if (address.length() > 0) {
                address.append(" ");
            }
            address.append(depth3);
        }

        return address.toString();
    }
}

