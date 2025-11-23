package com.lagavulin.yoghee.model.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
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
@JsonInclude(JsonInclude.Include.NON_NULL)
public class YogaClassDto {
    private String classId;
    private String className;
    private String type;
    private String address;
    private String description;
    private String thumbnail;
    private String masterId;
    private String masterName;
    private Number review;
    private Number price;
    private Long newMember;
    private Long capacity;
    private Number rating;
    private Double latitude;
    private Double longitude;
    private String scheduleId;
    private String startTime;
    private String endTime;

    public YogaClassDto(String classId, String classname, String description, String thumbnail){
        this.classId = classId;
        this.className = classname;
        this.description = description;
        this.thumbnail = thumbnail;
    }

    public YogaClassDto(String classId, String className, String type, String address, String scheduleId, String startTime, String endTime){
        this.classId = classId;
        this.className = className;
        this.type = type;
        this.address = address;
        this.scheduleId = scheduleId;
        this.startTime = startTime;
        this.endTime = endTime;
    }

    public YogaClassDto(String classId, String className, String thumbnail, String masterId, String masterName, Number rating, Number review) {
        this.classId = classId;
        this.className = className;
        this.thumbnail = thumbnail;
        this.masterId = masterId;
        this.masterName = masterName;
        this.rating = rating;
        this.review = review;
    }

    public YogaClassDto(String classId, String className, String thumbnail, String masterId, String masterName, Number price, Number rating, Number review) {
        this.classId = classId;
        this.className = className;
        this.thumbnail = thumbnail;
        this.masterId = masterId;
        this.masterName = masterName;
        this.price = price;
        this.rating = rating;
        this.review = review;
    }
}
