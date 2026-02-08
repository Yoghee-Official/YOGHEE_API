package com.lagavulin.yoghee.service.kakao;

import com.lagavulin.yoghee.service.kakao.model.KakaoAddressSearchResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "kakaoLocalClient", url = "https://dapi.kakao.com")
public interface KakaoLocalClient {

    /**
     * 주소로 좌표 검색
     *
     * @param authorization Kakao REST API Key (형식: "KakaoAK {REST_API_KEY}")
     * @param query         검색할 주소
     * @return 검색 결과
     */
    @GetMapping("/v2/local/search/address.json")
    KakaoAddressSearchResponse searchAddress(
        @RequestHeader("Authorization") String authorization,
        @RequestParam("query") String query
    );
}

