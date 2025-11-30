package com.lagavulin.yoghee.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
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
@Table(name = "LICENSE")
public class UserLicense {

    @Id
    @Column(name = "LICENSE_UUID")
    @GeneratedValue(strategy = GenerationType.UUID)
    private String licenseUuid;

    @Column(name = "USER_UUID")
    private String userUuid;

    @Column(name = "IMAGE_URL")
    private String imageUrl;

    @Column(name = "STATUS")
    private String status; // UPLOADED, VERIFIED, REJECTED
}
