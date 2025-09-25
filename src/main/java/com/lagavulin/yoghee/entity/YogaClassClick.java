package com.lagavulin.yoghee.entity;

import java.io.Serializable;
import java.time.LocalDate;

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
@IdClass(YogaClassClick.PK.class)
@Table(name = "CLASS_CLICK")
public class YogaClassClick {

    @Id
    @Column(name = "CLASS_ID")
    private String classId;

    @Id
    @Column(name = "USER_UUID")
    private String userUuid;

    @Id
    @Column(name = "TYPE")
    private String type;

    @Id
    @Column(name = "CLICK_DTS")
    private LocalDate clickDts;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class PK implements Serializable {
        private String classId;
        private String userUuid;
        private String type;
        private LocalDate clickDts;
    }
}
