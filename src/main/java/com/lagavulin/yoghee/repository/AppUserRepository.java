package com.lagavulin.yoghee.repository;

import com.lagavulin.yoghee.entity.AppUser;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AppUserRepository extends JpaRepository<AppUser, String> {

}
