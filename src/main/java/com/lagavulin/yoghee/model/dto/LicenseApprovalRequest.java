package com.lagavulin.yoghee.model.dto;

import com.lagavulin.yoghee.model.enums.LicenseType;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "자격증 승인 요청 DTO")
public class LicenseApprovalRequest {

    @Schema(description = "자격증 UUID", example = "550e8400-e29b-41d4-a716-446655440000")
    private String licenseUuid;

    @Schema(description = "자격증 타입", example = "YOGA_ALLIANCE_RYT200")
    private LicenseType licenseType;

    @Schema(description = "자격증 타입 직접 입력 (기타 선택 시)", example = "국제필라테스자격증")
    private String customLicenseTypeName;
}

