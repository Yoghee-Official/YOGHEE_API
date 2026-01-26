package com.lagavulin.yoghee.model.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter

@AllArgsConstructor
public class CategoryCountDto {

    private String categoryName;
    private Long count;
}