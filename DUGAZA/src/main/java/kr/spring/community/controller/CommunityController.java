package kr.spring.community.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

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

    // 메인
    @GetMapping({"", "/"})
    public String communityMain(Model model) {
        log.info("Community main page requested");

        // 페이징/검색은 나중에… 지금은 전체 불러오기
        Map<String, Object> map = new HashMap<>();
        map.put("start", 1);
        map.put("end", 20);

        List<CommunityPostVO> list = communityService.selectPostList(map);
        model.addAttribute("list", list);

        return "views/sample/community";
    }
}
