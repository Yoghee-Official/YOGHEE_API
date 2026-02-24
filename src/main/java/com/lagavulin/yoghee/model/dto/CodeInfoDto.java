package com.lagavulin.yoghee.model.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
@Schema(description = "ID/Name 공통 응답 DTO")
public class CodeInfoDto {

    @Schema(description = "ID")
    private String id;

    @Schema(description = "이름")
    private String name;
}

