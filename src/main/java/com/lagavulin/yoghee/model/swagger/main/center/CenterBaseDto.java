package com.lagavulin.yoghee.model.swagger.main.center;

import java.util.Date;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CenterBaseDto {

    @Schema(description = "요가원 ID", example = "center-1234abcd")
    private String centerId;

    @Schema(description = "요가원 이름", example = "정환요가원")
    private String name;

    @Schema(description = "전체 주소", example = "경기 남양주시 다산중앙로123번길 22-26 899호")
    private String address;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd", timezone = "Asia/Seoul")
    @Schema(description = "생성일자", example = "2025-12-01")
    private Date createdAt;
}