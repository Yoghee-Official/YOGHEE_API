package com.lagavulin.yoghee.repository;

import java.util.List;

import com.lagavulin.yoghee.entity.Amenity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface AmenityRepository extends JpaRepository<Amenity, String> {

    @Query(value = "SELECT a FROM Amenity a ORDER BY CAST(a.amenityId AS integer)")
    List<Amenity> findAllOrderByIdNumeric();
}

