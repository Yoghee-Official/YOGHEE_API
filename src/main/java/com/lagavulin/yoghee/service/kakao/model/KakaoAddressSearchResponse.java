package com.lagavulin.yoghee.service.kakao.model;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class KakaoAddressSearchResponse {

    @JsonProperty("meta")
    private Meta meta;

    @JsonProperty("documents")
    private List<Document> documents;

    @Getter
    @NoArgsConstructor
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Meta {

        @JsonProperty("total_count")
        private Integer totalCount;

        @JsonProperty("pageable_count")
        private Integer pageableCount;

        @JsonProperty("is_end")
        private Boolean isEnd;
    }

    @Getter
    @NoArgsConstructor
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Document {

        @JsonProperty("address_name")
        private String addressName;

        @JsonProperty("address_type")
        private String addressType; // REGION(지명) | ROAD(도로명) | REGION_ADDR(지번) | ROAD_ADDR(도로명 주소)

        @JsonProperty("x")
        private String x; // 경도

        @JsonProperty("y")
        private String y; // 위도

        @JsonProperty("address")
        private Address address;

        @JsonProperty("road_address")
        private RoadAddress roadAddress;
    }

    @Getter
    @NoArgsConstructor
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Address {

        @JsonProperty("address_name")
        private String addressName;

        @JsonProperty("region_1depth_name")
        private String region1depthName; // 시도

        @JsonProperty("region_2depth_name")
        private String region2depthName; // 시군구

        @JsonProperty("region_3depth_name")
        private String region3depthName; // 읍면동

        @JsonProperty("mountain_yn")
        private String mountainYn;

        @JsonProperty("main_address_no")
        private String mainAddressNo;

        @JsonProperty("sub_address_no")
        private String subAddressNo;

        @JsonProperty("zip_code")
        private String zipCode;

        @JsonProperty("x")
        private String x; // 경도

        @JsonProperty("y")
        private String y; // 위도
    }

    @Getter
    @NoArgsConstructor
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class RoadAddress {

        @JsonProperty("address_name")
        private String addressName;

        @JsonProperty("region_1depth_name")
        private String region1depthName; // 시도

        @JsonProperty("region_2depth_name")
        private String region2depthName; // 시군구

        @JsonProperty("region_3depth_name")
        private String region3depthName; // 읍면동

        @JsonProperty("road_name")
        private String roadName;

        @JsonProperty("underground_yn")
        private String undergroundYn;

        @JsonProperty("main_building_no")
        private String mainBuildingNo;

        @JsonProperty("sub_building_no")
        private String subBuildingNo;

        @JsonProperty("building_name")
        private String buildingName;

        @JsonProperty("zone_no")
        private String zoneNo;

        @JsonProperty("x")
        private String x; // 경도

        @JsonProperty("y")
        private String y; // 위도
    }
}

