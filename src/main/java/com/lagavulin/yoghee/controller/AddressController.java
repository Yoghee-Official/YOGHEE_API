package com.lagavulin.yoghee.controller;

import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * 카카오 주소 검색 테스트용 컨트롤러 local 프로파일에서만 활성화됨
 */
@Controller
@Profile("local")
public class AddressController {

    @GetMapping("/address/search")
    public String addressSearch() {
        return "address";
    }
}
