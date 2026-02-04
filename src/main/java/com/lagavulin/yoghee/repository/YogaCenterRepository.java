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
                c.centerId,
                CONCAT(ca.depth1, ' ', ca.depth2, ' ', ca.depth3),
                c.name,
                c.thumbnail,
                COUNT(DISTINCT uf.userUuid),
                CASE
                    WHEN :userUuid IS NOT NULL THEN false
                    WHEN uf.userUuid IS NOT NULL THEN true
                    ELSE false
                END
            )
            FROM YogaCenter c
            LEFT JOIN YogaCenterAddress ca ON c.addressId = ca.addressId
            LEFT JOIN UserFavorite uf ON uf.id = c.centerId AND uf.type = 'CENTER' AND (:userUuid IS NULL OR uf.userUuid = :userUuid)
            WHERE c.centerId IN :centerIds
            GROUP BY c.centerId, ca.depth1, ca.depth2, ca.depth3, c.name, c.thumbnail, uf.userUuid
        """)
    List<YogaCenterDto> findYogaCenterWithFavoriteCount(Collection<String> centerIds, String userUuid);

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
                c.centerId,
                CONCAT(ca.depth1, ' ', ca.depth2, ' ', ca.depth3),
                c.name,
                c.thumbnail,
                COUNT(DISTINCT uf.userUuid),
                CASE WHEN :userUuid IS NULL THEN false
                     WHEN uf.userUuid IS NOT NULL THEN true
                     ELSE false
                END
            )
            FROM YogaCenter c
            LEFT JOIN YogaCenterAddress ca ON c.addressId = ca.addressId
            LEFT JOIN UserFavorite uf ON c.centerId = uf.id AND uf.type = 'CENTER'
            WHERE c.centerId IN :centerIds
            GROUP BY c.centerId, ca.depth1, ca.depth2, ca.depth3, c.name, c.thumbnail
        """)
    List<YogaCenterDto> findWithReviewStatsByCenterIds(Set<String> centerIds, String userUuid);

    @Query("""
        SELECT c.centerId
        FROM YogaCenterClick c
        WHERE c.clickDts >= :fromDate
        GROUP BY c.centerId
        ORDER BY COUNT(c) DESC
        """)
    List<String> findTopClickedClasses(LocalDate fromDate, PageRequest of);

    @Query("""
        SELECT new com.lagavulin.yoghee.model.dto.YogaCenterDto(
            c.centerId,
            CONCAT(COALESCE(ca.depth1, ''), ' ', COALESCE(ca.depth2, ''), ' ', COALESCE(ca.depth3, '')),
            c.name,
            c.thumbnail,
            COUNT(DISTINCT uf.userUuid),
            true
        )
        FROM YogaCenter c
        LEFT JOIN YogaCenterAddress ca ON c.addressId = ca.addressId
        JOIN UserFavorite uf
            ON uf.id = c.centerId
           AND uf.type = 'CENTER'
           AND uf.userUuid = :userUuid
        GROUP BY c.centerId, c.name, c.thumbnail, ca.depth1, ca.depth2, ca.depth3
        """)
    List<YogaCenterDto> findUserFavoriteCenterRaw(String userUuid);
}
