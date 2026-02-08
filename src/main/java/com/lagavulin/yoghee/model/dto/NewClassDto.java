package com.lagavulin.yoghee.model.dto;

import java.util.List;

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
public class NewClassDto {

    @Schema(description = "클래스 ID (수정시에만 입력)", example = "class-1234abcd")
    private String classId;

    @Schema(description = "클래스 이름", example = "아침 요가 스트레칭", requiredMode = Schema.RequiredMode.REQUIRED)
    private String name;

    @Schema(description = "클래스 설명", example = "하루를 상쾌하게 시작하는 아침 요가 스트레칭 클래스입니다.")
    private String description;

    @Schema(description = "요가원 주소 ID", example = "adf1234e-ab12-cd34-ef56-abcdef", requiredMode = Schema.RequiredMode.REQUIRED)
    private String addressId;

    @Schema(description = "특징ID (최대 3개)", example = "1 : 기본 수련 경험이 있고 흐름 있는 동작\n2 : 허리·골반 주변 이완 및 안정\n3 : 요가 입문자, 기본 동작과 호흡 설명 중심\n4 : 몸이 뻣뻣하거나 스트레칭 위주 수련\n5 : 중심 잡기, 안정성, 자세 정렬에 집중하는 수련\n6 : 호흡·이완 중심, 심리적 안정")
    private List<String> featuresId;

    @Schema(description = "스케쥴 정보")
    private List<NewScheduleDto> schedules;

    @Schema(description = "이미지 URL 리스트", example = "[\"https://example.com/image1.jpg\", \"https://example.com/image2.jpg\"]")
    private List<String> images;

    @Schema(description = "가격(원)", example = "25000", requiredMode = Schema.RequiredMode.REQUIRED)
    private Long price;
}
