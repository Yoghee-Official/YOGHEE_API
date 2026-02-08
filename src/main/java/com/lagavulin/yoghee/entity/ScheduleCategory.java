package com.lagavulin.yoghee.entity;

import java.io.Serializable;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.IdClass;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "SCHEDULE_CATEGORY")
@IdClass(ScheduleCategory.PK.class)
public class ScheduleCategory {

    @Id
    @Column(name = "SCHEDULE_ID")
    private String scheduleId;

    @Id
    @Column(name = "CATEGORY_ID")
    private String categoryId;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class PK implements Serializable {

        private String scheduleId;
        private String categoryId;
    }
}

