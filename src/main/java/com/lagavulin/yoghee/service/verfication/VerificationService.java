package com.lagavulin.yoghee.service.verfication;

import java.time.Duration;

import com.lagavulin.yoghee.exception.BusinessException;
import com.lagavulin.yoghee.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public abstract class VerificationService {
    private final StringRedisTemplate redisTemplate;

    public abstract void sendVerificationCode(String to);

    protected abstract String getPrefix();

    public void verifyCode(String to, String code){
        if(to == null || to.isEmpty() || code == null || code.isEmpty()){
            throw new BusinessException(ErrorCode.INVALID_REQUEST, "인증 정보가 올바르지 않습니다.");
        }
        String key = getPrefix() + to;
        String savedCode = redisTemplate.opsForValue().get(key);
        if(!code.equals(savedCode)){
            throw new BusinessException(ErrorCode.INVALID_REQUEST, "인증 정보가 올바르지 않습니다.");
        }
        redisTemplate.delete(to);
    }

    protected void saveVerificationCode(String to, String code){
        String key = getPrefix() + to;
        redisTemplate.opsForValue().set(key, code, Duration.ofMinutes(5));
    }

    protected String makeVerificationCode(){
        StringBuilder code = new StringBuilder();
        for(int i=0; i<6; i++){
            int digit = (int)(Math.random() * 10);
            code.append(digit);
        }
        return code.toString();
    }
}
