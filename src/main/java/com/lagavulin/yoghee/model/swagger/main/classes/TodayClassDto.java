package com.lagavulin.yoghee.model.swagger.main.classes;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TodayClassDto {
    private String classId;
    private String className;
    private String type;
    private String address;
    private String scheduleId;
    private String startTime;
    private String endTime;
}
