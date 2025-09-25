package com.lagavulin.yoghee.model.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ClassClickCountDto {
    private String classId;
    private Long count;
}
