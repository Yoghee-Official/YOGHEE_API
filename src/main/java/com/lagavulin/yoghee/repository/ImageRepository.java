package com.lagavulin.yoghee.repository;

import java.util.List;

import com.lagavulin.yoghee.entity.Image;
import com.lagavulin.yoghee.model.enums.TargetType;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ImageRepository extends JpaRepository<Image, String> {

    // 이미지 엔티티에서 TYPE과 TARGET_ID 목록으로 TARGET_ID, ORDER_NO 기준으로 조회
    List<Image> findByTypeAndTargetIdInOrderByTargetIdAscOrderNoAsc(TargetType type, List<String> targetIds);

}
