package com.lagavulin.yoghee.repository;

import com.lagavulin.yoghee.entity.UserLicense;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface LicenseRepository extends JpaRepository<UserLicense, String> {
    
}
