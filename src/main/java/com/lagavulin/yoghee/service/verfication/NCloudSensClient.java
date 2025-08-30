package com.lagavulin.yoghee.service.verfication;

import java.util.Map;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;

@FeignClient(
    name = "naverSensClient",
    url = "https://sens.apigw.ntruss.com"
)
public interface NCloudSensClient {

    @PostMapping("/sms/v2/services/{serviceId}/messages")
    Map<String, Object> sendSms(
        @PathVariable("serviceId") String serviceId,
        @RequestHeader("x-ncp-apigw-timestamp") String timestamp,
        @RequestHeader("x-ncp-iam-access-key") String accessKey,
        @RequestHeader("x-ncp-apigw-signature-v2") String signature,
        @RequestBody Map<String, Object> body
    );
}
