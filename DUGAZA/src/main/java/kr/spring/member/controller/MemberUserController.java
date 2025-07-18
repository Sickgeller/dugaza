package kr.spring.member.controller;

import lombok.RequiredArgsConstructor;
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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

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

// 마이페이지 관련 서비스들 추가
import kr.spring.car.service.CarReservationService;
import kr.spring.car.vo.CarReservationVO;
import kr.spring.reservation.service.HouseReservationService;
import kr.spring.reservation.vo.HouseReservationVO;
import kr.spring.payment.service.PaymentPendingService;
import kr.spring.payment.vo.PaymentPendingVO;
import kr.spring.wishlist.service.WishListService;
import kr.spring.wishlist.vo.WishListVO;
import kr.spring.review.base.service.BaseReviewService;
import kr.spring.review.base.vo.BaseReviewVO;
import kr.spring.qnaQuestion.service.QnaQuestionService;
import kr.spring.qnaQuestion.vo.QnaQuestionVO;
import kr.spring.payment.service.PaymentService;
import kr.spring.house.service.HouseService;
import kr.spring.house.vo.HouseVO;

import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.util.ArrayList;

@Slf4j
@Controller
@RequestMapping("/member")
@RequiredArgsConstructor
public class MemberUserController {
	private final MemberService memberService;
	private final PasswordEncoder passwordEncoder;
	private final CarReservationService carReservationService;
	private final HouseReservationService houseReservationService;
	private final PaymentPendingService paymentPendingService;
	private final WishListService wishListService;
	private final BaseReviewService reviewService;
	private final QnaQuestionService qnaService;
	private final PaymentService paymentService;
	private final HouseService houseService;
	
	//회원가입 폼 호출
	@GetMapping("/register")
	public String form(Model model) {
		model.addAttribute("memberVO", new MemberVO());
		return "views/member/register";
	}
	
	
	//회원가입 처리
	@PostMapping("/registerUser")
	public String submit(@Valid MemberVO memberVO,
			             BindingResult result,
			             Model model,
			             HttpServletRequest request,
			             HttpSession session) {
		log.debug("<<회원가입>> : " + memberVO);
		
		//유효성 체크 결과 오류가 있으면 폼 호출
		if(result.hasErrors()) {
			//유효성 체크 결과 오류 필드 출력
			ValidationUtil.printErrorFields(result);
			return form(model);
		}
		
		//Spring Security 암호화
		memberVO.setPassword(passwordEncoder.encode(
				               memberVO.getPassword()));
		//회원가입
		memberService.insertMember(memberVO);
		
		//세션에 성공 메시지 저장
		session.setAttribute("registerSuccess", "회원가입이 완료되었습니다! 이제 로그인하여 서비스를 이용해보세요.");
		
		return "redirect:/member/login";
	}
	
	//로그인 폼 호출
	@GetMapping("/login")
	public String formLogin(HttpSession session, Model model) {
		//세션에서 회원가입 성공 메시지 확인
		String registerSuccess = (String) session.getAttribute("registerSuccess");
		if (registerSuccess != null) {
			model.addAttribute("registerSuccess",registerSuccess);
			session.removeAttribute("registerSuccess"); //메시지 사용 후 세션에서 제거
		}
		return "views/member/login";
	}
	
	@GetMapping("/myPage")
	public String getMyPage(Model model) {
		return "views/member/dashboard";
	}
	
	//회원정보수정 폼 호출
	@GetMapping("/updateUser")
	public String formUpdate(Model model) {
		return "views/member/memberModify";
	}
	
	//회원정보수정 처리
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
		
