package com.lagavulin.yoghee.repository;

import java.time.LocalDate;
import java.util.List;

import com.lagavulin.yoghee.entity.YogaClassClick;
import com.lagavulin.yoghee.model.dto.ClassClickCountDto;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface YogaClassClickRepository extends JpaRepository<YogaClassClick, YogaClassClick.PK> {

    @Query("""
        SELECT new com.lagavulin.yoghee.model.dto.ClassClickCountDto( c.classId, COUNT(c))
        FROM YogaClassClick c
        WHERE c.clickDts >= :fromDate
             AND c.type = :type
        GROUP BY c.classId
        ORDER BY COUNT(c) DESC
        """)
    List<ClassClickCountDto> findTopClickedClasses(String type, @Param("fromDate") LocalDate fromDate, Pageable pageable);
}
