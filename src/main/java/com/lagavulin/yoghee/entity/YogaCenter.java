package com.lagavulin.yoghee.entity;

import java.util.Date;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "CENTER")
public class YogaCenter {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "CENTER_ID")
    private String centerId;

    private String name;

    // 주소 참조 (기존 주소 컬럼들 대신)
    @Column(name = "ADDRESS_ID")
    private String addressId;

    @OneToOne
    @JoinColumn(name = "ADDRESS_ID", insertable = false, updatable = false)
    private YogaCenterAddress address;

    @Column(name = "PHONE_NO")
    private String phoneNo;

    @Column(name = "DESC")
    private String description;

    private String thumbnail;

    @Column(name = "MASTER_ID")
    private String masterId;

    @Column(name = "CREATED_AT")
    private Date createdAt;
}