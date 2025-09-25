package com.lagavulin.yoghee.repository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.Date;
import java.util.List;

import com.lagavulin.yoghee.entity.YogaClass;
import com.lagavulin.yoghee.model.dto.TodayClassDto;
import com.lagavulin.yoghee.model.dto.YogaClassDto;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface YogaClassRepository extends JpaRepository<YogaClass, String> {
    @Query(value = """
        SELECT new com.lagavulin.yoghee.model.dto.TodayClassDto(
                c.classId, c.name, c.type, c.address, s.scheduleId, s.startTime, s.endTime)
        FROM YogaClass c
        INNER JOIN YogaClassMember m ON c.classId = m.classId
        INNER JOIN YogaClassSchedule s ON c.classId = s.classId
        WHERE m.userUuid = :userUuid
          AND (s.specificDate = CURDATE() OR s.dayOfWeek = FUNCTION('DAYOFWEEK', CURRENT_DATE))
        """)
    List<TodayClassDto> findTodayClassesByUser(@Param("userUuid") String userUuid);

    List<YogaClass> findAllByClassIdIn(Collection<String> classIds);

    @Query(value = """
        SELECT DISTINCT c
        FROM YogaClass c
        JOIN YogaClassCategory yc ON c.classId = yc.classId
        WHERE yc.categoryId IN :categoryIds
            AND c.type = :type
        ORDER BY RAND()
    """)
    List<YogaClass> findRandomByCategoryIds(String type, List<String> categoryIds, Pageable pageable);

    @Query(value = """
        SELECT c
        FROM YogaClass c
        WHERE c.mainDisplay = 'Y'
          AND c.type = :type
        ORDER BY RAND()
    """)
    List<YogaClass> findAllByMainDisplay(String type, Pageable pageable);

    @Query(value = """
        SELECT m.classId
        FROM YogaClassMember m
        JOIN YogaClass c on m.classId = c.classId
        WHERE c.type = :type
        AND m.createdAt >= :startDate
        GROUP BY m.classId
        ORDER BY COUNT(m.userUuid) DESC
    """)
    List<String> findNewSignUpTopNClassSinceStartDate(String type, @Param("startDate") LocalDateTime startDate, Pageable pageable);

    @Query(value = """
        SELECT new com.lagavulin.yoghee.model.dto.YogaClassDto(
            c.classId, c.name, c.thumbnail, c.masterId, AVG(r.rating), COUNT(r.reviewId))
        FROM YogaClass c
        LEFT JOIN YogaClassReview r ON c.classId = r.classId
        LEFT JOIN YogaClassMember m ON c.classId = m.classId
        WHERE c.classId IN :classIds
        GROUP BY c.classId
    """)
    List<YogaClassDto> findWithReviewStatsByClassIds(Collection<String> classIds);

    @Query(value = """
        SELECT c.classId
        FROM YogaClass c
        WHERE c.type = :type
        ORDER BY RAND()
    """)
    List<String> findRandomNClassByType(String type, Pageable pageable);
}
