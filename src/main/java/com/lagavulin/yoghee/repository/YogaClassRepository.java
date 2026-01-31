package com.lagavulin.yoghee.repository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.Date;
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
                c.classId,
                c.name,
                c.thumbnail,
                c.masterId,
                u.nickname,
                COALESCE(c.price, 0),
                ROUND(COALESCE(AVG(r.rating), 0.0),2),
                COUNT(DISTINCT r.reviewId),
                CASE WHEN uf.userUuid IS NOT NULL THEN true ELSE false END
            )
            FROM YogaClass c
            LEFT JOIN YogaClassReview r ON c.classId = r.classId
            LEFT JOIN UserFavorite uf ON uf.id = c.classId
                    AND uf.type = 'CLASS'
                    AND uf.userUuid = :userUuid
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
                c.classId,
                c.name,
                c.address,
                c.thumbnail,
                c.masterId,
                u.nickname,
                ROUND(COALESCE(AVG(r.rating), 0.0),2),
                COUNT(DISTINCT r.reviewId),
                COALESCE(c.price, 0),
                COUNT(DISTINCT alluf.userUuid),
                CASE WHEN myuf.userUuid IS NOT NULL THEN true ELSE false END
            )
            FROM YogaClass c
            JOIN YogaClassCategory yc ON c.classId = yc.classId
            LEFT JOIN YogaClassMember m ON c.classId = m.classId
            INNER JOIN AppUser u ON c.masterId = u.uuid
            LEFT JOIN YogaClassReview r ON c.classId = r.classId
            LEFT JOIN UserFavorite myuf ON myuf.id = c.classId
                    AND myuf.type = 'CLASS'
                    AND myuf.userUuid = :userUuid
            LEFT JOIN UserFavorite alluf ON alluf.id = c.classId
                    AND alluf.type = 'CLASS'
            WHERE yc.categoryId = :categoryId
                AND c.type = :type
            GROUP BY c.classId, c.name, c.thumbnail, c.masterId, u.nickname, c.price
            ORDER BY COUNT(DISTINCT m.userUuid) DESC
        """)
    List<CategoryClassDto> findMostJoinedClassByTypeAndCategoryId(String type, String categoryId, String userUuid);

    /**
     * 리뷰 평점이 높은 클래스
     */
    @Query(value = """
            SELECT new com.lagavulin.yoghee.model.dto.CategoryClassDto(
                c.classId,
                c.name,
                c.address,
                c.thumbnail,
                c.masterId,
                u.nickname,
                ROUND(COALESCE(AVG(r.rating), 0.0),2),
                COUNT(DISTINCT r.reviewId),
                COALESCE(c.price, 0),
                COUNT(DISTINCT alluf.userUuid),
                CASE WHEN myuf.userUuid IS NOT NULL THEN true ELSE false END
            )
            FROM YogaClass c
            JOIN YogaClassCategory yc ON c.classId = yc.classId
            INNER JOIN AppUser u ON c.masterId = u.uuid
            LEFT JOIN YogaClassReview r ON c.classId = r.classId
            LEFT JOIN UserFavorite myuf ON myuf.id = c.classId
                    AND myuf.type = 'CLASS'
                    AND myuf.userUuid = :userUuid
            LEFT JOIN UserFavorite alluf ON alluf.id = c.classId
                    AND alluf.type = 'CLASS'
            LEFT JOIN Image i ON c.classId = i.targetId
                    AND i.type = 'CLASS'
            WHERE yc.categoryId = :categoryId
                AND c.type = :type
            GROUP BY c.classId
            ORDER BY COALESCE(AVG(r.rating), 0.0) DESC
        """)
    List<CategoryClassDto> findHighestRatedClassByTypeAndCategoryId(String type, String categoryId, String userUuid);

    /**
     * 최근에 생긴 클래스
     */
    @Query(value = """
            SELECT new com.lagavulin.yoghee.model.dto.CategoryClassDto(
                c.classId,
                c.name,
                c.address,
                c.thumbnail,
                c.masterId,
                u.nickname,
                ROUND(COALESCE(AVG(r.rating), 0.0),2),
                COUNT(DISTINCT r.reviewId),
                COALESCE(c.price, 0),
                COUNT(DISTINCT alluf.userUuid),
                CASE WHEN myuf.userUuid IS NOT NULL THEN true ELSE false END
            )
            FROM YogaClass c
            JOIN YogaClassCategory yc ON c.classId = yc.classId
            INNER JOIN AppUser u ON c.masterId = u.uuid
            LEFT JOIN YogaClassReview r ON c.classId = r.classId
            LEFT JOIN UserFavorite myuf ON myuf.id = c.classId
                    AND myuf.type = 'CLASS'
                    AND myuf.userUuid = :userUuid
            LEFT JOIN UserFavorite alluf ON alluf.id = c.classId
                    AND alluf.type = 'CLASS'
            WHERE yc.categoryId = :categoryId
                AND c.type = :type
            GROUP BY c.classId
            ORDER BY c.createdAt DESC
        """)
    List<CategoryClassDto> findRecentClassByTypeAndCategoryId(String type, String categoryId, String userUuid);

    @Query("""
        SELECT c.classId
        FROM YogaClassClick c
        WHERE c.clickDts >= :fromDate
             AND c.type = :type
        GROUP BY c.classId
        ORDER BY COUNT(c) DESC
        """)
    List<String> findTopClickedClasses(String type, @Param("fromDate") LocalDate fromDate, Pageable pageable);

    @Query(value = """
            SELECT new com.lagavulin.yoghee.model.dto.CategoryClassDto(
                c.classId,
                c.name,
                c.address,
                c.thumbnail,
                c.masterId,
                u.nickname,
                ROUND(COALESCE(AVG(r.rating), 0.0),2),
                COUNT(DISTINCT r.reviewId),
                COALESCE(c.price, 0),
                COUNT(DISTINCT alluf.userUuid),
                CASE WHEN myuf.userUuid IS NOT NULL THEN true ELSE false END
            )
            FROM YogaClass c
            JOIN YogaClassCategory yc ON c.classId = yc.classId
            INNER JOIN AppUser u ON c.masterId = u.uuid
            LEFT JOIN YogaClassReview r ON c.classId = r.classId
            LEFT JOIN UserFavorite myuf ON myuf.id = c.classId
                    AND myuf.type = 'CLASS'
                    AND myuf.userUuid = :userUuid
            LEFT JOIN UserFavorite alluf ON alluf.id = c.classId
                    AND alluf.type = 'CLASS'
            WHERE yc.categoryId = :categoryId AND c.type = :type
            GROUP BY c.classId, c.name, c.thumbnail, c.masterId, u.nickname, c.price
            ORDER BY COUNT(DISTINCT alluf.userUuid) DESC
        """)
    List<CategoryClassDto> findMostFavoritedClassByTypeAndCategoryId(String type, String categoryId, String userUuid);

    @Query(value = """
             SELECT new com.lagavulin.yoghee.model.dto.CategoryClassDto(
                c.classId,
                c.name,
                c.address,
                c.thumbnail,
                c.masterId,
                u.nickname,
                ROUND(COALESCE(AVG(r.rating), 0.0),2),
                COUNT(DISTINCT r.reviewId),
                COALESCE(c.price, 0),
                COUNT(DISTINCT alluf.userUuid),
                CASE WHEN myuf.userUuid IS NOT NULL THEN true ELSE false END
            )
            FROM YogaClass c
            JOIN YogaClassCategory yc ON c.classId = yc.classId
            INNER JOIN AppUser u ON c.masterId = u.uuid
            LEFT JOIN YogaClassReview r ON c.classId = r.classId
            LEFT JOIN UserFavorite myuf ON myuf.id = c.classId
                    AND myuf.type = 'CLASS'
                    AND myuf.userUuid = :userUuid
            LEFT JOIN UserFavorite alluf ON alluf.id = c.classId
                    AND alluf.type = 'CLASS'
            WHERE yc.categoryId = :categoryId AND c.type = :type
            GROUP BY c.classId, c.name, c.thumbnail, c.masterId, u.nickname, c.price
            ORDER BY c.price DESC
        """)
    List<CategoryClassDto> findMostExpensiveClassByTypeAndCategoryId(String type, String categoryId, String userUuid);

    @Query(value = """
            SELECT new com.lagavulin.yoghee.model.dto.CategoryClassDto(
                c.classId,
                c.name,
                c.address,
                c.thumbnail,
                c.masterId,
                u.nickname,
                ROUND(COALESCE(AVG(r.rating), 0.0),2),
                COUNT(DISTINCT r.reviewId),
                COALESCE(c.price, 0),
                COUNT(DISTINCT alluf.userUuid),
                CASE WHEN myuf.userUuid IS NOT NULL THEN true ELSE false END
            )
            FROM YogaClass c
            JOIN YogaClassCategory yc ON c.classId = yc.classId
            INNER JOIN AppUser u ON c.masterId = u.uuid
            LEFT JOIN YogaClassReview r ON c.classId = r.classId
            LEFT JOIN UserFavorite myuf ON myuf.id = c.classId
                    AND myuf.type = 'CLASS'
                    AND myuf.userUuid = :userUuid
            LEFT JOIN UserFavorite alluf ON alluf.id = c.classId
                    AND alluf.type = 'CLASS'
            WHERE yc.categoryId = :categoryId AND c.type = :type
            GROUP BY c.classId, c.name, c.thumbnail, c.masterId, u.nickname, c.price
            ORDER BY c.price ASC
        """)
    List<CategoryClassDto> findCheapestClassByTypeAndCategoryId(String type, String categoryId, String userUuid);

    @Query(value = """
            SELECT
                c.CLASS_ID,
                c.NAME,
                c.THUMBNAIL,
                c.MASTER_ID,
                u.NICKNAME,
                NULL,
                ROUND(COALESCE(AVG(r.RATING), 0.0), 2),
                COUNT(DISTINCT r.REVIEW_ID),
                (SELECT GROUP_CONCAT(DISTINCT cat.NAME ORDER BY cat.NAME SEPARATOR ', ')
                 FROM CLASS_CATEGORY cc
                 JOIN CATEGORY cat ON cc.CATEGORY_ID = cat.CATEGORY_ID
                 WHERE cc.CLASS_ID = c.CLASS_ID)
            FROM CLASS c
            INNER JOIN APP_USER u ON c.MASTER_ID = u.USER_UUID
            INNER JOIN USER_FAVORITE uf ON c.CLASS_ID = uf.ID AND uf.TYPE = 'CLASS'
            LEFT JOIN CLASS_REVIEW r ON c.CLASS_ID = r.CLASS_ID
            WHERE uf.USER_UUID = :userUuid
              AND uf.TYPE = 'CLASS'
            GROUP BY c.CLASS_ID, c.NAME, c.THUMBNAIL, c.MASTER_ID, u.NICKNAME
        """, nativeQuery = true)
    List<Object[]> findUserFavoriteClassesRaw(String userUuid);

    @Query(value = """
            SELECT
                c.CLASS_ID,
                c.NAME,
                c.THUMBNAIL,
                c.ADDRESS,
                COUNT(DISTINCT alluf.USER_UUID),
                (SELECT GROUP_CONCAT(DISTINCT cat.NAME ORDER BY cat.NAME SEPARATOR ', ')
                 FROM CLASS_CATEGORY cc
                 JOIN CATEGORY cat ON cc.CATEGORY_ID = cat.CATEGORY_ID
                 WHERE cc.CLASS_ID = c.CLASS_ID)
            FROM CLASS c
            INNER JOIN USER_FAVORITE uf ON c.CLASS_ID = uf.ID AND uf.TYPE = 'CLASS'
            LEFT JOIN USER_FAVORITE alluf ON alluf.ID = c.CLASS_ID AND alluf.TYPE = 'CLASS'
            WHERE uf.USER_UUID = :userUuid
              AND uf.TYPE = 'CLASS'
              AND c.TYPE = 'R'
            GROUP BY c.CLASS_ID, c.NAME, c.THUMBNAIL, c.ADDRESS
        """, nativeQuery = true)
    List<Object[]> findUserFavoriteRegularClassesRaw(String userUuid);

    // === 지도자 관련 쿼리 ===

    /**
     * 지도자가 개설한 모든 클래스 조회
     */
    List<YogaClass> findByMasterId(String masterId);

    /**
     * 지도자가 개설한 모든 클래스의 리뷰 수 합계
     */
    @Query(value = """
            SELECT COUNT(r.reviewId)
            FROM YogaClassReview r
            WHERE r.classId IN (SELECT c.classId FROM YogaClass c WHERE c.masterId = :masterId)
        """)
    Long countAllReviewsByMasterId(@Param("masterId") String masterId);

    /**
     * 지도자의 이번 달 가장 예약이 많은 카테고리 조회
     */
    @Query(value = """
            SELECT new com.lagavulin.yoghee.model.dto.CategoryCountDto(
                cat.name,
                COUNT(DISTINCT ucs.attendanceId)
            )
            FROM UserClassSchedule ucs
            JOIN YogaClassSchedule cs ON ucs.scheduleId = cs.scheduleId
            JOIN YogaClass c ON cs.classId = c.classId
            JOIN YogaClassCategory ycc ON c.classId = ycc.classId
            JOIN Category cat ON ycc.categoryId = cat.categoryId
            WHERE c.masterId = :masterId
              AND ucs.status = 'REGISTERED'
              AND cs.specificDate BETWEEN :startDate AND :endDate
            GROUP BY cat.categoryId, cat.name
            ORDER BY COUNT(DISTINCT ucs.attendanceId) DESC
        """)
    com.lagavulin.yoghee.model.dto.CategoryCountDto findMostReservedCategoryByMasterIdForPeriod(
        @Param("masterId") String masterId,
        @Param("startDate") Date startDate,
        @Param("endDate") Date endDate
    );

    /**
     * 지도자의 오늘 수업 조회 (Native Query)
     */
    @Query(value = """
            SELECT
                c.CLASS_ID,
                c.NAME,
                cs.SPECIFIC_DATE,
                cs.DAY_OF_WEEK,
                c.THUMBNAIL,
                c.ADDRESS,
                COALESCE(SUM(ucs.ATTENDEE_COUNT), 0),
                (SELECT GROUP_CONCAT(DISTINCT cat.NAME ORDER BY cat.NAME SEPARATOR ', ')
                 FROM CLASS_CATEGORY cc
                 JOIN CATEGORY cat ON cc.CATEGORY_ID = cat.CATEGORY_ID
                 WHERE cc.CLASS_ID = c.CLASS_ID)
            FROM CLASS c
            JOIN CLASS_SCHEDULE cs ON c.CLASS_ID = cs.CLASS_ID
            LEFT JOIN USER_CLASS_SCHEDULE ucs ON cs.SCHEDULE_ID = ucs.SCHEDULE_ID
            WHERE c.MASTER_ID = :masterId
              AND cs.SPECIFIC_DATE BETWEEN :todayStart AND :todayEnd
            GROUP BY c.CLASS_ID, c.NAME, cs.SPECIFIC_DATE, c.THUMBNAIL, c.ADDRESS
            ORDER BY cs.SPECIFIC_DATE, cs.START_TIME
        """, nativeQuery = true)
    List<Object[]> findTodayClassesByMasterIdRaw(
        @Param("masterId") String masterId,
        @Param("todayStart") Date todayStart,
        @Param("todayEnd") Date todayEnd
    );

    /**
     * 지도자의 스케줄 조회 (기간 범위) (Native Query)
     */
    @Query(value = """
            SELECT
                c.CLASS_ID,
                c.NAME,
                cs.SPECIFIC_DATE,
                DAYOFWEEK(cs.SPECIFIC_DATE) - 1,
                c.THUMBNAIL,
                c.ADDRESS,
                COALESCE(SUM(ucs.ATTENDEE_COUNT), 0),
                (SELECT GROUP_CONCAT(DISTINCT cat.NAME ORDER BY cat.NAME SEPARATOR ', ')
                 FROM CLASS_CATEGORY cc
                 JOIN CATEGORY cat ON cc.CATEGORY_ID = cat.CATEGORY_ID
                 WHERE cc.CLASS_ID = c.CLASS_ID)
            FROM CLASS c
            JOIN CLASS_SCHEDULE cs ON c.CLASS_ID = cs.CLASS_ID
            LEFT JOIN USER_CLASS_SCHEDULE ucs ON cs.SCHEDULE_ID = ucs.SCHEDULE_ID
            WHERE c.MASTER_ID = :masterId
              AND cs.SPECIFIC_DATE BETWEEN :startDate AND :endDate
            GROUP BY c.CLASS_ID, c.NAME, cs.SPECIFIC_DATE, c.THUMBNAIL, c.ADDRESS
            ORDER BY cs.SPECIFIC_DATE, cs.START_TIME
        """, nativeQuery = true)
    List<Object[]> findSchedulesByMasterIdBetweenDatesRaw(
        @Param("masterId") String masterId,
        @Param("startDate") Date startDate,
        @Param("endDate") Date endDate
    );
}
