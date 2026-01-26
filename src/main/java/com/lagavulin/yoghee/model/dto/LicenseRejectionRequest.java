package com.lagavulin.yoghee.model.dto;

import com.lagavulin.yoghee.model.enums.LicenseRejectReason;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "자격증 거절 요청 DTO")
public class LicenseRejectionRequest {

    @Schema(description = "자격증 UUID", example = "550e8400-e29b-41d4-a716-446655440000")
    private String licenseUuid;

    @Schema(description = "거절 사유", example = "UNCLEAR_IMAGE")
    private LicenseRejectReason rejectReason;

    @Schema(description = "거절 상세 사유 (OTHER 선택 시 필수)", example = "이미지 파일이 손상되어 확인이 불가능합니다.")
    private String rejectDetail;
}

