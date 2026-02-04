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
@Schema(description = "지도자 페이지 응답 DTO")
public class UpdateLeaderIntroductionDto {

    @Schema(description = "지도자 소개글", example = "안녕하세요! 요가 마스터 오정환입니다. 항상 건강한 수련되도록 도와드리겠습니다.")
    private String introduction;

    @Schema(description = "지도자 경력 년수", example = "20")
    private Long career;
}
