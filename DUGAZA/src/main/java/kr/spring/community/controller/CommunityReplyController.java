package kr.spring.community.controller;

import java.security.Principal;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import kr.spring.community.service.CommunityReplyService;
import kr.spring.community.vo.CommunityReplyVO;
import kr.spring.member.service.MemberService;
import lombok.RequiredArgsConstructor;

@Controller
@RequestMapping("/community")
@RequiredArgsConstructor
public class CommunityReplyController {

    private final CommunityReplyService replyService;
    private final MemberService memberService;

    /**
     * 댓글 작성
     */
    @PostMapping("/reply")
    public String writeReply(CommunityReplyVO reply, Principal principal) {
        // 로그인 사용자 ID를 댓글에 셋팅
        Long memberId = memberService.getMemberIdByUsername(principal.getName());
        reply.setMemberId(memberId);

        replyService.insertReply(reply);
        return "redirect:/community/detail?id=" + reply.getPostId();
    }

    /**
     * 댓글 삭제
     */
    @PostMapping("/reply/delete")
    public String deleteReply(Long replyId) {
        Long postId = replyService.getReply(replyId).getPostId();
        replyService.deleteReply(replyId);
        return "redirect:/community/detail?id=" + postId;
    }

    /**
     * 댓글 수정
     */
    @PostMapping("/reply/update")
    public String updateReply(CommunityReplyVO reply) {
        replyService.updateReply(reply);
        return "redirect:/community/detail?id=" + reply.getPostId();
    }
}
