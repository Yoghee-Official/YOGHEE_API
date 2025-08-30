package com.lagavulin.yoghee.service.verfication;

import com.lagavulin.yoghee.exception.BusinessException;
import com.lagavulin.yoghee.exception.ErrorCode;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class EmailVerificationService extends VerificationService {

    private final JavaMailSender mailSender;

    public EmailVerificationService(StringRedisTemplate redisTemplate, JavaMailSender mailSender) {
        super(redisTemplate);
        this.mailSender = mailSender;
    }

    @Override
    public void sendVerificationCode(String to) {
        String code = makeVerificationCode();
        saveVerificationCode(to, code);

        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setTo(to);
            message.setSubject("[YOGHEE] 이메일 인증 코드");
            message.setText("인증번호는 [" + code + "] 입니다. 5분 이내에 입력해주세요.");

            mailSender.send(message);
        } catch (Exception e) {
            log.error("Failed to send email : {}", to, e);
            throw new BusinessException(ErrorCode.MAIL_SEND_FAIL);
        }
    }

    @Override
    protected String getPrefix() {
        return "EMAIL_";
    }
}
