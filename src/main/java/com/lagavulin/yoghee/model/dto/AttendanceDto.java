package com.lagavulin.yoghee.model.dto;

import java.util.Date;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "참석자 정보 DTO")
public class AttendanceDto {

    @Schema(description = "참석 ID", example = "attendance-uuid-001")
    private String attendanceId;

    @Schema(description = "사용자 UUID", example = "user-uuid-001")
    private String userUuid;

    @Schema(description = "사용자 이름", example = "김정환")
    private String userName;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd", timezone = "Asia/Seoul")
    @Schema(description = "스케줄 날짜", example = "2026-02-01")
    private Date scheduleDate;

    @Schema(description = "스케줄 시작 시간", example = "09:00:00")
    private String startTime;

    @Schema(description = "스케줄 종료 시간", example = "10:30:00")
    private String endTime;

    @Schema(description = "신청 인원 수", example = "3")
    private Integer attendeeCount;

    @Schema(description = "참석 상태", example = "REGISTERED")
    private String status;
}

