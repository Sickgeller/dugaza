package kr.spring.member.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.SessionAttribute;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import kr.spring.member.service.MemberService;
import kr.spring.member.vo.MemberVO;
import kr.spring.auth.security.CustomUserDetails;
import kr.spring.util.FileUtil;
import kr.spring.util.ValidationUtil;
import lombok.extern.slf4j.Slf4j;
import kr.spring.seller.vo.SellerVO;

@Slf4j
@Controller
@RequestMapping("/member")
public class MemberUserController {
	@Autowired
	private MemberService memberService;
	
	@Autowired
	private PasswordEncoder passwordEncoder;
	
	//자바빈(VO) 초기화
	@ModelAttribute
	public MemberVO initCommand() {
		return new MemberVO();
	}
	
	//회원가입 폼 호출
	@GetMapping("/register")
	public String form() {
		return "views/member/register";
	}
	
	//회원가입 처리
	@PostMapping("/registerUser")
	public String submit(@Valid MemberVO memberVO,
			             BindingResult result,
			             Model model,
			             HttpServletRequest request) {
		log.debug("<<회원가입>> : " + memberVO);
		
		//유효성 체크 결과 오류가 있으면 폼 호출
		if(result.hasErrors()) {
			//유효성 체크 결과 오류 필드 출력
			ValidationUtil.printErrorFields(result);
			return form();
		}
		
		//Spring Security 암호화
		memberVO.setPassword(passwordEncoder.encode(
				               memberVO.getPassword()));
		//회원가입
		memberService.insertMember(memberVO);
		
		//결과 메시지 처리
		model.addAttribute("accessTitle", "회원가입 완료");
		model.addAttribute("accessMsg", 
				            "회원가입이 완료되었습니다. 이제 로그인하여 서비스를 이용해보세요!");
		model.addAttribute("accessBtn", "로그인하러 가기");
		model.addAttribute("accessUrl", 
				  request.getContextPath()+"/member/login");
		
		return "views/common/resultView";
	}
	
	//로그인 폼 호출
	@GetMapping("/login")
	public String formLogin() {
		return "views/member/login";
	}
	
	/*
	 * @PreAuthorize
	 * 메서드 호출 이전에 접근 권한을 검사. 메서드 실행 전에
	 * 주어진 SpEL(Spring Expression Language) 조건을
	 * 평가하여 접근을 허용할지 결정
	 */
	//MY페이지 호출
	@PreAuthorize("isAuthenticated()")
	@GetMapping("/myPage")
	public String getMyPage(Model model) {
		return "views/member/memberView";
	}
	
	//회원정보수정 폼 호출
	@PreAuthorize("isAuthenticated()")
	@GetMapping("/updateUser")
	public String formUpdate(Model model) {
		return "views/member/memberModify";
	}
	
	//회원정보수정 처리
	@PreAuthorize("isAuthenticated()")
	@PostMapping("/updateUser")
	public String submitUpdate(@Valid MemberVO memberVO, BindingResult result) {
		log.debug("<<회원정보수정>> : {}",memberVO);
		
		if(result.hasErrors()) {
			ValidationUtil.printErrorFields(result);
			return "views/member/memberModify";
		}
		
		// memberId는 세션에서 자동 주입된 member로부터 가져와야 하므로, 서비스 단에서 처리하거나 별도 로직 필요
		// memberService.updateMember(memberVO);
		
		log.info("회원정보 수정 완료");
		
		return "redirect:/member/myPage";
	}

	
	//기본 이미지 읽기
	public void getBasicProfileImage(
			               HttpServletRequest request,
			               Model model) {
		byte[] readbyte = 
				FileUtil.getBytes(
						request.getServletContext()
						       .getRealPath("/assets/image_bundle/face.png"));
		model.addAttribute("imageFile", readbyte);
		model.addAttribute("filename", "face.png");
	}
	
	// 비밀번호 찾기
	@GetMapping("/sendPassword")
	public String sendPasswordForm() {
		return "views/member/memberFindPassword";
	}
	
	// 비밀번호 변경 폼
	@PreAuthorize("isAuthenticated()")
	@GetMapping("/changePassword")
	public String formChangePassword() {
		return "views/member/memberChangePassword";
	}
	
	// 비밀번호 변경
	@PreAuthorize("isAuthenticated()")
	@PostMapping("/changePassword")
	public String submitChangePassword(@Valid MemberVO memberVO, BindingResult result) {
		log.debug("<<비밀번호 변경>> : {}", memberVO);
		
		// 세션에서 member 정보 가져오기
		MemberVO member = (MemberVO) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		if (member == null) {
			return "redirect:/member/login";
		}
		
		if(result.hasFieldErrors("now_passwd") || result.hasFieldErrors("passwd")) {
			ValidationUtil.printErrorFields(result);
			return formChangePassword();
		}
		
		// 회원번호 저장
		memberVO.setMemberId(member.getMemberId());
		
		// 비밀번호 암호화 후 업데이트
		memberVO.setPassword(passwordEncoder.encode(memberVO.getPassword()));
		memberService.updatePassword(memberVO);
		
		log.info("비밀번호 변경 완료: 사용자 = {}", member.getId());
		
		return "views/common/resultAlert";
	}
	//사용예시 메서드 나중에 주석처리를하든 삭제를하든 고쳐도됨 
	@PreAuthorize("isAuthenticated()")
	/*
		시큐리티 필터에서 권한 통제중이라 이거 안붙여도되긴함
		이렇게 붙여놓으면 2번체크함 -> 서비스 커지면 DB부하 커짐 (중대한 로직 돌릴때 사용하면됨 삭제, 업데이트 그런거 ㅇㅇ)
	*/
	@GetMapping("/mypage")
	public String viewDetail(Model model) {
		// @ModelAttribute에서 자동으로 추가된 사용자 정보 사용
		// member나 seller 정보는 이미 모델에 추가되어 있음
		
		log.debug("사용자 상세 정보 페이지 접근");
		return "views/member/detail";
	}


}









