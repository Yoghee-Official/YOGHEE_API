package com.lagavulin.yoghee.model.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
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
@JsonIgnoreProperties(ignoreUnknown = true)
public class YogaClassDto {
    private String classId;
    private String className;
    private String type;
    private String address;
    private String description;
    private String thumbnail;
    private String masterId;
    private double rating;
    private long review;
    private long price;
    private long newMember;
    private long capacity;
    private double latitude;
    private double longitude;

    public YogaClassDto(String classId, String className, String thumbnail, String masterId, double rating, long review) {
        this.classId = classId;
        this.className = className;
        this.thumbnail = thumbnail;
        this.masterId = masterId;
        this.rating = rating;
        this.review = review;
    }
}
