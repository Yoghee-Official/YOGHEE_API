package com.lagavulin.yoghee.model.swagger.main.classes;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ClassDto {
    private String classId;
    private String className;
    private String thumbnail;
    private String masterId;
    private String masterName;
    private Number rating;
    private Number review;
}
