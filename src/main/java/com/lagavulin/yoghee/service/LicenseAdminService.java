package com.lagavulin.yoghee.service;

import com.lagavulin.yoghee.entity.AppUser;
import com.lagavulin.yoghee.entity.UserLicense;
import com.lagavulin.yoghee.exception.BusinessException;
import com.lagavulin.yoghee.exception.ErrorCode;
import com.lagavulin.yoghee.model.dto.LicenseApprovalRequest;
import com.lagavulin.yoghee.model.dto.LicenseRejectionRequest;
import com.lagavulin.yoghee.repository.AppUserRepository;
import com.lagavulin.yoghee.repository.LicenseRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class LicenseAdminService {

    private final LicenseRepository licenseRepository;
    private final AppUserRepository appUserRepository;
    private final EmailService emailService;

    /**
     * 자격증 승인
     */
    @Transactional
    public void approveLicense(LicenseApprovalRequest request) {
        // 자격증 조회
        UserLicense license = licenseRepository.findById(request.getLicenseUuid())
                                               .orElseThrow(() -> new BusinessException(ErrorCode.RESOURCE_NOT_FOUND, "자격증을 찾을 수 없습니다."));

        // 이미 처리된 자격증인지 확인
        if (!"U".equals(license.getStatus())) {
            throw new BusinessException(ErrorCode.INVALID_REQUEST, "이미 처리된 자격증입니다.");
        }

        // 자격증 승인 처리
        license.setStatus("A"); // Approved
        license.setLicenseType(request.getLicenseType());

        // OTHER 타입인 경우 사용자 입력값 저장
        if (request.getLicenseType() == com.lagavulin.yoghee.model.enums.LicenseType.OTHER) {
            license.setCustomLicenseTypeName(request.getCustomLicenseTypeName());
        }

        licenseRepository.save(license);

        log.info("자격증 승인 완료: licenseUuid={}, type={}, customType={}",
            license.getLicenseUuid(),
            request.getLicenseType(),
            request.getCustomLicenseTypeName());

        // 사용자 조회 및 이메일 발송
        AppUser user = appUserRepository.findById(license.getUserUuid())
                                        .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));

        emailService.sendLicenseApprovedEmail(license, user);
    }

    /**
     * 자격증 거절
     */
    @Transactional
    public void rejectLicense(LicenseRejectionRequest request) {
        // 자격증 조회
        UserLicense license = licenseRepository.findById(request.getLicenseUuid())
                                               .orElseThrow(() -> new BusinessException(ErrorCode.RESOURCE_NOT_FOUND, "자격증을 찾을 수 없습니다."));

        // 이미 처리된 자격증인지 확인
        if (!"U".equals(license.getStatus())) {
            throw new BusinessException(ErrorCode.INVALID_REQUEST, "이미 처리된 자격증입니다.");
        }

        // 자격증 거절 처리
        license.setStatus("R"); // Rejected
        license.setRejectReason(request.getRejectReason());
        license.setRejectDetail(request.getRejectDetail());
        licenseRepository.save(license);

        log.info("자격증 거절 완료: licenseUuid={}, reason={}", license.getLicenseUuid(), request.getRejectReason());

        // 사용자 조회 및 이메일 발송
        AppUser user = appUserRepository.findById(license.getUserUuid())
                                        .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));

        emailService.sendLicenseRejectedEmail(license, user);
    }
}

