package kr.spring.api.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/api/test")
public class ApiTestViewController {

    /**
     * API 성능 비교 테스트 페이지
     */
    @GetMapping("/performance-page")
    public String performanceTestPage() {
        return "views/api/performance-test";
    }
} 