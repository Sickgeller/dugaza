package kr.spring.member.controller;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import kr.spring.util.ValidationUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import kr.spring.member.email.Email;
import kr.spring.member.email.EmailSender;
import kr.spring.member.service.MemberService;
import kr.spring.member.vo.MemberVO;
import kr.spring.member.vo.PrincipalDetails;
import kr.spring.member.vo.UserRole;
import kr.spring.util.StringUtil;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping("/member")
public class MemberRestController {

    @Autowired
    private MemberService memberService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    //회원가입 처리
    @PostMapping("/registerProc")
    public String submit(@Valid MemberVO memberVO,
                         BindingResult result,
                         Model model,
                         HttpServletRequest request) {
        log.debug("<<회원가입>> : " + memberVO);

//        //유효성 체크 결과 오류가 있으면 폼 호출
//        if(result.hasErrors()) {
//            //유효성 체크 결과 오류 필드 출력
//            ValidationUtil.printErrorFields(result);
//            return form();
//        }

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

//    private final JavaMailSenderImpl javaMailSenderImpl;
//	@Autowired
//	private MemberService memberService;
//	@Autowired
//	private EmailSender emailSender;
//	@Autowired
//	private Email email;
//	@Autowired
//	private PasswordEncoder passwordEncoder;
//
//    MemberRestController(JavaMailSenderImpl javaMailSenderImpl) {
//        this.javaMailSenderImpl = javaMailSenderImpl;
//    }
//	//아이디 중복 체크
//	@GetMapping("/confirmId/{id}")
//	public ResponseEntity<Map<String,String>> checkId(
//			                  @PathVariable String id){
//		log.debug("<<아이디 중복 체크>> : " + id);
//
//		Map<String,String> mapAjax =
//				new HashMap<String,String>();
//
//		MemberVO member = memberService.selectIdCheck(id);
//		if(id!=null) {//아이디 체크
//			if(member!=null) {
//				//아이디 중복
//				mapAjax.put("result", "idDuplicated");
//			}else {
//				if(!Pattern.matches("^[A-Za-z0-9]{4,12}$", id)) {
//					//패턴 불일치
//					mapAjax.put("result", "notMatchPattern");
//				}else {
//					//패턴 일치하면서 아이디 미중복
//					mapAjax.put("result", "idNotFound");
//				}
//			}
//		}else {
//			mapAjax.put("result", "error");
//		}
//
//		return new ResponseEntity<Map<String,String>>(
//				                   mapAjax,HttpStatus.OK);
//	}
//
//	//별명 중복체크
//	@GetMapping("/confirmNickName/{nick_name}")
//	public ResponseEntity<Map<String,String>>
//	                              checkNickName(
//	                    @PathVariable String nick_name){
//		log.debug("<<별명 중복체크>> : " + nick_name);
//
//		Map<String,String> mapAjax =
//				new HashMap<String,String>();
//
//		Map<String,String> map =
//				       new HashMap<String,String>();
//		map.put("nick_name", nick_name);
//		MemberVO member =
//		       memberService.selectIdAndNickName(map);
//		if(nick_name!=null) {//별명 체크
//			if(member!=null) {
//				//별명 중복
//				mapAjax.put("result", "nickDuplicated");
//			}else {
//				if(!Pattern.matches("^[ㄱ-ㅎ가-힣A-Za-z0-9]{2,10}$", nick_name)) {
//					//패턴 불일치
//					mapAjax.put("result", "notMatchPattern");
//				}else {
//					//패턴이 일치하면서 별명 미중복
//					mapAjax.put("result", "nickNotFound");
//				}
//			}
//		}else {
//			mapAjax.put("result", "error");
//		}
//
//		return new ResponseEntity<Map<String,String>>(
//				                mapAjax,HttpStatus.OK);
//	}
//
//	//프로필 사진 업로드
//	@PutMapping("/updateMyPhoto")
//	public ResponseEntity<Map<String,String>>
//	                              updateMyPhoto(
//	                         MemberVO memberVO,
//	                    @AuthenticationPrincipal
//	                    PrincipalDetails principal){
//		log.debug("<<프로필 사진 업로드>> : {}",principal);
//
//		Map<String,String> mapAjax =
//				new HashMap<String,String>();
//		if(principal==null) {
//			mapAjax.put("result", "logout");
//		}else {
//			memberVO.setMem_num(
//					principal.getMemberVO().getMem_num());
//			memberService.updateProfile(memberVO);
//			mapAjax.put("result", "success");
//		}
//		return new ResponseEntity<Map<String,String>>(
//				                   mapAjax,HttpStatus.OK);
//	}
//
//	// 비밀번호 찾기
//	@PutMapping("/getPasswordInfo")
//	public ResponseEntity<Map<String, String>> sendEmailAction(@RequestBody MemberVO memberVO){
//		log.debug("<<비밀번호 찾기>> : {}", memberVO);
//
//		Map<String, String> mapAjax = new HashMap<String, String>();
//
//		MemberVO member = memberService.selectCheckMember(memberVO.getId());
//		if(member != null && !member.getRole().equals(UserRole.INACTIVE.getValue()) && member.getEmail().equals(memberVO.getEmail())) {
//			// 오류를 대비해서 원래 비밀번호 저장
//			String origin_password = member.getPassword();
//			// 기존 비밀번호를 임시비밀번호로 변경
//			String password = StringUtil.randomPassword(10);
//			member.setPassword(passwordEncoder.encode(password));
//			// 변경된 임시 비밀번호를 DB에 저장
//			memberService.updateRadomPassword(member);
//			email.setContent("임시 비밀번호는 " + password + " 입니다.");
//			email.setReceiver(member.getEmail());
//			email.setSubject(member.getId() + " 님 비밀번호 찾기 메일입니다.");
//
//			log.debug("<<임시 비밀번호>> : {}", password);
//
//			try {
//				emailSender.sendEmail(email);
//				mapAjax.put("result", "success");
//			} catch (Exception e) {
//				log.debug("<<복구 비밀번호>> : {}", origin_password);
//				log.error("<< 비밀번호 찾기>> : {}", e.toString());
//				// 오류 발생시 비밀번호 원상 복구 발동
//				member.setPassword(origin_password);
//				memberService.updateRadomPassword(member);
//				mapAjax.put("result", "failure");
//			}
//		} else if(member != null && member.getRole().equals(UserRole.SUSPENDED.getValue())) {
//			mapAjax.put("result", "suspended");
//		} else {
//			mapAjax.put("result" , "invalidInfo");
//		}
//
//		return new ResponseEntity<Map<String,String>>(mapAjax,HttpStatus.OK);
//	}
}
