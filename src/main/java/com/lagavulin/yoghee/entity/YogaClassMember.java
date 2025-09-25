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
@Table(name = "CLASS_MEMBER")
public class YogaClassMember {

    @Id
    @Column(name = "CLASS_ID")
    private String classId;

    @Id
    @Column(name = "USER_UUID")
    private String userUuid;

    @Column(name = "USER_NAME")
    private String userName;

    @Column(name = "TYPE")
    private String type; // INSTRUCTOR, STUDENT

    @Column(name = "CREATED_AT")
    private Date createdAt;
}
