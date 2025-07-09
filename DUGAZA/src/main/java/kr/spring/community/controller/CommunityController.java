package kr.spring.community.controller;

import java.io.File;
import java.security.Principal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import kr.spring.community.service.CommunityReplyService;
import kr.spring.community.service.CommunityService;
import kr.spring.community.vo.CommunityPostVO;
import kr.spring.community.vo.CommunityReplyVO;
import kr.spring.member.service.MemberService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Controller
@RequiredArgsConstructor
@RequestMapping("/community")
public class CommunityController {

    private final CommunityService communityService;
    private final CommunityReplyService replyService;
    private final MemberService memberService;


    // 메인
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
        map.put("start", 1);
        map.put("end", 20);

        List<CommunityPostVO> list = communityService.selectPostList(map);

        model.addAttribute("list", list);
        model.addAttribute("category", category);
        model.addAttribute("order", order);
        model.addAttribute("keyword", keyword);

        return "views/sample/community";
    }

    // 상세
    @GetMapping("/detail")
    public String detail(@RequestParam("id") Long id, Model model) {
        log.info("Community detail page requested for id={}", id);

        CommunityPostVO post = communityService.selectPost(id);
        List<CommunityReplyVO> replyList = replyService.selectReplyList(id);

        model.addAttribute("post", post);
        model.addAttribute("replyList", replyList);

        return "views/sample/community-detail";
    }
    @GetMapping("/write")
    @PreAuthorize("isAuthenticated()")
    public String writeForm(Model model) {
        model.addAttribute("postVO", new CommunityPostVO());
        return "views/sample/community-write"; // 글쓰기 폼
    }
    
    //글 작성
    private static final String UPLOAD_DIR = "C:/DUGAZA/upload/";

    @PostMapping("/write")
    @PreAuthorize("isAuthenticated()")
    public String writePost(CommunityPostVO postVO,
                            @RequestParam("uploadFile") MultipartFile file,
                            Principal principal) throws Exception {
        if (!file.isEmpty()) {
            String filename = file.getOriginalFilename();

            File uploadDir = new File(UPLOAD_DIR);
            if (!uploadDir.exists()) {
                uploadDir.mkdirs(); // 디렉토리가 없으면 생성
            }

            File dest = new File(uploadDir, filename);
            file.transferTo(dest);

            postVO.setFilename(filename);
        }

        Long memberId = memberService.getMemberIdByUsername(principal.getName());
        postVO.setMemberId(memberId);

        communityService.insertPost(postVO);

        return "redirect:/community";
    }




}
