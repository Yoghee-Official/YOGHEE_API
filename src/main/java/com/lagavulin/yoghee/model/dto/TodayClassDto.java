package com.lagavulin.yoghee.model.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class TodayClassDto {
    private String classId;
    private String className;
    private String type;
    private String address;
    private String scheduleId;
    private String startTime;
    private String endTime;
}
