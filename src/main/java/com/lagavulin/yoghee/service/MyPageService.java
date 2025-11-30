package com.lagavulin.yoghee.service;

import com.lagavulin.yoghee.entity.UserLicense;
import com.lagavulin.yoghee.exception.BusinessException;
import com.lagavulin.yoghee.exception.ErrorCode;
import com.lagavulin.yoghee.repository.LicenseRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MyPageService {

    private final LicenseRepository licenseRepository;

    public void saveUserLicense(String userUuid, String imageUrl) {
        if (userUuid == null || userUuid.isEmpty()) {
            throw new BusinessException(ErrorCode.INVALID_REQUEST, "사용자 계정 정보를 알 수 없습니다.");
        }

        if (imageUrl == null || !imageUrl.contains("/license")) {
            throw new BusinessException(ErrorCode.INVALID_REQUEST, "이미지 URL이 유효하지 않습니다.");
        }

        licenseRepository.save(UserLicense.builder()
                                          .userUuid(userUuid)
                                          .imageUrl(imageUrl)
                                          .status("U")
                                          .build());
    }
}
