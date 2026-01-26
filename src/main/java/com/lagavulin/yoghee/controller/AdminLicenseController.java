package com.lagavulin.yoghee.controller;

import com.lagavulin.yoghee.model.dto.LicenseApprovalRequest;
import com.lagavulin.yoghee.model.dto.LicenseRejectionRequest;
import com.lagavulin.yoghee.service.LicenseAdminService;
import com.lagavulin.yoghee.util.LicenseTokenUtil;
import com.lagavulin.yoghee.util.ResponseUtil;
import io.swagger.v3.oas.annotations.Hidden;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/admin/license")
@RequiredArgsConstructor
@Hidden
@Tag(name = "Admin - License", description = "관리자 자격증 승인/거절 API")
public class AdminLicenseController {

    private final LicenseAdminService licenseAdminService;
    private final LicenseTokenUtil licenseTokenUtil;

    @PostMapping("/approve")
    @Operation(
        summary = "자격증 승인 API",
        description = "관리자가 자격증을 승인하고 타입을 지정합니다. 이메일로도 호출 가능합니다.",
        responses = {
            @ApiResponse(
                responseCode = "200",
                description = "자격증 승인 완료",
                content = @Content(mediaType = "application/json",
                    schema = @Schema(example = """
                        {
                            "code": 200,
                            "status": "success",
                            "data": "자격증 승인이 완료되었습니다."
                        }
                        """)
                )
            )
        }
    )
    public ResponseEntity<?> approveLicense(
        @Parameter(description = "보안 토큰 (이메일에서 전송됨)", required = true)
        @RequestParam String token,

        @Parameter(description = "자격증 타입", required = true)
        @RequestParam String licenseType,

        @Parameter(description = "자격증 타입 직접 입력 (기타 선택 시)")
        @RequestParam(required = false) String licenseTypeCustom) {

        // 토큰 검증 및 licenseUuid 추출
        String licenseUuid;
        try {
            licenseUuid = licenseTokenUtil.validateTokenAndGetLicenseUuid(token);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest()
                                 .body("<html><body style='font-family: Arial; text-align: center; padding: 50px;'>" +
                                     "<h1 style='color: #dc3545;'>❌ 오류</h1>" +
                                     "<p>" + e.getMessage() + "</p>" +
                                     "<p>토큰이 만료되었거나 유효하지 않습니다.</p>" +
                                     "<a href='javascript:window.close()' style='display: inline-block; margin-top: 20px; padding: 10px 20px; background-color: #667eea; color: white; text-decoration: none; border-radius: 5px;'>창 닫기</a>"
                                     +
                                     "</body></html>");
        }

        LicenseApprovalRequest request = new LicenseApprovalRequest();
        request.setLicenseUuid(licenseUuid);

        // OTHER 선택 시 사용자 입력값 사용
        if ("OTHER".equals(licenseType) && licenseTypeCustom != null && !licenseTypeCustom.trim().isEmpty()) {
            request.setLicenseType(com.lagavulin.yoghee.model.enums.LicenseType.OTHER);
            request.setCustomLicenseTypeName(licenseTypeCustom.trim());
        } else {
            request.setLicenseType(com.lagavulin.yoghee.model.enums.LicenseType.valueOf(licenseType));
        }

        licenseAdminService.approveLicense(request);

        return ResponseEntity.ok()
                             .body("<html><body style='font-family: Arial; text-align: center; padding: 50px;'>" +
                                 "<h1 style='color: #28a745;'>✅ 승인 완료</h1>" +
                                 "<p>자격증이 성공적으로 승인되었습니다.</p>" +
                                 "<p>사용자에게 승인 완료 이메일이 발송되었습니다.</p>" +
                                 "<a href='javascript:window.close()' style='display: inline-block; margin-top: 20px; padding: 10px 20px; background-color: #667eea; color: white; text-decoration: none; border-radius: 5px;'>창 닫기</a>"
                                 +
                                 "</body></html>");
    }

