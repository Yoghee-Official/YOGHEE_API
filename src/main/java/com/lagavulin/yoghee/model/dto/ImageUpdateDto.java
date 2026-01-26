package com.lagavulin.yoghee.model.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "이미지 업데이트 요청 DTO")
public class ImageUpdateDto {

    @Schema(description = "Presign API에서 받은 imageKey", example = "profile/images/550e8400-e29b-41d4-a716-446655440000_profile.jpg", requiredMode = Schema.RequiredMode.REQUIRED)
    private String imageKey;
}