		return "redirect:/member/dashboard";
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
	/**
	 * 마이페이지 메인 - 대시보드
	 */
	@GetMapping("/dashboard")
	public String dashboard(Model model, @AuthenticationPrincipal CustomUserDetails userDetails) {
		try {
			MemberVO member = userDetails.getMember();
			if (member == null) {
				return "redirect:/member/login";
			}
			
			Long memberId = member.getMemberId();
			
			// 실제 서비스 호출
			List<CarReservationVO> carReservations = carReservationService.getReservationsByMember(memberId);
			List<HouseReservationVO> houseReservations = houseReservationService.getReservationsByMember(memberId);
			
			// 숙소 정보 조회 (houseId -> HouseVO 매핑)
			Map<Long, HouseVO> houseMap = new HashMap<>();
			for (HouseReservationVO reservation : houseReservations) {
				try {
					HouseVO house = houseService.selectHouse(reservation.getHouseId());
					if (house != null) {
						houseMap.put(reservation.getHouseId(), house);
					}
				} catch (Exception e) {
					log.warn("숙소 정보 조회 실패: houseId={}", reservation.getHouseId(), e);
				}
			}
			
			// 결제 대기 정보
			List<PaymentPendingVO> paymentPendings = paymentPendingService.getPaymentPendingByMember(memberId);
			int paymentPendingCount = paymentPendings.size();
			
			// 찜 목록 수 (임시로 0 사용 - WishListService에 count 메서드가 없음)
			int wishListCount = 0;
			
			// 리뷰 수 (임시로 0 사용 - BaseReviewService에 member별 조회 메서드가 없음)
			int reviewCount = 0;
			
			// 문의 수 (임시로 0 사용 - QnaQuestionService에 member별 조회 메서드가 없음)
			int qnaCount = 0;
			
			// 결제 내역 (임시로 빈 리스트 사용 - PaymentService에 member별 조회 메서드가 없음)
			List<Object> payments = new ArrayList<>();
			
			// 모델에 데이터 추가
			model.addAttribute("carReservations", carReservations);
			model.addAttribute("houseReservations", houseReservations);
			model.addAttribute("houseMap", houseMap);
			model.addAttribute("paymentPendingCount", paymentPendingCount);
			model.addAttribute("wishListCount", wishListCount);
			model.addAttribute("reviewCount", reviewCount);
			model.addAttribute("qnaCount", qnaCount);
			model.addAttribute("payments", payments);
			model.addAttribute("currentMenu", "dashboard");
			
			log.info("마이페이지 대시보드 - 사용자: {}, 차량예약: {}, 숙소예약: {}, 결제대기: {}, 찜: {}, 리뷰: {}, 문의: {}",
					member.getName(), carReservations.size(), houseReservations.size(), 
					paymentPendingCount, wishListCount, reviewCount, qnaCount);
			
			return "views/member/dashboard";
			
		} catch (Exception e) {
			log.error("마이페이지 대시보드 로드 중 오류 발생", e);
			model.addAttribute("error", "대시보드를 불러올 수 없습니다.");
			return "views/common/error";
		}
	}
	
	/**
	 * 예약 내역 페이지
	 */
	@GetMapping("/reservations")
	public String reservations(Model model, @AuthenticationPrincipal CustomUserDetails userDetails) {
		try {
			MemberVO member = userDetails.getMember();
			if (member == null) {
				return "redirect:/member/login";
			}
			
			Long memberId = member.getMemberId();
			
			// 실제 서비스 호출
			List<CarReservationVO> carReservations = carReservationService.getReservationsByMember(memberId);
			List<HouseReservationVO> houseReservations = houseReservationService.getReservationsByMember(memberId);
			
			log.info("차량 예약 조회 결과: memberId={}, count={}", memberId, carReservations.size());
			if (!carReservations.isEmpty()) {
				for (CarReservationVO reservation : carReservations) {
					log.info("차량 예약: reservationId={}, carId={}, carName={}, startDate={}, endDate={}", 
							reservation.getReservationId(), reservation.getCarId(), 
							reservation.getCarName(), reservation.getStartDate(), reservation.getEndDate());
				}
			}
			
			// 숙소 정보 조회 (houseId -> HouseVO 매핑)
			Map<Long, HouseVO> houseMap = new HashMap<>();
			for (HouseReservationVO reservation : houseReservations) {
				try {
					HouseVO house = houseService.selectHouse(reservation.getHouseId());
					if (house != null) {
						houseMap.put(reservation.getHouseId(), house);
					}
				} catch (Exception e) {
					log.warn("숙소 정보 조회 실패: houseId={}", reservation.getHouseId(), e);
				}
			}
			
			model.addAttribute("carReservations", carReservations);
			model.addAttribute("houseReservations", houseReservations);
			model.addAttribute("houseMap", houseMap);
			model.addAttribute("currentMenu", "reservations");
			
			return "views/member/reservations";
			
		} catch (Exception e) {
			log.error("예약 내역 조회 중 오류 발생", e);
			model.addAttribute("error", "예약 내역을 불러올 수 없습니다.");
			return "views/common/error";
		}
	}
	
	/**
	 * 찜 목록 페이지
	 */
	@GetMapping("/wishlist")
	public String wishlist(Model model, @AuthenticationPrincipal CustomUserDetails userDetails) {
		try {
			MemberVO member = userDetails.getMember();
			if (member == null) {
				return "redirect:/member/login";
			}
			
			Long memberId = member.getMemberId();
			// 임시로 빈 리스트 사용
			List<WishListVO> wishList = new ArrayList<>();
			
			model.addAttribute("wishList", wishList);
			model.addAttribute("currentMenu", "wishlist");
			
			return "views/member/wishlist";
			
		} catch (Exception e) {
			log.error("찜 목록 조회 중 오류 발생", e);
			model.addAttribute("error", "찜 목록을 불러올 수 없습니다.");
			return "views/common/error";
		}
	}
	
