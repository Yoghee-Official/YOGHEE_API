package com.lagavulin.yoghee.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * 클래스 환불 정책 (CLASS_POLICY 와 N:1)
 * <p>
 * 예시: hoursBeforeClass=72, refundRate=100 → 수업 72시간 전까지 취소 시 전액 환불 hoursBeforeClass=24, refundRate=50  → 수업 24시간 전까지 취소 시 50% 환불 hoursBeforeClass=0,
 * refundRate=0   → 수업 당일 취소 시 환불 불가
 * <p>
 * TABLE: CLASS_REFUND_POLICY
 */
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "CLASS_REFUND")
public class ClassRefund {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "REFUND_ID")
    private String refundPolicyId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "CLASS_ID", nullable = false)
    private ClassPolicy classPolicy;

    /**
     * 수업 시작 몇 시간 전까지 취소 시 적용되는 규칙인지 (예: 72, 24)
     */
    @Column(name = "HOURS_BEFORE", nullable = false)
    private Integer hoursBefore;

    /**
     * 환불율 % (예: 100=전액환불, 50, 20, 0=환불불가)
     */
    @Column(name = "REFUND_RATE", nullable = false)
    private Integer refundRate;
}
