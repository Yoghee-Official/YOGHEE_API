package com.lagavulin.yoghee.repository;

import java.util.Optional;

import com.lagavulin.yoghee.entity.UserSsoToken;
import com.lagavulin.yoghee.model.enums.SsoType;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserSsoTokenRepository extends JpaRepository<UserSsoToken, String> {
    Optional<UserSsoToken> findBySsoTypeAndSsoUserId(SsoType ssoType, String ssoUserId);
}
