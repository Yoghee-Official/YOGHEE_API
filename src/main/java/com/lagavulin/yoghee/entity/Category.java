package com.lagavulin.yoghee.entity;

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
@Table(name = "CATEGORY")
public class Category {
    @Id
    @Column(name = "CATEGORY_ID")
    private String categoryId;

    @Column(name = "NAME")
    private String name;

    @Column(name = "DESC")
    private String description;

    @Column(name = "MAIN_DISPLAY")
    private String mainDisplay;

    @Column(name = "TYPE")
    private String type;

    public Category(String categoryId, String name, String description){
        this.categoryId = categoryId;
        this.name = name;
        this.description = description;
    }
}
