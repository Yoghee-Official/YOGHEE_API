package com.lagavulin.yoghee.service.verfication;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

import com.lagavulin.yoghee.exception.BusinessException;
import com.lagavulin.yoghee.exception.ErrorCode;
import feign.RequestInterceptor;
import feign.RequestTemplate;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class SmsVerificationService extends VerificationService {

    private final NCloudSensClient nCloudSensClient;

    @Value("${ncloud.sens.accessKey}")
    private String accessKey;

    @Value("${ncloud.sens.secretKey}")
    private String secretKey;

    @Value("${ncloud.sens.serviceId}")
    private String serviceId;

    @Value("${ncloud.sens.senderPhone}")
    private String senderPhone;

    @Autowired
    public SmsVerificationService(StringRedisTemplate redisTemplate, NCloudSensClient nCloudSensClient) {
        super(redisTemplate);
        this.nCloudSensClient = nCloudSensClient;
    }

    @Override
    public void sendVerificationCode(String phoneNumber) {
        String code = makeVerificationCode();

        try {
            String timestamp = String.valueOf(System.currentTimeMillis());
            String uri = "/sms/v2/services/" + serviceId + "/messages";
            String signature = makeSignature("POST", uri, timestamp);

            Map<String, Object> body = new HashMap<>();
            body.put("type", "SMS");
            body.put("contentType", "COMM");
            // body.put("countryCode", "82"); // Default 82 KR
            body.put("from", senderPhone);
            body.put("content", "[YOGHEE] 인증번호는 [" + code + "] 입니다.");
            body.put("messages", Collections.singletonList(Collections.singletonMap("to", phoneNumber)));

//            Map<String, Object> response = nCloudSensClient.sendSms(
//                serviceId,
//                timestamp,
//                accessKey,
//                signature,
//                body
//            );

//            if(response.get("statusCode") != null && "202".equals(response.get("statusCode").toString())) {
                saveVerificationCode(phoneNumber, code);
//            }
        } catch (Exception e) {
            log.error("Failed to send SMS", e);
            throw new BusinessException(ErrorCode.INTERNAL_SERVER_ERROR);
        }
    }

    @Override
    protected String getPrefix() {
        return "SMS_";
    }

    private String makeSignature(String method, String uri, String timestamp) throws Exception {
        String message = method + " " + uri + "\n" + timestamp + "\n" + accessKey;
        SecretKeySpec signingKey = new SecretKeySpec(secretKey.getBytes("UTF-8"), "HmacSHA256");
        Mac mac = Mac.getInstance("HmacSHA256");
        mac.init(signingKey);

        byte[] rawHmac = mac.doFinal(message.getBytes("UTF-8"));
        return Base64.getEncoder().encodeToString(rawHmac);
    }
}
