package com.lagavulin.yoghee.repository;

import java.util.Date;
import java.util.List;

import com.lagavulin.yoghee.entity.UserClassSchedule;
import com.lagavulin.yoghee.model.dto.CategoryCountDto;
import com.lagavulin.yoghee.model.enums.AttendanceStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
public interface UserClassScheduleRepository extends JpaRepository<UserClassSchedule, String> {

    @Query("""
            SELECT COALESCE(COUNT(distinct ucs.attendanceId), 0)
            FROM UserClassSchedule ucs
            WHERE ucs.userUuid = :userUuid
            AND ucs.status = :status
        """)
    int countByUserUuidAndStatus(String userUuid, AttendanceStatus status);

    @Query("""
            SELECT COALESCE(SUM(ucs.durationMinutes), 0)
            FROM UserClassSchedule ucs
            WHERE ucs.userUuid = :userUuid
            AND ucs.status = :status
        """)
    int sumDurationMinutesByUserUuidAndStatus(String userUuid, AttendanceStatus status);

    @Query(value = """
            SELECT 
                c.CLASS_ID,
                c.NAME,
                ADDTIME(cs.SPECIFIC_DATE, cs.START_TIME),
                cs.DAY_OF_WEEK,
                c.THUMBNAIL,
                c.ADDRESS,
                (SELECT COALESCE(SUM(u.ATTENDEE_COUNT), 0)
                 FROM USER_CLASS_SCHEDULE u 
                 WHERE u.SCHEDULE_ID = cs.SCHEDULE_ID),
                (SELECT GROUP_CONCAT(DISTINCT cat.NAME ORDER BY cat.NAME SEPARATOR ', ')
                 FROM CLASS_CATEGORY cc
                 JOIN CATEGORY cat ON cc.CATEGORY_ID = cat.CATEGORY_ID
                 WHERE cc.CLASS_ID = c.CLASS_ID)
            FROM USER_CLASS_SCHEDULE ucs
            JOIN CLASS_SCHEDULE cs ON ucs.SCHEDULE_ID = cs.SCHEDULE_ID
            JOIN CLASS c ON cs.CLASS_ID = c.CLASS_ID
            WHERE ucs.USER_UUID = :userUuid
              AND cs.SPECIFIC_DATE BETWEEN :startDate AND :endDate
            ORDER BY DATE(cs.SPECIFIC_DATE), TIME(cs.START_TIME)
        """, nativeQuery = true)
    List<Object[]> findSchedulesBetweenDatesRaw(@Param("userUuid") String userUuid,
        @Param("startDate") Date startDate,
        @Param("endDate") Date endDate);

    // New: category counts for the month (ATTENDED only) - count schedules per category
    @Query("""
            SELECT new com.lagavulin.yoghee.model.dto.CategoryCountDto(
                cat.name,
                COUNT(ucs)
            )
            FROM UserClassSchedule ucs
            JOIN YogaClassSchedule cs ON ucs.scheduleId = cs.scheduleId
            JOIN YogaClass c ON cs.classId = c.classId
            JOIN YogaClassCategory ycc ON c.classId = ycc.classId
            JOIN Category cat ON ycc.categoryId = cat.categoryId
            WHERE ucs.userUuid = :userUuid
              AND ucs.status = 'ATTENDED'
              AND cs.specificDate BETWEEN :startDate AND :endDate
            GROUP BY cat.categoryId, cat.name
            ORDER BY COUNT(ucs) DESC
        """)
    List<CategoryCountDto> findTopCategoriesByUserForPeriod(@Param("userUuid") String userUuid,
        @Param("startDate") Date startDate,
        @Param("endDate") Date endDate);

    /**
     * 특정 스케줄의 참석자 목록 조회
     */
    @Query(value = """
            SELECT
                ucs.ATTENDANCE_ID,
                ucs.USER_UUID,
                u.NICKNAME,
                cs.SPECIFIC_DATE,
                cs.START_TIME,
                cs.END_TIME,
                ucs.ATTENDEE_COUNT,
                ucs.STATUS
            FROM USER_CLASS_SCHEDULE ucs
            JOIN CLASS_SCHEDULE cs ON ucs.SCHEDULE_ID = cs.SCHEDULE_ID
            JOIN APP_USER u ON ucs.USER_UUID = u.USER_UUID
            WHERE ucs.SCHEDULE_ID = :scheduleId
            ORDER BY ucs.CREATED_AT
        """, nativeQuery = true)
    List<Object[]> findAttendancesByScheduleIdRaw(@Param("scheduleId") String scheduleId);

    /**
     * 특정 스케줄의 모든 REGISTERED 상태 참석자를 ATTENDED로 변경
     */
    @Modifying
    @Transactional
    @Query("""
            UPDATE UserClassSchedule ucs
            SET ucs.status = :newStatus
            WHERE ucs.scheduleId = :scheduleId
            AND ucs.status = :currentStatus
        """)
    int updateStatusByScheduleId(
        @Param("scheduleId") String scheduleId,
        @Param("currentStatus") AttendanceStatus currentStatus,
        @Param("newStatus") AttendanceStatus newStatus
    );

    /**
     * 특정 스케줄의 REGISTERED 상태 참석자 수 조회
     */
    @Query("""
            SELECT COUNT(ucs)
            FROM UserClassSchedule ucs
            WHERE ucs.scheduleId = :scheduleId
            AND ucs.status = :status
        """)
    int countByScheduleIdAndStatus(
        @Param("scheduleId") String scheduleId,
        @Param("status") AttendanceStatus status
    );
}
