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
@Table(name = "USER_CATEGORY")
@IdClass(UserCategory.PK.class)
public class UserCategory {

    @Id
    @Column(name = "USER_UUID")
    private String userUuid;

    @Id
    @Column(name = "CATEGORY_ID")
    private String categoryId;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class PK implements Serializable {
        private String userUuid;
        private String categoryId;
    }
}
