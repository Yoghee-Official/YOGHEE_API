package com.lagavulin.yoghee.model.dto;

import java.util.Arrays;
import java.util.Date;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class YogaClassScheduleDto {

    @Schema(description = "클래스 아이디", example = "d1594-49853")
    private String classId;

    @Schema(description = "클래스명", example = "정환이와 함께하는 요가 클래스")
    private String className;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd", timezone = "Asia/Seoul")
    @Schema(description = "일자", example = "2025-05-28")
    private Date day;

    @Schema(description = "요일", example = "0(일요일), 1(월요일) ... 6(토요일)")
    private Long dayOfWeek;

    @Schema(description = "클래스 등록 이미지 URL", example = "https://image1_url")
    private String thumbnailUrl;

    @Schema(description = "요가원 주소 (시 구 동까지 노출)", example = "서울시 강남구 역삼동")
    private String address;

    @Schema(description = "참석 인원 수", example = "23")
    private Long attendance;

    @Schema(description = "지난 날짜 여부", example = "true / false")
    private Boolean isPast;

    @Schema(description = "카테고리 목록", example = "[\"빈야사\", \"하타\"]")
    private List<String> categories;

    public YogaClassScheduleDto(String classId, String className, Date day, Long dayOfWeek,
        String thumbnailUrl, String address, Long attendance) {
        this.classId = classId;
        this.className = className;
        this.day = day;
        this.dayOfWeek = dayOfWeek;
        this.thumbnailUrl = thumbnailUrl;
        this.address = address;
        this.attendance = attendance;
        this.isPast = day.before(new Date());
    }

    public YogaClassScheduleDto(String classId, String className, Date day, Long dayOfWeek,
        String thumbnailUrl, String address, Long attendance, String categoriesStr) {
        this.classId = classId;
        this.className = className;
        this.day = day;
        this.dayOfWeek = dayOfWeek;
        this.thumbnailUrl = thumbnailUrl;
        this.address = address;
        this.attendance = attendance;
        this.isPast = day.before(new Date());
        this.categories = (categoriesStr != null && !categoriesStr.isEmpty())
            ? Arrays.asList(categoriesStr.split(", "))
            : List.of();
    }
}
