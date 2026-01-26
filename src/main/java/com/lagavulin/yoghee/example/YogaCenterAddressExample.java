package com.lagavulin.yoghee.example;

import java.util.Date;

import com.lagavulin.yoghee.entity.YogaCenter;
import com.lagavulin.yoghee.util.AddressParser;

/**
 * YogaCenter 주소 사용 예시
 */
public class YogaCenterAddressExample {

    /**
     * 1. 카카오 주소 API 응답으로부터 YogaCenter 생성 예시
     */
    public YogaCenter createCenterFromKakaoAddress() {
        // 카카오 주소 API 응답 예시
        String address = "서울 강남구 테헤란로 212";
        String roadAddress = "서울 강남구 테헤란로 212";
        String jibunAddress = "서울 강남구 역삼동 718-5";
        String zonecode = "06220";
        String buildingName = "멀티캠퍼스";
        String bname = "역삼동";

        // 전체 주소 구성
        String fullAddress = AddressParser.extractFullAddressFromKakao(address, bname, buildingName);

        // YogaCenter 엔티티 생성
        YogaCenter.YogaCenterBuilder builder = YogaCenter.builder()
                                                         .name("강남 요가 센터")
                                                         .phoneNo("02-1234-5678")
                                                         .description("강남역 근처 프리미엄 요가 센터")
                                                         .thumbnail("https://example.com/thumbnail.jpg")
                                                         .masterId("master-uuid")
                                                         .createdAt(new Date())
                                                         .latitude(37.4989885)
                                                         .longitude(127.0304713);

        // 주소 정보 적용
        YogaCenter center = AddressParser.applyAddress(
            builder,
            address,  // 기본 주소 (파싱용)
            roadAddress,
            jibunAddress,
            zonecode,
            buildingName + " 3층" // 상세 주소
        ).build();

        return center;
    }

    /**
     * 2. 수동으로 3단계 주소 설정 예시
     */
    public YogaCenter createCenterManually() {
        return YogaCenter.builder()
                         .name("경주 요가 센터")
                         .addressDepth1("경북")      // 시/도
                         .addressDepth2("경주시")     // 시/군/구
                         .addressDepth3("용강동")     // 동/읍/면
                         .fullAddress("경북 경주시 용강동 123-45")
                         .roadAddress("경북 경주시 중앙로 123")
                         .jibunAddress("경북 경주시 용강동 123-45")
                         .zonecode("38100")
                         .addressDetail("요가빌딩 2층")
                         .phoneNo("054-1234-5678")
                         .description("경주 중심가 요가 센터")
                         .thumbnail("https://example.com/thumbnail2.jpg")
                         .masterId("master-uuid-2")
                         .createdAt(new Date())
                         .latitude(35.8562)
                         .longitude(129.2247)
                         .build();
    }

    /**
     * 3. 기존 문자열 주소를 3단계로 파싱하는 예시
     */
    public void parseExistingAddress() {
        String existingAddress = "서울 강남구 역삼동";

        AddressParser.AddressDepth depth = AddressParser.parseAddress(existingAddress);

        System.out.println("Depth1 (시/도): " + depth.depth1);      // 서울
        System.out.println("Depth2 (시/군/구): " + depth.depth2);    // 강남구
        System.out.println("Depth3 (동/읍/면): " + depth.depth3);    // 역삼동
    }

    /**
     * 4. 3단계 주소를 다시 조합하는 예시
     */
    public void combineAddress() {
        String combined = AddressParser.combineAddress("경북", "경주시", "용강동");
        System.out.println("Combined: " + combined); // "경북 경주시 용강동"
    }

    /**
     * 5. DTO에서 주소 정보 활용 예시
     */
    public void useDtoAddress() {
        // YogaCenterDto는 자동으로 다음 필드들을 포함합니다:
        // - addressDepth1, addressDepth2, addressDepth3 (3단계 주소)
        // - fullAddress (전체 주소)
        // - roadAddress (도로명 주소)
        // - jibunAddress (지번 주소)
        // - zonecode (우편번호)
        // - addressDetail (상세 주소)
        // - address (하위 호환성을 위한 필드, fullAddress와 동일)

        // API 응답 JSON 예시:
        /*
        {
          "centerId": "center-uuid",
          "addressDepth1": "서울",
          "addressDepth2": "강남구",
          "addressDepth3": "역삼동",
          "fullAddress": "서울 강남구 테헤란로 212",
          "roadAddress": "서울 강남구 테헤란로 212",
          "jibunAddress": "서울 강남구 역삼동 718-5",
          "zonecode": "06220",
          "addressDetail": "멀티캠퍼스 3층",
          "address": "서울 강남구 테헤란로 212",  // 하위 호환성
          "name": "강남 요가 센터",
          "thumbnail": "https://example.com/thumbnail.jpg",
          "favoriteCount": 42,
          "isFavorite": true
        }
        */
    }
}

