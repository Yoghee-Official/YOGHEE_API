package com.lagavulin.yoghee.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class PresignedFileModel {
    private String fileName;
    private String contentType;

    private Integer width;
    private Integer height;
    private Long fileSize;
    @Setter
    private String imageKey;
    @Setter
    private String presignedUrl;
}
