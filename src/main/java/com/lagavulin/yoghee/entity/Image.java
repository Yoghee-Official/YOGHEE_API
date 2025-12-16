package com.lagavulin.yoghee.entity;

import com.lagavulin.yoghee.model.enums.TargetType;
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

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "IMAGE")
public class Image {

    @Id
    @Column(name = "IMAGE_UUID")
    @GeneratedValue(strategy = jakarta.persistence.GenerationType.UUID)
    private String imageUuid;

    @Column(name = "TYPE")
    @Enumerated(EnumType.STRING)
    private TargetType type; // CENTER, CLASS

    @Column(name = "TARGET_ID")
    private String targetId;

    @Column(name = "URL")
    private String url;

    @Column(name = "ORDER_NO")
    private Integer orderNo;
}
