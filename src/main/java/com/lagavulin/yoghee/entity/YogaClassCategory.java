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
@Table(name = "CLASS_CATEGORY")
@IdClass(YogaClassCategory.PK.class)
public class YogaClassCategory {

    @Id
    @Column(name = "CLASS_ID")
    private String classId;

    @Id
    @Column(name = "CATEGORY_ID")
    private String categoryId;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class PK implements Serializable {
        private String classId;
        private String categoryId;
    }
}
