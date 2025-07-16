package kr.spring.community.controller;

import java.io.File;
import java.security.Principal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import kr.spring.community.service.CommunityReplyService;
import kr.spring.community.service.CommunityService;
import kr.spring.community.vo.CommunityPostVO;
import kr.spring.community.vo.CommunityReplyVO;
import kr.spring.member.service.MemberService;
import kr.spring.util.PagingUtil;
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

    private static final String UPLOAD_DIR = "C:/community-uploads/";

    
    






 // 📄 커뮤니티 메인
    @GetMapping({"", "/"})
    public String communityMain(
            @RequestParam(name = "pageNum", defaultValue = "1") int pageNum,
            @RequestParam(required = false) String category,
            @RequestParam(required = false) String order,
            @RequestParam(required = false) String keyword,
            Model model) {

        log.info("Community main page requested: page={}, category={}, order={}, keyword={}", pageNum, category, order, keyword);

        Map<String, Object> map = new HashMap<>();
        map.put("category", category);
        map.put("order", order);
        map.put("keyword", keyword);

        int count = communityService.selectPostCount(map);

        // 페이징 유틸 생성
        PagingUtil page = new PagingUtil(
                pageNum,
                count,
                15,     // 한 페이지에 16개
                5,      // 블록 크기
                "/community",
                "&category=" + (category != null ? category : "") + "&order=" + (order != null ? order : "") + "&keyword=" + (keyword != null ? keyword : "")
        );

        List<CommunityPostVO> list = null;
        if (count > 0) {
            map.put("start", page.getStartRow());
            map.put("end", page.getEndRow());
            list = communityService.selectPostList(map);
        }

        model.addAttribute("list", list);
        model.addAttribute("count", count);
        model.addAttribute("page", page.getPage());
        model.addAttribute("category", category);
        model.addAttribute("order", order);
        model.addAttribute("keyword", keyword);

        return "views/sample/community";
    }



    // 📄 글 상세
    @GetMapping("/detail")
    public String detail(@RequestParam("id") Long id, Model model, Principal principal) {
        log.info("Community detail page requested for id={}", id);

        communityService.incrementViewCount(id); 
        CommunityPostVO post = communityService.selectPost(id);
        List<CommunityReplyVO> replyList = replyService.selectReplyList(id);

        model.addAttribute("post", post);
        model.addAttribute("replyList", replyList);

        boolean isOwner = false;
        boolean liked = false;

        if (principal != null) {
            Long memberId = memberService.getMemberIdByUsername(principal.getName());
            isOwner = post.getMemberId().equals(memberId);
            liked = communityService.isLiked(id, memberId);

            // ✅ 현재 로그인 사용자 ID를 모델에 추가
            model.addAttribute("currentUserId", memberId);
        }

        model.addAttribute("isOwner", isOwner);
        model.addAttribute("liked", liked);

        return "views/sample/community-detail";
    }



    // ✍ 글쓰기 폼
    @GetMapping("/write")
    @PreAuthorize("isAuthenticated()")
    public String writeForm(Model model) {
        model.addAttribute("postVO", new CommunityPostVO());
        return "views/sample/community-write";
    }

    // ✍ 글 작성
    @PostMapping("/write")
    @PreAuthorize("isAuthenticated()")
    public String writePost(CommunityPostVO postVO,
                            @RequestParam("uploadFile") MultipartFile file,
                            Principal principal) throws Exception {

        Long memberId = memberService.getMemberIdByUsername(principal.getName());
        postVO.setMemberId(memberId);
        

        if (!file.isEmpty()) {
            String filename = file.getOriginalFilename();

            File uploadDir = new File(UPLOAD_DIR);
            if (!uploadDir.exists()) {
                uploadDir.mkdirs();
            }

            File dest = new File(uploadDir, filename);
            file.transferTo(dest);

            postVO.setFilename(filename);
        } else {
            // 여기 추가! 기존 게시글의 파일명 유지
            if (postVO.getId() != null) {
                CommunityPostVO dbPost = communityService.selectPost(postVO.getId());
                postVO.setFilename(dbPost.getFilename());
            }
        }

        if (postVO.getId() != null) {
            communityService.updatePost(postVO);
        } else {
            communityService.insertPost(postVO);
        }

        return "redirect:/community";
    }


    // ❤️ 좋아요 토글
    @PostMapping("/like")
    @ResponseBody
    public Map<String, Object> toggleLike(@RequestParam Long id, Principal principal) {
        Long memberId = memberService.getMemberIdByUsername(principal.getName());
        boolean liked = communityService.toggleLike(id, memberId);
        int likeCount = communityService.selectPost(id).getLikeCount();

        Map<String, Object> result = new HashMap<>();
        result.put("likeCount", likeCount);
        result.put("liked", liked);
        return result;
    }
    
 // 수정 폼
    @GetMapping("/edit")
    @PreAuthorize("isAuthenticated()")
    public String editForm(@RequestParam("id") Long id, Model model, Principal principal) {
        CommunityPostVO post = communityService.selectPost(id);
        Long memberId = memberService.getMemberIdByUsername(principal.getName());

        if (!post.getMemberId().equals(memberId)) {
            throw new AccessDeniedException("본인 글만 수정할 수 있습니다.");
        }

        model.addAttribute("postVO", post);
        return "views/sample/community-write";
    }

    // 수정 처리
    @PostMapping("/edit")
    @PreAuthorize("isAuthenticated()")
    public String editPost(CommunityPostVO postVO,
                           @RequestParam("uploadFile") MultipartFile file,
                           Principal principal) throws Exception {
        CommunityPostVO original = communityService.selectPost(postVO.getId());
        Long memberId = memberService.getMemberIdByUsername(principal.getName());

        if (!original.getMemberId().equals(memberId)) {
            throw new AccessDeniedException("본인 글만 수정할 수 있습니다.");
        }

        if (!file.isEmpty()) {
            String filename = file.getOriginalFilename();
            File uploadDir = new File(UPLOAD_DIR);
            if (!uploadDir.exists()) uploadDir.mkdirs();
            File dest = new File(uploadDir, filename);
            file.transferTo(dest);
            postVO.setFilename(filename);
        } else {
            postVO.setFilename(original.getFilename());
        }

        postVO.setMemberId(memberId);
        communityService.updatePost(postVO);

        return "redirect:/community/detail?id=" + postVO.getId();
    }
    
    @GetMapping("/delete")
    @PreAuthorize("isAuthenticated()")
    public String deletePost(@RequestParam("id") Long id, Principal principal) {
        CommunityPostVO post = communityService.selectPost(id);
        Long memberId = memberService.getMemberIdByUsername(principal.getName());

        if (!post.getMemberId().equals(memberId)) {
            throw new AccessDeniedException("본인 글만 삭제할 수 있습니다.");
        }

        communityService.deletePost(id);
        return "redirect:/community";
    }



}
