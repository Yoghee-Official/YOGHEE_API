package com.lagavulin.yoghee.repository;

import com.lagavulin.yoghee.entity.ScheduleCategory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ScheduleCategoryRepository extends JpaRepository<ScheduleCategory, ScheduleCategory.PK> {

    /**
     * 특정 스케줄의 모든 카테고리 삭제
     */
    @Modifying
    @Query("DELETE FROM ScheduleCategory sc WHERE sc.scheduleId = :scheduleId")
    void deleteByScheduleId(@Param("scheduleId") String scheduleId);
}

