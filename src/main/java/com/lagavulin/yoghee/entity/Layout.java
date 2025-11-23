package com.lagavulin.yoghee.entity;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.IdClass;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "LAYOUT")
@IdClass(Layout.PK.class)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Layout{
    @Id
    private String order;

    @Id
    private String type;

    private String key;
    private String text;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class PK implements Serializable {
        private String order;
        private String type;
    }
}