package com.lagavulin.yoghee.service.kakao;

import com.lagavulin.yoghee.service.kakao.model.KakaoAddressSearchResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class KakaoLocalService {

    private final KakaoLocalClient kakaoLocalClient;

    @Value("${kakao.rest-api-key:}")
    private String restApiKey;

    /**
     * 주소를 좌표로 변환
     *
     * @param address 검색할 주소 (도로명 주소 또는 지번 주소)
     * @return 위도, 경도 배열 [latitude, longitude]
     */
    public double[] getCoordinates(String address) {
        try {
            if (restApiKey == null || restApiKey.isEmpty()) {
                log.warn("Kakao REST API Key가 설정되지 않았습니다. 좌표 변환을 건너뜁니다.");
                return null;
            }

            String authorization = "KakaoAK " + restApiKey;
            KakaoAddressSearchResponse response = kakaoLocalClient.searchAddress(authorization, address);

            if (response == null || response.getDocuments() == null || response.getDocuments().isEmpty()) {
                log.warn("주소 검색 결과가 없습니다: {}", address);
                return null;
            }

            // 첫 번째 결과 사용
            KakaoAddressSearchResponse.Document document = response.getDocuments().get(0);
            double latitude = Double.parseDouble(document.getY());
            double longitude = Double.parseDouble(document.getX());

            log.info("주소 좌표 변환 성공: {} -> ({}, {})", address, latitude, longitude);
            return new double[]{latitude, longitude};

        } catch (Exception e) {
            log.error("주소 좌표 변환 실패: {}", address, e);
            return null;
        }
    }

    /**
     * 도로명 주소 또는 지번 주소로 좌표 검색 (우선순위: 도로명 > 지번)
     *
     * @param roadAddress  도로명 주소
     * @param jibunAddress 지번 주소
     * @return 위도, 경도 배열 [latitude, longitude]
     */
    public double[] getCoordinatesFromAddresses(String roadAddress, String jibunAddress) {
        // 도로명 주소 우선 시도
        if (roadAddress != null && !roadAddress.isEmpty()) {
            double[] coords = getCoordinates(roadAddress);
            if (coords != null) {
                return coords;
            }
        }

        // 도로명 주소 실패시 지번 주소 시도
        if (jibunAddress != null && !jibunAddress.isEmpty()) {
            double[] coords = getCoordinates(jibunAddress);
            if (coords != null) {
                return coords;
            }
        }

        return null;
    }
}

