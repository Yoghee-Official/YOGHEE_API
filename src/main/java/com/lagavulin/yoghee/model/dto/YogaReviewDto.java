package com.lagavulin.yoghee.model.dto;

import java.util.Date;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class YogaReviewDto {
    private String reviewId;
    private String userUuid;
    private String thumbnail;
    private String content;
    private Number rating;
    private Date createdAt;
}
