package com.lagavulin.yoghee.entity;

import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * 클래스 할인/예약안내/환불 정책 (CLASS 와 1:1, 공유 PK) TABLE: CLASS_POLICY
 */
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "CLASS_POLICY")
public class ClassPolicy {

    /**
     * CLASS_ID를 PK 겸 FK로 사용 (1:1 공유 PK 패턴)
     */
    @Id
    @Column(name = "CLASS_ID")
    private String classId;

    @OneToOne(fetch = FetchType.LAZY)
    @MapsId
    @JoinColumn(name = "CLASS_ID")
    private YogaClass yogaClass;

    /**
     * 할인 가격 (원) - discountRate와 둘 중 하나만 사용
     */
    @Column(name = "DISCOUNT_PRICE")
    private Long discountPrice;

    /**
     * 할인율 (%) - discountPrice와 둘 중 하나만 사용
     */
    @Column(name = "DISCOUNT_RATE")
    private Long discountRate;

    /**
     * 예약 시 안내사항 (준비물, 복장, 주의사항 등 자유 텍스트)
     */
    @Column(name = "RESERVATION_NOTE", columnDefinition = "TEXT")
    private String reservationNote;

    /**
     * 환불 정책 목록 /** 환불 정책 목록 (hoursBeforeClass 오름차순)
     */
    @Builder.Default
    @OneToMany(mappedBy = "classPolicy", cascade = CascadeType.ALL, orphanRemoval = true)
    @OrderBy("hoursBefore ASC")
    private List<ClassRefund> refundPolicies = new ArrayList<>();
}
