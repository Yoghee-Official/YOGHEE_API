package com.lagavulin.yoghee.repository;

import com.lagavulin.yoghee.entity.YogaClassCategory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

public interface YogaClassCategoryRepository extends JpaRepository<YogaClassCategory, YogaClassCategory.PK> {

    @Modifying
    @Query("DELETE FROM YogaClassCategory yc WHERE yc.classId = :classId")
    void deleteByClassId(String classId);
}
