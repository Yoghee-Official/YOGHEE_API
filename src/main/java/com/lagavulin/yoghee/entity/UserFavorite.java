package com.lagavulin.yoghee.entity;

import java.io.Serializable;
import java.util.Date;

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
@Table(name = "FAVORITE")
@IdClass(UserFavorite.PK.class)
public class UserFavorite {
    @Id
    @Column(name = "ID")
    private String id;

    @Id
    @Column(name = "TYPE")
    private String type; // CENTER, CLASS

    @Id
    @Column(name = "USER_UUID")
    private String userUuid;

    @Column(name = "CREATED_AT")
    private Date createdAt;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class PK implements Serializable {
        private String id;
        private String type;
        private String userUuid;
    }
}