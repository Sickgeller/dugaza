package kr.spring.community.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import kr.spring.community.service.CommunityService;
import kr.spring.community.vo.CommunityPostVO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Controller
@RequiredArgsConstructor
@RequestMapping("/community")
public class CommunityController {

    private final CommunityService communityService;

    @GetMapping({"", "/"})
    public String communityMain(
        @RequestParam(required = false) String category,
        @RequestParam(required = false) String order,
        @RequestParam(required = false) String keyword,
        Model model) {

        log.info("Community main page requested");

        Map<String, Object> map = new HashMap<>();
        map.put("category", category);
        map.put("order", order);
        map.put("keyword", keyword);

        List<CommunityPostVO> list = communityService.selectPostList(map);
        log.info("list.size()={}", list.size());

        model.addAttribute("list", list);
        model.addAttribute("category", category);
        model.addAttribute("order", order);
        model.addAttribute("keyword", keyword);

        return "views/sample/community";
    }

}
