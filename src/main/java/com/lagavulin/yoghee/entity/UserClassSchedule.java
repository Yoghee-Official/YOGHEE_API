package com.lagavulin.yoghee.entity;

import java.util.Date;

import com.lagavulin.yoghee.model.enums.AttendanceStatus;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "USER_CLASS_SCHEDULE")
public class UserClassSchedule {

    @Id
    @Column(name = "ATTENDANCE_ID")
    @GeneratedValue(strategy = jakarta.persistence.GenerationType.UUID)
    private String attendanceId;

    @Column(name = "SCHEDULE_ID", nullable = false)
    private String scheduleId;

    @Column(name = "USER_UUID", nullable = false)
    private String userUuid;

    @Column(name = "SPECIFIC_DATE")
    private Date specificDate;
    
    // 참석 상태 (기본: REGISTERED)
    @Enumerated(EnumType.STRING)
    @Column(name = "STATUS", nullable = false)
    private AttendanceStatus status = AttendanceStatus.REGISTERED;

    @Column(name = "DURATION_MINUTES")
    private Integer durationMinutes;
}
