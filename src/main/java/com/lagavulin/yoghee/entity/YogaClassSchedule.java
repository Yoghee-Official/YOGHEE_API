package com.lagavulin.yoghee.entity;

import java.time.LocalTime;
import java.util.Date;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
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
@Table(name = "CLASS_SCHEDULE")
public class YogaClassSchedule {

    @Id
    @Column(name = "SCHEDULE_ID")
    @GeneratedValue(strategy = jakarta.persistence.GenerationType.UUID)
    private String scheduleId;

    @Column(name = "CLASS_ID")
    private String classId;

    @Column(name = "DAY_OF_WEEK")
    private int dayOfWeek;

    @Column(name = "SPECIFIC_DATE")
    private Date specificDate;

    @Column(name = "START_TIME")
    private LocalTime startTime;

    @Column(name = "END_TIME")
    private LocalTime endTime;

    @Column(name = "CONTENT")
    private String content;
}