	/**
	 * 리뷰 관리 페이지
	 */
	@GetMapping("/reviews")
	public String reviews(Model model, @AuthenticationPrincipal CustomUserDetails userDetails) {
		try {
			MemberVO member = userDetails.getMember();
			if (member == null) {
				return "redirect:/member/login";
			}

			Long memberId = member.getMemberId();
			
			// 실제 서비스 호출
			List<BaseReviewVO> reviews = reviewService.getReviewsByMember(memberId);

			// 통계 계산
			double averageRating = 0.0;
			long houseReviewCount = 0;
			long carReviewCount = 0;

			if (!reviews.isEmpty()) {
				// 평균 평점 계산
				double totalRating = reviews.stream()
					.mapToDouble(review -> review.getRating() != null ? review.getRating() : 0.0)
					.sum();
				averageRating = totalRating / reviews.size();

				// 리뷰 타입별 개수 계산 (contentTypeId로 구분)
				houseReviewCount = reviews.stream()
					.filter(review -> review.getContentTypeId() != null && review.getContentTypeId() == 1) // 숙소 타입 ID
					.count();
				carReviewCount = reviews.stream()
					.filter(review -> review.getContentTypeId() != null && review.getContentTypeId() == 2) // 차량 타입 ID
					.count();
			}

			model.addAttribute("reviews", reviews);
			model.addAttribute("averageRating", averageRating);
			model.addAttribute("houseReviewCount", houseReviewCount);
			model.addAttribute("carReviewCount", carReviewCount);
			model.addAttribute("currentMenu", "reviews");

			return "views/member/reviews";

		} catch (Exception e) {
			log.error("리뷰 조회 중 오류 발생", e);
			model.addAttribute("error", "리뷰를 불러올 수 없습니다.");
			return "views/common/error";
		}
	}

	/**
	 * 문의 내역 페이지
	 */
	@PreAuthorize("isAuthenticated()")
	@GetMapping("/qna")
	public String qna(Model model, @AuthenticationPrincipal CustomUserDetails userDetails) {
		try {
			MemberVO member = userDetails.getMember();
			if (member == null) {
				return "redirect:/member/login";
			}
			
			Long memberId = member.getMemberId();
			// 임시로 빈 리스트 사용
			List<QnaQuestionVO> qnaList = new ArrayList<>();
			
			model.addAttribute("qnaList", qnaList);
			model.addAttribute("currentMenu", "qna");
			
			return "views/member/qna";
			
		} catch (Exception e) {
			log.error("문의 내역 조회 중 오류 발생", e);
			model.addAttribute("error", "문의 내역을 불러올 수 없습니다.");
			return "views/common/error";
		}
	}
	
	/**
	 * 결제 내역 페이지
	 */
	@PreAuthorize("isAuthenticated()")
	@GetMapping("/payments")
	public String payments(Model model, @AuthenticationPrincipal CustomUserDetails userDetails) {
		try {
			MemberVO member = userDetails.getMember();
			if (member == null) {
				return "redirect:/member/login";
			}
			
			Long memberId = member.getMemberId();
			// 임시로 빈 리스트 사용
			List<Object> payments = new ArrayList<>();
			
			model.addAttribute("payments", payments);
			model.addAttribute("currentMenu", "payments");
			
			return "views/member/payments";
			
		} catch (Exception e) {
			log.error("결제 내역 조회 중 오류 발생", e);
			model.addAttribute("error", "결제 내역을 불러올 수 없습니다.");
			return "views/common/error";
		}
	}
	
	/**
	 * 개인정보 관리 페이지
	 */
	@GetMapping("/profile")
	public String profile(Model model, @AuthenticationPrincipal CustomUserDetails userDetails) {
		try {
			MemberVO member = userDetails.getMember();
			if (member == null) {
				return "redirect:/member/login";
			}
			
			model.addAttribute("currentMenu", "profile");
			
			return "views/member/profile";
			
		} catch (Exception e) {
			log.error("개인정보 페이지 로드 중 오류 발생", e);
			model.addAttribute("error", "개인정보 페이지를 불러올 수 없습니다.");
			return "views/common/error";
		}
	}

	/**
	 * 결제 페이지
	 */
	@GetMapping("/payment")
	public String payment(Model model, @AuthenticationPrincipal CustomUserDetails userDetails) {
		try {
			MemberVO member = userDetails.getMember();
			if (member == null) {
				return "redirect:/member/login";
			}
			
			Long memberId = member.getMemberId();
			
			// 결제되지 않은 예약들 조회
			List<CarReservationVO> unpaidCarReservations = paymentPendingService.getUnpaidCarReservations(memberId);
			List<HouseReservationVO> unpaidHouseReservations = paymentPendingService.getUnpaidHouseReservations(memberId);
			
			// 숙소 정보 조회 (houseId -> HouseVO 매핑)
			Map<Long, HouseVO> houseMap = new HashMap<>();
			for (HouseReservationVO reservation : unpaidHouseReservations) {
				try {
					HouseVO house = houseService.selectHouse(reservation.getHouseId());
					if (house != null) {
						houseMap.put(reservation.getHouseId(), house);
					}
				} catch (Exception e) {
					log.warn("숙소 정보 조회 실패: houseId={}", reservation.getHouseId(), e);
				}
			}
			
			model.addAttribute("unpaidCarReservations", unpaidCarReservations);
			model.addAttribute("unpaidHouseReservations", unpaidHouseReservations);
			model.addAttribute("houseMap", houseMap);
			model.addAttribute("currentMenu", "payment");
			
			return "views/member/payment";
			
		} catch (Exception e) {
			log.error("결제 페이지 로드 중 오류 발생", e);
			model.addAttribute("error", "결제 페이지를 불러올 수 없습니다.");
			return "views/common/error";
		}
	}
}









