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
import lombok.Setter;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "CENTER_AMENITY")
@IdClass(YogaCenterAmenity.PK.class)
public class YogaCenterAmenity {

    @Id
    @Column(name = "CENTER_ID")
    private String centerId;

    @Id
    @Column(name = "AMENITY_ID")
    private String amenityId;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class PK implements Serializable {

        private String centerId;
        private String amenityId;
    }
}
