package kr.spring.member.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import kr.spring.member.service.MemberService;
import kr.spring.member.vo.MemberVO;
import kr.spring.auth.security.PrincipalDetails;
import kr.spring.util.FileUtil;
import kr.spring.util.ValidationUtil;
import lombok.extern.slf4j.Slf4j;

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
		model.addAttribute("accessTitle", "회원가입");
		model.addAttribute("accessMsg", 
				            "회원가입이 완료되었습니다.");
		model.addAttribute("accessBtn", "홈으로");
		model.addAttribute("accessUrl", 
				  request.getContextPath()+"/");
		
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
	public String getMyPage(
			@AuthenticationPrincipal 
			PrincipalDetails principal,
			Model model) {
		
		//회원정보
		MemberVO member =
				memberService.selectMember(
						principal.getMemberVO()
						         .getMemberId());
		
		model.addAttribute("member", member);
		
		return "views/member/memberView";
	}
	
	//회원정보수정 폼 호출
	@PreAuthorize("isAuthenticated()")
	@GetMapping("/updateUser")
	public String formUpdate(
			        @AuthenticationPrincipal
			        PrincipalDetails principal,
			        Model model) {
		//회원정보
		MemberVO memberVO =
				memberService.selectMember(
				  principal.getMemberVO().getMemberId());
		model.addAttribute("memberVO", memberVO);
		
		return "views/member/memberModify";
	}
	
	//회원정보수정 처리
	@PreAuthorize("isAuthenticated()")
	@PostMapping("/updateUser")
	public String submitUpdate(@Valid MemberVO memberVO,
			                   BindingResult result,
			                   @AuthenticationPrincipal
			                   PrincipalDetails principal) {
		log.debug("<<회원정보수정>> : {}",memberVO);
		
		//유효성 체크 결과 오류가 있으면 폼 호출
		if(result.hasErrors()) {
			ValidationUtil.printErrorFields(result);
			return "views/member/memberModify";
		}
		
		memberVO.setMemberId(
				principal.getMemberVO().getMemberId());		
		//회원정보수정
		memberService.updateMember(memberVO);
		
		//PrincipalDetails에 저장된 자바빈의 email 정보 갱신
		principal.getMemberVO().setEmail(
				                  memberVO.getEmail());
		
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
	public String submitChangePassword(@Valid MemberVO memberVO, BindingResult result, HttpServletRequest request, @AuthenticationPrincipal PrincipalDetails principal) {
		log.debug("<<비밀번호 변경>> : {}", memberVO);
		if(result.hasFieldErrors("now_passwd") || result.hasFieldErrors("passwd")) {
			ValidationUtil.printErrorFields(result);
			return formChangePassword();
		}
		
		// 회원번호 저장
		memberVO.setMemberId(principal.getMemberVO().getMemberId());
		
		return "views/common/resultAlert";
	}
}









