package com.lagavulin.yoghee.repository;

import com.lagavulin.yoghee.entity.UserLicense;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface LicenseRepository extends JpaRepository<UserLicense, String> {

    /**
     * 사용자의 승인된 자격증 중 가장 최근 것 조회
     */
    UserLicense findTopByUserUuidAndStatusOrderByCreatedAtDesc(String userUuid, String status);

    /**
     * 사용자의 모든 자격증 중 가장 최근 것 조회 (STATUS 상관없이)
     */
    UserLicense findTopByUserUuidOrderByCreatedAtDesc(String userUuid);
}
