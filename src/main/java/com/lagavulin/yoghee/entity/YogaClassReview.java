package com.lagavulin.yoghee.entity;

import java.util.Date;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "CLASS_REVIEW")
public class YogaClassReview {

    @Id
    @Column(name = "REVIEW_ID")
    private String reviewId;

    @Column(name = "CLASS_ID")
    private String classId;

    @Column(name = "USER_UUID")
    private String userUuid;

    private Double rating;

    private String content;

    private String thumbnail;

    @Column(name = "CREATED_AT")
    private Date createdAt;
}
