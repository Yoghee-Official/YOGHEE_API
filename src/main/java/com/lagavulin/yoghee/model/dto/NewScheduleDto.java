package com.lagavulin.yoghee.model.dto;

import java.time.LocalTime;
import java.util.Date;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonFormat;
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
public class NewScheduleDto {

    @Schema(description = "스케쥴 ID (수정시에만 입력)", example = "schedule-1234abcd")
    private String scheduleId;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd", timezone = "Asia/Seoul")
    @Schema(description = "스케쥴 날짜 리스트", example = "[\"2026-02-08\", \"2026-02-15\"]", requiredMode = Schema.RequiredMode.REQUIRED)
    private List<Date> dates;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "HH:mm", timezone = "Asia/Seoul")
    @Schema(description = "시작 시간", example = "09:30", requiredMode = Schema.RequiredMode.REQUIRED)
    private LocalTime startTime;

    @Schema(description = "최소 수강 인원", example = "3", requiredMode = Schema.RequiredMode.REQUIRED)
    private Long minCapacity;

    @Schema(description = "최대 수강 인원", example = "10", requiredMode = Schema.RequiredMode.REQUIRED)
    private Long maxCapacity;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "HH:mm", timezone = "Asia/Seoul")
    @Schema(description = "종료 시간", example = "10:30", requiredMode = Schema.RequiredMode.REQUIRED)
    private LocalTime endTime;

    @Schema(description = "스케쥴 이름", example = "아침 요가 스트레칭", requiredMode = Schema.RequiredMode.REQUIRED)
    private String name;

    @Schema(description = "카테고리 ID 리스트", example = "[\"1\", \"3\"]")
    private List<String> categoryIds;
}
