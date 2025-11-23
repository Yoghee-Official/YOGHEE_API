package com.lagavulin.yoghee.repository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;
import java.util.Set;

import com.lagavulin.yoghee.entity.YogaCenter;
import com.lagavulin.yoghee.model.dto.YogaCenterDto;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface YogaCenterRepository extends JpaRepository<YogaCenter, String> {
    @Query(value = """
        SELECT c.centerId
        FROM YogaClassMember m
        JOIN YogaClass c on m.classId = c.classId
        WHERE c.type = :type
        AND m.createdAt >= :startDate
        GROUP BY m.classId
        ORDER BY COUNT(m.userUuid) DESC
    """)
    List<String> findNewSignUpTopNClassSinceStartDate(String type, LocalDateTime startDate, Pageable pageable);

    @Query(value = """
        SELECT new com.lagavulin.yoghee.model.dto.YogaCenterDto(
            c.centerId, c.address, c.name, c.thumbnail, COUNT(DISTINCT f.userUuid))
        FROM YogaCenter c
        LEFT JOIN UserFavorite f ON c.centerId = f.id AND f.type = 'CENTER'
        WHERE c.centerId IN :centerIds
        GROUP BY c.centerId
    """)
    List<YogaCenterDto> findYogaCenterWithFavoriteCount(Collection<String> centerIds);

    @Query(value = """
        SELECT DISTINCT c
        FROM YogaCenter c
        JOIN YogaCenterCategory yc ON c.centerId = yc.centerId
        WHERE yc.categoryId IN :categoryIds
        ORDER BY RAND()
    """)
    List<YogaCenter> findRandomByCategoryIds(List<String> categoryIds, Pageable pageable);

    @Query(value = """
        SELECT c.centerId
        FROM YogaCenter c
        ORDER BY RAND()
    """)
    List<String> findRandomNCenter(PageRequest of);

    @Query(value = """
        SELECT new com.lagavulin.yoghee.model.dto.YogaCenterDto(
            c.centerId, c.address, c.name, c.thumbnail, COUNT(DISTINCT r.userUuid))
        FROM YogaCenter c
        LEFT JOIN UserFavorite r ON c.centerId = r.id AND r.type = "CENTER"
        GROUP BY c.centerId
    """)
    List<YogaCenterDto> findWithReviewStatsByCenterIds(Set<String> centerIds);

    @Query("""
        SELECT c.centerId
        FROM YogaCenterClick c
        WHERE c.clickDts >= :fromDate
        GROUP BY c.centerId
        ORDER BY COUNT(c) DESC
        """)
    List<String> findTopClickedClasses(LocalDate fromDate, PageRequest of);
}
