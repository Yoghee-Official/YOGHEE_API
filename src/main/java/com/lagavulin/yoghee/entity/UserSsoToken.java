package com.lagavulin.yoghee.entity;

import java.util.Date;

import com.lagavulin.yoghee.model.enums.SsoType;
import jakarta.persistence.*;
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
@Table(name = "USER_SSO_TOKEN")
public class UserSsoToken {
    @Id
    @Column(name = "TOKEN_ID")
    @GeneratedValue(strategy = GenerationType.UUID)
    private String tokenId;

    @Column(name = "SSO_TYPE")
    @Enumerated(EnumType.STRING)
    private SsoType ssoType;

    @Column(name = "SSO_USER_ID")
    private String ssoUserId;   // ex) "123123123"

    @Column(name="ACCESS_TOKEN")
    private String accessToken;

    @Column(name="ACCESS_TOKEN_EXPIRES_AT")
    private Date accessTokenExpiresAt;

    @Column(name="REFRESH_TOKEN")
    private String refreshToken;

    @Column(name="REFRESH_TOKEN_EXPIRES_AT")
    private Date refreshTokenExpiresAt;

    @Column(name="CREATED_AT")
    @CreationTimestamp
    private Date createdAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "USER_ID")   // 내부 User 테이블과 연결
    private AppUser user;

}
