package com.lagavulin.yoghee.repository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;

import com.lagavulin.yoghee.entity.YogaClass;
import com.lagavulin.yoghee.model.dto.CategoryClassDto;
import com.lagavulin.yoghee.model.dto.YogaClassDto;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface YogaClassRepository extends JpaRepository<YogaClass, String> {

    @Query(value = """
        SELECT new com.lagavulin.yoghee.model.dto.YogaClassDto(
                c.classId, c.name, c.type, c.address, s.scheduleId, s.startTime, s.endTime)
        FROM YogaClass c
        INNER JOIN YogaClassMember m ON c.classId = m.classId
        INNER JOIN YogaClassSchedule s ON c.classId = s.classId
        WHERE m.userUuid = :userUuid
          AND (s.specificDate = CURDATE() OR s.dayOfWeek = FUNCTION('DAYOFWEEK', CURRENT_DATE))
        """)
    List<YogaClassDto> findTodayClassesByUser(@Param("userUuid") String userUuid);

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
            SELECT new com.lagavulin.yoghee.model.dto.YogaClassDto(
                c.classId, c.name,c.description, c.thumbnail)
            FROM YogaClass c
            WHERE c.mainDisplay = 'Y'
              AND c.type = :type
            ORDER BY RAND()
        """)
    List<YogaClassDto> findAllByMainDisplay(String type, Pageable pageable);

    @Query(value = """
            SELECT m.classId
            FROM YogaClassMember m
            JOIN YogaClass c on m.classId = c.classId
            WHERE c.type = :type
            AND m.createdAt >= :startDate
            GROUP BY m.classId
            ORDER BY COUNT(m.userUuid) DESC
        """)
    List<String> findNewSignUpTopNClassSinceStartDate(String type, LocalDateTime startDate, Pageable pageable);

    @Query(value = """
            SELECT new com.lagavulin.yoghee.model.dto.YogaClassDto(
                c.classId, c.name, c.thumbnail, c.masterId, u.nickname, COALESCE(c.price, 0), ROUND(COALESCE(AVG(r.rating), 0.0),2), COUNT(DISTINCT r.reviewId), CASE WHEN uf.userUuid IS NOT NULL THEN true ELSE false END)
            FROM YogaClass c
            LEFT JOIN YogaClassReview r ON c.classId = r.classId
            LEFT JOIN UserFavorite uf ON uf.id = c.classId AND uf.type = 'CLASS' AND (:userUuid IS NULL OR uf.userUuid = :userUuid)
            LEFT JOIN AppUser u ON c.masterId = u.uuid
            WHERE c.classId IN :classIds
            GROUP BY c.classId
        """)
    List<YogaClassDto> findWithReviewStatsByClassIds(Collection<String> classIds, String userUuid);

    @Query(value = """
            SELECT c.classId
            FROM YogaClass c
            WHERE c.type = :type
            ORDER BY RAND()
        """)
    List<String> findRandomNClassByType(String type, Pageable pageable);

    /**
     * 가장 참여자가 많은 클래스
     */
    @Query(value = """
            SELECT new com.lagavulin.yoghee.model.dto.CategoryClassDto(
                c.classId, c.name, c.thumbnail, c.masterId, u.nickname, ROUND(COALESCE(AVG(r.rating), 0.0),2), COUNT(DISTINCT r.reviewId), COALESCE(c.price, 0))
            FROM YogaClass c
            JOIN YogaClassCategory yc ON c.classId = yc.classId
            LEFT JOIN YogaClassMember m ON c.classId = m.classId
            INNER JOIN AppUser u ON c.masterId = u.uuid
            LEFT JOIN YogaClassReview r ON c.classId = r.classId
            WHERE yc.categoryId = :categoryId
                AND c.type = :type
            GROUP BY c.classId, c.name, c.thumbnail, c.masterId, u.nickname, c.price
            ORDER BY COUNT(DISTINCT m.userUuid) DESC
        """)
    List<CategoryClassDto> findMostJoinedClassByTypeAndCategoryId(String type, String categoryId);

    /**
     * 리뷰 평점이 높은 클래스
     */
    @Query(value = """
            SELECT new com.lagavulin.yoghee.model.dto.CategoryClassDto(
                c.classId, c.name, c.thumbnail, c.masterId, u.nickname, ROUND(COALESCE(AVG(r.rating), 0.0),2), COUNT(DISTINCT r.reviewId), COALESCE(c.price, 0))
            FROM YogaClass c
            JOIN YogaClassCategory yc ON c.classId = yc.classId
            INNER JOIN AppUser u ON c.masterId = u.uuid
            LEFT JOIN YogaClassReview r ON c.classId = r.classId
            WHERE yc.categoryId = :categoryId
                AND c.type = :type
            GROUP BY c.classId
            ORDER BY COALESCE(AVG(r.rating), 0.0) DESC
        """)
    List<CategoryClassDto> findHighestRatedClassByTypeAndCategoryId(String type, String categoryId);

    /**
     * 최근에 생긴 클래스
     */
    @Query(value = """
            SELECT new com.lagavulin.yoghee.model.dto.CategoryClassDto(
                c.classId, c.name, c.thumbnail, c.masterId, u.nickname, ROUND(COALESCE(AVG(r.rating), 0.0),2), COUNT(DISTINCT r.reviewId), COALESCE(c.price, 0))
            FROM YogaClass c
            JOIN YogaClassCategory yc ON c.classId = yc.classId
            INNER JOIN AppUser u ON c.masterId = u.uuid
            LEFT JOIN YogaClassReview r ON c.classId = r.classId
            WHERE yc.categoryId = :categoryId
                AND c.type = :type
            GROUP BY c.classId
            ORDER BY c.createdAt DESC
        """)
    List<CategoryClassDto> findRecentClassByTypeAndCategoryId(String type, String categoryId);

    @Query("""
        SELECT c.classId
        FROM YogaClassClick c
        WHERE c.clickDts >= :fromDate
             AND c.type = :type
        GROUP BY c.classId
        ORDER BY COUNT(c) DESC
        """)
    List<String> findTopClickedClasses(String type, @Param("fromDate") LocalDate fromDate, Pageable pageable);

    @Query("""
        SELECT new com.lagavulin.yoghee.model.dto.CategoryClassDto(
                      c.classId, c.name, c.thumbnail, c.masterId, u.nickname, ROUND(COALESCE(AVG(r.rating), 0.0),2), COUNT(DISTINCT r.reviewId), COALESCE(c.price, 0))
        FROM YogaClass c
        JOIN YogaClassCategory yc ON c.classId = yc.classId
        INNER JOIN AppUser u ON c.masterId = u.uuid
        LEFT JOIN YogaClassReview r ON c.classId = r.classId
        LEFT JOIN UserFavorite uf ON c.classId = uf.id AND uf.type = 'CLASS'
        WHERE yc.categoryId = :categoryId AND c.type = :type
        GROUP BY c.classId, c.name, c.thumbnail, c.masterId, u.nickname, c.price
        ORDER BY COUNT(DISTINCT uf.userUuid) DESC
        """)
    List<CategoryClassDto> findMostFavoritedClassByTypeAndCategoryId(String type, String categoryId);

    @Query(value = """
            SELECT new com.lagavulin.yoghee.model.dto.CategoryClassDto(
                c.classId, c.name, c.thumbnail, c.masterId, u.nickname, ROUND(COALESCE(AVG(r.rating), 0.0),2), COUNT(DISTINCT r.reviewId), COALESCE(c.price, 0))
            FROM YogaClass c
            JOIN YogaClassCategory yc ON c.classId = yc.classId
            INNER JOIN AppUser u ON c.masterId = u.uuid
            LEFT JOIN YogaClassReview r ON c.classId = r.classId
            WHERE yc.categoryId = :categoryId AND c.type = :type
            GROUP BY c.classId, c.name, c.thumbnail, c.masterId, u.nickname, c.price
            ORDER BY c.price DESC
        """)
    List<CategoryClassDto> findMostExpensiveClassByTypeAndCategoryId(String type, String categoryId);

    @Query(value = """
            SELECT new com.lagavulin.yoghee.model.dto.CategoryClassDto(
                c.classId, c.name, c.thumbnail, c.masterId, u.nickname, ROUND(COALESCE(AVG(r.rating), 0.0),2), COUNT(DISTINCT r.reviewId), COALESCE(c.price, 0))
            FROM YogaClass c
            JOIN YogaClassCategory yc ON c.classId = yc.classId
            INNER JOIN AppUser u ON c.masterId = u.uuid
            LEFT JOIN YogaClassReview r ON c.classId = r.classId
            WHERE yc.categoryId = :categoryId AND c.type = :type
            GROUP BY c.classId, c.name, c.thumbnail, c.masterId, u.nickname, c.price
            ORDER BY c.price ASC
        """)
    List<CategoryClassDto> findCheapestClassByTypeAndCategoryId(String type, String categoryId);
}