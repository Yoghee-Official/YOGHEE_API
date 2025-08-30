package com.lagavulin.yoghee.repository;

import java.util.Optional;

import com.lagavulin.yoghee.entity.AppUser;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AppUserRepository extends JpaRepository<AppUser, String> {

    Optional<AppUser> findAppUserByEmail(String email);

    Optional<AppUser> findByUserId(String userId);

    Optional<AppUser> findAppUserByPhoneNo(String phoneNo);
}
