package kr.spring.community.controller;

import java.security.Principal;

import org.springframework.security.access.AccessDeniedException;
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
    private final MemberService memberService; // username → memberId

    /**
     * 댓글 작성
     */
    @PostMapping("/reply")
    public String writeReply(CommunityReplyVO reply, Principal principal) {
        if (principal == null) {
            throw new AccessDeniedException("로그인 후 작성 가능합니다.");
        }

        Long memberId = memberService.getMemberIdByUsername(principal.getName());
        reply.setMemberId(memberId);

        replyService.insertReply(reply);
        return "redirect:/community/detail?id=" + reply.getPostId();
    }

    /**
     * 댓글 삭제
     */
    @PostMapping("/reply/delete")
    public String deleteReply(Long replyId, Principal principal) {
        if (principal == null) {
            throw new AccessDeniedException("로그인 후 삭제 가능합니다.");
        }

        Long memberId = memberService.getMemberIdByUsername(principal.getName());
        CommunityReplyVO reply = replyService.getReply(replyId);

        if (!reply.getMemberId().equals(memberId)) {
            throw new AccessDeniedException("본인만 삭제할 수 있습니다.");
        }

        replyService.deleteReply(replyId);
        return "redirect:/community/detail?id=" + reply.getPostId();
    }

    /**
     * 댓글 수정
     */
    @PostMapping("/reply/update")
    public String updateReply(CommunityReplyVO reply, Principal principal) {
        if (principal == null) {
            throw new AccessDeniedException("로그인 후 수정 가능합니다.");
        }

        Long memberId = memberService.getMemberIdByUsername(principal.getName());
        CommunityReplyVO original = replyService.getReply(reply.getReplyId());

        if (!original.getMemberId().equals(memberId)) {
            throw new AccessDeniedException("본인만 수정할 수 있습니다.");
        }

        replyService.updateReply(reply);
        return "redirect:/community/detail?id=" + reply.getPostId();
    }
}
