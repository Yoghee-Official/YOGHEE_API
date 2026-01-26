package com.lagavulin.yoghee.service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import com.lagavulin.yoghee.entity.AppUser;
import com.lagavulin.yoghee.entity.UserLicense;
import com.lagavulin.yoghee.util.LicenseTokenUtil;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

@Slf4j
@Service
@RequiredArgsConstructor
public class EmailService {

    private final JavaMailSender mailSender;
    private final TemplateEngine templateEngine;
    private final LicenseTokenUtil licenseTokenUtil;
    
    @Value("${spring.mail.username}")
    private String adminEmail;

    @Value("${yoghee.domain:http://localhost:3333}")
    private String domain;

    /**
     * 관리자에게 자격증 승인 요청 이메일 발송
     */
    public void sendLicenseApprovalRequestToAdmin(UserLicense license, AppUser user) {
        try {
            // 보안 토큰 생성 (licenseUuid + 만료시간 + 서명)
            String secureToken = licenseTokenUtil.generateToken(license.getLicenseUuid());

            Context context = new Context();
            context.setVariable("userUuid", user.getUuid());
            context.setVariable("userEmail", user.getEmail());
            context.setVariable("userName", user.getName());
            context.setVariable("licenseUuid", license.getLicenseUuid());
            context.setVariable("secureToken", secureToken); // 보안 토큰 추가
            context.setVariable("imageUrl", license.getImageUrl());
            context.setVariable("submittedAt", LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
            context.setVariable("domain", domain); // 절대 URL 생성을 위한 domain 추가

            String htmlContent = templateEngine.process("license-approval-email", context);

            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setFrom(adminEmail);
            helper.setTo(adminEmail);
            helper.setSubject("[Yoghee] 새로운 자격증 승인 요청 - " + user.getName());
            helper.setText(htmlContent, true);

            mailSender.send(message);
            log.info("자격증 승인 요청 이메일 발송 완료: licenseUuid={}, adminEmail={}", license.getLicenseUuid(), adminEmail);

        } catch (MessagingException e) {
            log.error("자격증 승인 요청 이메일 발송 실패: {}", e.getMessage(), e);
            throw new RuntimeException("이메일 발송에 실패했습니다.", e);
        }
    }

    /**
     * 사용자에게 자격증 승인 완료 이메일 발송
     */
    public void sendLicenseApprovedEmail(UserLicense license, AppUser user) {
        try {
            String subject = "[Yoghee] 자격증 인증이 승인되었습니다 🎉";

            // 자격증 타입 이름 결정
            String licenseTypeName;
            if (license.getLicenseType() != null) {
                if (license.getLicenseType() == com.lagavulin.yoghee.model.enums.LicenseType.OTHER
                    && license.getCustomLicenseTypeName() != null) {
                    licenseTypeName = license.getCustomLicenseTypeName();
                } else {
                    licenseTypeName = license.getLicenseType().getDescription();
                }
            } else {
                licenseTypeName = "기타";
            }

            String content = String.format("""
                    <html>
                    <body style="font-family: 'Malgun Gothic', sans-serif; padding: 20px;">
                        <div style="max-width: 600px; margin: 0 auto; background-color: #ffffff; border-radius: 10px; box-shadow: 0 2px 10px rgba(0,0,0,0.1);">
                            <div style="background: linear-gradient(135deg, #667eea 0%%, #764ba2 100%%); color: white; padding: 30px; text-align: center; border-radius: 10px 10px 0 0;">
                                <h1>✅ 자격증 인증 승인</h1>
                            </div>
                            <div style="padding: 30px;">
                                <h2>%s님, 축하합니다!</h2>
                                <p>신청하신 자격증 인증이 승인되었습니다.</p>
                                <div style="background-color: #e8f5e9; padding: 15px; border-radius: 5px; margin: 20px 0;">
                                    <h3>승인된 자격증 정보</h3>
                                    <p><strong>자격증 타입:</strong> %s</p>
                                    <p><strong>승인 일시:</strong> %s</p>
                                </div>
                                <p>이제 요기니 강사로 활동하실 수 있습니다!</p>
                                <div style="text-align: center; margin-top: 30px;">
                                    <a href="%s" style="display: inline-block; padding: 12px 30px; background-color: #667eea; color: white; text-decoration: none; border-radius: 5px; font-weight: bold;">
                                        마이페이지에서 확인하기
                                    </a>
                                </div>
                            </div>
                            <div style="background-color: #f5f5f5; padding: 20px; text-align: center; font-size: 12px; color: #666; border-radius: 0 0 10px 10px;">
                                <p>© 2024 Yoghee. All rights reserved.</p>
                            </div>
                        </div>
                    </body>
                    </html>
                    """,
                user.getName(),
                licenseTypeName,
                LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")),
                domain + "/api/my"
            );

            sendHtmlEmail(user.getEmail(), subject, content);
            log.info("자격증 승인 완료 이메일 발송: userEmail={}", user.getEmail());

        } catch (Exception e) {
            log.error("자격증 승인 완료 이메일 발송 실패: {}", e.getMessage(), e);
        }
    }

    /**
     * 사용자에게 자격증 거절 이메일 발송
     */
    public void sendLicenseRejectedEmail(UserLicense license, AppUser user) {
        try {
            String subject = "[Yoghee] 자격증 인증이 반려되었습니다";
            String rejectReasonText = license.getRejectReason() != null
                ? license.getRejectReason().getDescription()
                : "사유 없음";

            String content = String.format("""
                    <html>
                    <body style="font-family: 'Malgun Gothic', sans-serif; padding: 20px;">
                        <div style="max-width: 600px; margin: 0 auto; background-color: #ffffff; border-radius: 10px; box-shadow: 0 2px 10px rgba(0,0,0,0.1);">
                            <div style="background: linear-gradient(135deg, #dc3545 0%%, #c82333 100%%); color: white; padding: 30px; text-align: center; border-radius: 10px 10px 0 0;">
                                <h1>❌ 자격증 인증 반려</h1>
                            </div>
                            <div style="padding: 30px;">
                                <h2>%s님, 안녕하세요</h2>
                                <p>아쉽게도 신청하신 자격증 인증이 반려되었습니다.</p>
                                <div style="background-color: #ffebee; padding: 15px; border-radius: 5px; margin: 20px 0;">
                                    <h3>반려 사유</h3>
                                    <p><strong>사유:</strong> %s</p>
                                    %s
                                </div>
                                <p>다시 확인 후 재신청 부탁드립니다.</p>
                                <div style="text-align: center; margin-top: 30px;">
                                    <a href="%s" style="display: inline-block; padding: 12px 30px; background-color: #667eea; color: white; text-decoration: none; border-radius: 5px; font-weight: bold;">
                                        다시 신청하기
                                    </a>
                                </div>
                            </div>
                            <div style="background-color: #f5f5f5; padding: 20px; text-align: center; font-size: 12px; color: #666; border-radius: 0 0 10px 10px;">
                                <p>© 2024 Yoghee. All rights reserved.</p>
                            </div>
                        </div>
                    </body>
                    </html>
                    """,
                user.getName(),
                rejectReasonText,
                license.getRejectDetail() != null ? "<p><strong>상세 사유:</strong> " + license.getRejectDetail() + "</p>" : "",
                domain + "/api/my"
            );

            sendHtmlEmail(user.getEmail(), subject, content);
            log.info("자격증 거절 이메일 발송: userEmail={}", user.getEmail());

        } catch (Exception e) {
            log.error("자격증 거절 이메일 발송 실패: {}", e.getMessage(), e);
        }
    }

    /**
     * HTML 이메일 발송
     */
    private void sendHtmlEmail(String to, String subject, String htmlContent) throws MessagingException {
        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

        helper.setFrom(adminEmail);
        helper.setTo(to);
        helper.setSubject(subject);
        helper.setText(htmlContent, true);

        mailSender.send(message);
    }
}

