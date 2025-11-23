package com.lagavulin.yoghee.entity;

import java.util.Date;

import com.lagavulin.yoghee.model.enums.SsoType;
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
import org.hibernate.annotations.CreationTimestamp;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "APP_USER")
public class AppUser {
    @Id
    @Column(name = "USER_UUID")
    @GeneratedValue(strategy = jakarta.persistence.GenerationType.UUID)
    private String uuid;

    @Column(name = "USER_ID")
    private String userId;
    private String name;
    private String nickname;

    @Column(name = "PHONE_NO")
    private String phoneNo;
    private String email;
    private String password;
    private String sso;
    @Column(name = "SSO_TYPE")
    @Enumerated(EnumType.STRING)
    private SsoType ssoType;
    private Long point;

    @Column(name = "CREATED_AT")
    @CreationTimestamp
    private Date createdAt;
}
