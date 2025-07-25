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
			@RequestParam(name = "page", defaultValue="1") int pageNum,
			@RequestParam(name = "keyword", required = false) String keyword,
			@RequestParam(name = "status", required = false) String status,
			@RequestParam(name = "sort", required = false) String sort,
			Model model) {
		
		Map<String,Object> map = 
				new HashMap<String,Object>();
		
		// 검색 조건 설정
		if (keyword != null && !keyword.trim().isEmpty()) {
			map.put("keyword", keyword.trim());
			// 기본적으로 전체 검색으로 설정
			map.put("keyfield", "4");
		}
		
		// 상태 필터 설정
		if (status != null && !status.trim().isEmpty()) {
			map.put("status", status);
		}
		
		// 정렬 설정
		if (sort != null && !sort.trim().isEmpty()) {
			map.put("sort", sort);
		}
		
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
		PagingUtil page = new PagingUtil(null, keyword,
				                pageNum,count,20,10,
				                         "admin_member");
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
		model.addAttribute("memberList", list);
		model.addAttribute("pageCount", page.getTotalPage());
		model.addAttribute("currentPage", pageNum);
		model.addAttribute("keyword", keyword);
		model.addAttribute("status", status);
		model.addAttribute("sort", sort);
		
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
	@PreAuthorize("isAuthenticated()")
	@PostMapping("/admin_update")
	public String submit(MemberVO memberVO,
			             Model model) {
		log.debug("<<회원권한 수정>> : {}",memberVO);
		
		//회원권한 수정
		memberService.updateByAdmin(memberVO);
		
		//View에 표시할 메시지
		model.addAttribute("accessTitle", "회원권한 수정");
		model.addAttribute("accessMsg", "회원권한 수정 완료!!");
		model.addAttribute("accessUrl", 
				"/member/admin_update?memberId="+memberVO.getMemberId());
		model.addAttribute("accessBtn", "이동");
		 
		return "views/common/resultView";
	}
	
}






