package kr.spring.faq.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;


@Slf4j
@Controller
@RequestMapping("/community")
public class CommunityController {

    @GetMapping({"", "/"})
    public String communityMain() {
        log.info("Community main page requested");
        return "views/sample/community";
    }
}