    @PostMapping("/reject")
    @Operation(
        summary = "자격증 거절 API",
        description = "관리자가 자격증을 거절하고 사유를 지정합니다. 이메일로도 호출 가능합니다.",
        responses = {
            @ApiResponse(
                responseCode = "200",
                description = "자격증 거절 완료",
                content = @Content(mediaType = "application/json",
                    schema = @Schema(example = """
                        {
                            "code": 200,
                            "status": "success",
                            "data": "자격증 거절이 완료되었습니다."
                        }
                        """)
                )
            )
        }
    )
    public ResponseEntity<?> rejectLicense(
        @Parameter(description = "보안 토큰 (이메일에서 전송됨)", required = true)
        @RequestParam String token,

        @Parameter(description = "거절 사유", required = true)
        @RequestParam String rejectReason,

        @Parameter(description = "거절 상세 사유")
        @RequestParam(required = false) String rejectDetail) {

        // 토큰 검증 및 licenseUuid 추출
        String licenseUuid;
        try {
            licenseUuid = licenseTokenUtil.validateTokenAndGetLicenseUuid(token);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest()
                                 .body("<html><body style='font-family: Arial; text-align: center; padding: 50px;'>" +
                                     "<h1 style='color: #dc3545;'>❌ 오류</h1>" +
                                     "<p>" + e.getMessage() + "</p>" +
                                     "<p>토큰이 만료되었거나 유효하지 않습니다.</p>" +
                                     "<a href='javascript:window.close()' style='display: inline-block; margin-top: 20px; padding: 10px 20px; background-color: #667eea; color: white; text-decoration: none; border-radius: 5px;'>창 닫기</a>"
                                     +
                                     "</body></html>");
        }

        LicenseRejectionRequest request = new LicenseRejectionRequest();
        request.setLicenseUuid(licenseUuid);
        request.setRejectReason(com.lagavulin.yoghee.model.enums.LicenseRejectReason.valueOf(rejectReason));
        request.setRejectDetail(rejectDetail);

        licenseAdminService.rejectLicense(request);

        return ResponseEntity.ok()
                             .body("<html><body style='font-family: Arial; text-align: center; padding: 50px;'>" +
                                 "<h1 style='color: #dc3545;'>❌ 거절 완료</h1>" +
                                 "<p>자격증이 거절되었습니다.</p>" +
                                 "<p>사용자에게 거절 사유 이메일이 발송되었습니다.</p>" +
                                 "<a href='javascript:window.close()' style='display: inline-block; margin-top: 20px; padding: 10px 20px; background-color: #667eea; color: white; text-decoration: none; border-radius: 5px;'>창 닫기</a>"
                                 +
                                 "</body></html>");
    }

    @PostMapping("/approve-api")
    @Operation(
        summary = "자격증 승인 API (JSON)",
        description = "관리자가 자격증을 승인하고 타입을 지정합니다. (REST API용)",
        requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
            description = "승인 요청 정보",
            required = true,
            content = @Content(schema = @Schema(implementation = LicenseApprovalRequest.class))
        )
    )
    public ResponseEntity<?> approveLicenseApi(@RequestBody LicenseApprovalRequest request) {
        licenseAdminService.approveLicense(request);
        return ResponseUtil.success("자격증 승인이 완료되었습니다.");
    }

    @PostMapping("/reject-api")
    @Operation(
        summary = "자격증 거절 API (JSON)",
        description = "관리자가 자격증을 거절하고 사유를 지정합니다. (REST API용)",
        requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
            description = "거절 요청 정보",
            required = true,
            content = @Content(schema = @Schema(implementation = LicenseRejectionRequest.class))
        )
    )
    public ResponseEntity<?> rejectLicenseApi(@RequestBody LicenseRejectionRequest request) {
        licenseAdminService.rejectLicense(request);
        return ResponseUtil.success("자격증 거절이 완료되었습니다.");
    }
}

