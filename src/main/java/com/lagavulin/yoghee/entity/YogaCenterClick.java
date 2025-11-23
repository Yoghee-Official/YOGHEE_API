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
@IdClass(YogaCenterClick.PK.class)
@Table(name = "CENTER_CLICK")
public class YogaCenterClick {

    @Id
    @Column(name = "CENTER_ID")
    private String centerId;

    @Id
    @Column(name = "USER_UUID")
    private String userUuid;

    @Id
    @Column(name = "CLICK_DTS")
    private LocalDate clickDts;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class PK implements Serializable {
        private String centerId;
        private String userUuid;
        private LocalDate clickDts;
    }
}
