package com.lagavulin.yoghee.service.verfication;

import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

@Service
public class EmailVerificationService extends VerificationService {

    public EmailVerificationService(StringRedisTemplate redisTemplate) {
        super(redisTemplate);
    }

    @Override
    public void sendVerificationCode(String to) {

    }

    @Override
    protected String getPrefix() {
        return "EMAIL_";
    }
}
