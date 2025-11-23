package com.lagavulin.yoghee.model.swagger.main.center;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CenterDto {
    private String centerId;
    private String address;
    private String name;
    private String thumbnail;
    private Number favoriteCount;
}
