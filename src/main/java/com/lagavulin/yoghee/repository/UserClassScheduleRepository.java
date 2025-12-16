package com.lagavulin.yoghee.repository;

import java.util.Date;
import java.util.List;

import com.lagavulin.yoghee.entity.UserClassSchedule;
import com.lagavulin.yoghee.model.dto.CategoryCountDto;
import com.lagavulin.yoghee.model.dto.YogaClassScheduleDto;
import com.lagavulin.yoghee.model.enums.AttendanceStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

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

    // Native query: upcoming schedules for the current month (after today, within same year-month)
    @Query(value = """
            SELECT new com.lagavulin.yoghee.model.dto.YogaClassScheduleDto(
                c.classId,
                c.name,
                cs.specificDate,
                cs.dayOfWeek,
                c.thumbnail,
                c.address,
                (SELECT COUNT(DISTINCT u.userUuid) FROM UserClassSchedule u WHERE u.scheduleId = cs.scheduleId)
            )
            FROM UserClassSchedule ucs
            JOIN YogaClassSchedule cs ON ucs.scheduleId = cs.scheduleId
            JOIN YogaClass c ON cs.classId = c.classId
            WHERE ucs.userUuid = :userUuid
              AND ucs.status = :status
              AND FUNCTION('date', cs.specificDate) > CURRENT_DATE
              AND FUNCTION('year', cs.specificDate) = FUNCTION('year', CURRENT_DATE)
              AND FUNCTION('month', cs.specificDate) = FUNCTION('month', CURRENT_DATE)
            ORDER BY cs.specificDate, cs.startTime
        """)
    List<YogaClassScheduleDto> findUpcomingMonthClassesByUserAndStatus(@Param("userUuid") String userUuid, @Param("status") AttendanceStatus status);

    @Query(value = """
            SELECT new com.lagavulin.yoghee.model.dto.YogaClassScheduleDto(
                c.classId,
                c.name,
                cs.specificDate,
                cs.dayOfWeek,
                c.thumbnail,
                c.address,
                (SELECT COUNT(DISTINCT u.userUuid) FROM UserClassSchedule u WHERE u.scheduleId = cs.scheduleId)
            )
            FROM UserClassSchedule ucs
            JOIN YogaClassSchedule cs ON ucs.scheduleId = cs.scheduleId
            JOIN YogaClass c ON cs.classId = c.classId
            WHERE ucs.userUuid = :userUuid
              AND cs.specificDate BETWEEN :startDate AND :endDate
            ORDER BY cs.specificDate, cs.startTime
        """)
    List<YogaClassScheduleDto> findSchedulesBetweenDates(@Param("userUuid") String userUuid,
        @Param("startDate") Date startDate,
        @Param("endDate") Date endDate);

    // New: category counts for the month (ATTENDED only) - count distinct scheduleId per category
    @Query("""
            SELECT new com.lagavulin.yoghee.model.dto.CategoryCountDto(
                cat.categoryId,
                cat.name,
                COUNT(DISTINCT ucs.scheduleId)
            )
            FROM UserClassSchedule ucs
            JOIN YogaClassSchedule cs ON ucs.scheduleId = cs.scheduleId
            JOIN YogaClass cl ON cs.classId = cl.classId
            JOIN cl.categories cat
            WHERE ucs.userUuid = :userUuid
              AND ucs.status = com.lagavulin.yoghee.model.enums.AttendanceStatus.ATTENDED
              AND cs.specificDate BETWEEN :startDate AND :endDate
            GROUP BY cat.categoryId, cat.name
            ORDER BY COUNT(DISTINCT ucs.scheduleId) DESC
        """)
    List<CategoryCountDto> findTopCategoriesByUserForPeriod(@Param("userUuid") String userUuid,
        @Param("startDate") Date startDate,
        @Param("endDate") Date endDate);
}
