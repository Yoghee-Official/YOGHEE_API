package com.lagavulin.yoghee.model.dto;

import java.util.List;

import com.lagavulin.yoghee.model.PresignedFileModel;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PresignedFileDto {

    private String bucket;
    private List<PresignedFileModel> files;
}
