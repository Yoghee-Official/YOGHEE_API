//package com.lagavulin.yoghee.service.verfication;
//
//import java.util.Map;
//
//import org.springframework.http.HttpEntity;
//import org.springframework.http.HttpHeaders;
//import org.springframework.http.HttpMethod;
//import org.springframework.http.ResponseEntity;
//import org.springframework.stereotype.Service;
//import org.springframework.web.client.HttpClientErrorException;
//import org.springframework.web.client.RestTemplate;
//
//@Service
//public class KgInicisVerificationService {
//
//    @Value("${yoghee.kg.api-key}")
//    private String apiSecret;
//
//    private final RestTemplate restTemplate = new RestTemplate();
//
//    public Map<String, Object> getVerificationResult(String verificationId) {
//        String url = "https://api.portone.io/identity-verifications/" + verificationId;
//
//        HttpHeaders headers = new HttpHeaders();
//        headers.set("Authorization", "PortOne " + apiSecret);
//
//        HttpEntity<String> entity = new HttpEntity<>(headers);
//
//        try {
//            // exchange 호출 시 entity를 넘김
//            ResponseEntity<Map> response = restTemplate.exchange(url, HttpMethod.GET, entity, Map.class);
//            return response.getBody();
//        } catch (HttpClientErrorException.Unauthorized e) {
//            System.err.println("인증 실패 응답: " + e.getResponseBodyAsString());
//            throw new RuntimeException("API Secret 키가 올바르지 않습니다.");
//        } catch (Exception e) {
//            e.printStackTrace();
//            throw new RuntimeException("조회 실패: " + e.getMessage());
//        }
//    }
//}
