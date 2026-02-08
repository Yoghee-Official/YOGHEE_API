package com.lagavulin.yoghee.model.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CenterAddressDto {

    @Schema(description = "주소 ID (수정시에만 필요)", example = "adf1234e-ab12-cd34-ef56-abcdef")
    private String addressId;

    @Schema(description = "도로명 주소", example = "서울 강남구 테헤란로 212", requiredMode = Schema.RequiredMode.REQUIRED)
    private String roadAddress;

    @Schema(description = "지번 주소", example = "서울 강남구 역삼동 718-5", requiredMode = Schema.RequiredMode.REQUIRED)
    private String jibunAddress;

    @Schema(description = "우편번호", example = "06220", requiredMode = Schema.RequiredMode.REQUIRED)
    private String zonecode;

    @Schema(description = "상세 주소", example = "멀티캠퍼스 3층")
    private String addressDetail;

    @Schema(description = "수련 장소명", example = "정환요가원")
    private String name;

    private Double latitude;
    private Double longitude;
}

