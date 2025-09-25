package com.lagavulin.yoghee.repository;

import java.util.List;

import com.lagavulin.yoghee.entity.UserCategory;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserCategoryRepository extends JpaRepository<UserCategory, String> {

    List<UserCategory> findAllByUserUuid(String userUuid);
}
