package com.lagavulin.yoghee.repository;

import java.util.List;

import com.lagavulin.yoghee.entity.YogaCenterAmenity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface YogaCenterAmenityRepository extends JpaRepository<YogaCenterAmenity, YogaCenterAmenity.PK> {

    @Modifying
    @Query("DELETE FROM YogaCenterAmenity ya WHERE ya.centerId = :centerId")
    void deleteByCenterId(String centerId);

    @Query("SELECT ya.amenityId FROM YogaCenterAmenity ya WHERE ya.centerId = :centerId")
    List<String> findAmenityIdsByCenterId(String centerId);
}
