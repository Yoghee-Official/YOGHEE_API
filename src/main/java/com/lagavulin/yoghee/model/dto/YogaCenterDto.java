package com.lagavulin.yoghee.model.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class YogaCenterDto {

    private String centerId;
    private String address;
    private String name;
    private String thumbnail;
    private Number favoriteCount;
    private Boolean isFavorite;
}
