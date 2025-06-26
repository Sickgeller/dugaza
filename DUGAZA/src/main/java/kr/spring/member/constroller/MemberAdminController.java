package kr.spring.member.constroller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import jakarta.servlet.http.HttpServletRequest;
import kr.spring.member.service.MemberService;
import kr.spring.member.vo.MemberVO;
import kr.spring.util.PagingUtil;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Controller
@PreAuthorize("hasRole('ROLE_ADMIN')")
@RequestMapping("/member")
public class MemberAdminController {
	@Autowired
	private MemberService memberService;
	
	//회원목록
	@GetMapping("/admin_list")
	public String getList(
			@RequestParam(defaultValue="1") int pageNum,
			String keyfield,String keyword,Model model) {
		
		Map<String,Object> map = 
				new HashMap<String,Object>();
		map.put("keyfield", keyfield);
		map.put("keyword", keyword);
		
		//전체/검색 레코드수
		int count = memberService.selectRowCount(map);
		
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
		model.addAttribute("list", list);
		model.addAttribute("page", page.getPage());
		model.addAttribute("keyfield", keyfield);
		model.addAttribute("keyword", keyword);
		
		return "views/member/admin_list";
	}
	
	//회원권한 수정 폼 호출
	@GetMapping("/admin_update")
	public String form(long mem_num, Model model) {
		MemberVO memberVO =
				memberService.selectMember(mem_num);
		
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
				request.getContextPath()+"/member/admin_update?mem_num="+memberVO.getMem_num());
		model.addAttribute("accessBtn", "이동");
		 
		return "views/common/resultView";
	}
	
}






