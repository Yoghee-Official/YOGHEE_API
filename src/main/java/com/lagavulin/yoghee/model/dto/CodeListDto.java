package com.lagavulin.yoghee.model.dto;

import java.util.List;
import java.util.Map;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@Schema(description = "카테고리/특징/편의시설 코드 목록 응답 DTO")
public class CodeListDto {

    @Schema(description = "카테고리 목록 (type별 그룹핑)")
    private Map<String, List<CodeInfoDto>> categories;

    @Schema(description = "특징 목록")
    private List<CodeInfoDto> features;

    @Schema(description = "편의시설 목록 (type별 그룹핑)")
    private Map<String, List<CodeInfoDto>> amenities;
}

