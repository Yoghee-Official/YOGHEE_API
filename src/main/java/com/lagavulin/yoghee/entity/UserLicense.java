package com.lagavulin.yoghee.entity;

import java.util.Date;

import com.lagavulin.yoghee.model.enums.LicenseRejectReason;
import com.lagavulin.yoghee.model.enums.LicenseType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;

@Getter
@Setter
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
    private String status; // U(UPLOADED), A(APPROVED), R(REJECTED)

    @Enumerated(EnumType.STRING)
    @Column(name = "LICENSE_TYPE")
    private LicenseType licenseType;

    @Column(name = "LICENSE_TYPE_CUSTOM")
    private String customLicenseTypeName; // 기타 자격증 타입 직접 입력

    @Enumerated(EnumType.STRING)
    @Column(name = "REJECT_REASON")
    private LicenseRejectReason rejectReason;

    @Column(name = "REJECT_DETAIL")
    private String rejectDetail; // 기타 사유 상세 내용

    @Column(name = "CREATED_AT")
    @CreationTimestamp
    private Date createdAt;
}
