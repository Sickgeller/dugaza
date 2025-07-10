package kr.spring.member.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import jakarta.servlet.http.HttpServletRequest;
import kr.spring.auth.security.CustomUserDetails;
import kr.spring.member.service.MemberService;
import kr.spring.member.vo.MemberVO;
import kr.spring.seller.vo.SellerVO;
import kr.spring.util.PagingUtil;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Controller
//@PreAuthorize("hasRole('ADMIN')")
@RequestMapping("/member")
@RequiredArgsConstructor
public class MemberAdminController {

	private final MemberService memberService;

	/**
	 * 모든 컨트롤러 메서드에서 자동으로 실행되어 인증된 사용자 정보를 모델에 추가
	 */
	@ModelAttribute
	public void addUserToModel(Model model) {
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		
		if (authentication != null && authentication.isAuthenticated() && 
			authentication.getPrincipal() instanceof CustomUserDetails) {
			
			CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
			
			// 사용자 정보를 모델에 추가
			model.addAttribute("userDetails", userDetails);
			
			// 회원인 경우
			if (userDetails.isMember()) {
				model.addAttribute("member", userDetails.getMember());
			}
			
			// 판매자인 경우
			if (userDetails.isSeller()) {
				model.addAttribute("seller", userDetails.getSeller());
			}
		}
	}
	
	// 관리자 회원관리 페이지 이동
//	@GetMapping("/admin_member")
//	public String form(Model model) {
//		// 전체 회원 수
//		int memberCount = memberService.selectMemberCount();
//		
//		model.addAttribute("count",memberCount);
//		
//		return "views/admin/super-admin-users";
//	}
	
	//회원목록
	@GetMapping("/admin_member")
	public String getList(
			@RequestParam(name = "pageNum", defaultValue="1") int pageNum,
			String keyfield,String keyword,Model model) {
		
		Map<String,Object> map = 
				new HashMap<String,Object>();
		map.put("keyfield", keyfield);
		map.put("keyword", keyword);
		
		//전체/검색 레코드수
		int count = memberService.selectRowCount(map);
		
		// 신규 회원 수
		int newCount = memberService.selectNewMemberCount();
		
		// 탈퇴 회원 수
		int withdrawnCount = memberService.selectWithdrawnMemberCount();
		
		// 휴면 회원 수
		int humanCount = memberService.selectHumanMemberCount();
		
		log.debug("<<회원목록 - count>> : {}",count);
		
		//페이지 처리
		PagingUtil page = new PagingUtil(keyfield,keyword,
				                pageNum,count,20,10,
				                         "admin_list");
		List<MemberVO> list = null;
		if(count > 0) {
			map.put("start", page.getStartRow());
			map.put("end", page.getEndRow());
			
			list = memberService.selectList(map);
			log.debug("<<회원목록 - list>> : {}",list);
		}
		
		model.addAttribute("count", count);
		model.addAttribute("newCount", newCount);
		model.addAttribute("withdrawnCount", withdrawnCount);
		model.addAttribute("humanCount", humanCount);
		model.addAttribute("list", list);
		model.addAttribute("page", page.getPage());
		model.addAttribute("keyfield", keyfield);
		model.addAttribute("keyword", keyword);
		
		return "views/admin/super-admin-users";
	}
	
	//회원권한 수정 폼 호출
	@GetMapping("/admin_update")
	public String form(long memberId, Model model) {
		MemberVO memberVO =
				memberService.selectMember(memberId);
		
		model.addAttribute("memberVO", memberVO);
		
		return "views/member/admin_modify";
	}
	
	//회원권한 수정 
	@PostMapping("/admin_update")
	public String submit(MemberVO memberVO,
			             Model model,
			             HttpServletRequest request) {
		log.debug("<<회원권한 수정>> : {}",memberVO);
		
		//회원권한 수정
		memberService.updateByAdmin(memberVO);
		
		//View에 표시할 메시지
		model.addAttribute("accessTitle", "회원권한 수정");
		model.addAttribute("accessMsg", "회원권한 수정 완료!!");
		model.addAttribute("accessUrl", 
				request.getContextPath()+"/member/admin_update?memberId="+memberVO.getMemberId());
		model.addAttribute("accessBtn", "이동");
		 
		return "views/common/resultView";
	}
	
}






