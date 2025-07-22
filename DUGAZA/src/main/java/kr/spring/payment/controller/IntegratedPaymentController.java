package kr.spring.payment.controller;

import kr.spring.car.service.CarService;
import kr.spring.payment.service.PaymentPendingService;
import kr.spring.payment.vo.PaymentPendingVO;
import kr.spring.payment.vo.PaymentRequest;
import kr.spring.payment.vo.PaymentPendingItemVO;
import kr.spring.house.service.HouseService;
import kr.spring.house.vo.HouseVO;
import kr.spring.member.vo.MemberVO;
import kr.spring.car.vo.CarVO;
import kr.spring.car.vo.CarReservationVO;
import kr.spring.reservation.vo.HouseReservationVO;
import kr.spring.reservation.service.HouseReservationService;
import kr.spring.payment.service.PaymentService;
import kr.spring.payment.dto.PaymentRequestDTO;
import kr.spring.payment.dto.PaymentResponseDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDateTime;
import java.util.Base64;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.ArrayList;

@Controller
@RequestMapping("/payment")
@RequiredArgsConstructor
@Slf4j
public class IntegratedPaymentController {

	private final PaymentPendingService paymentPendingService;
	private final CarService carService;
	private final HouseService houseService;
	private final PaymentService paymentService;
	private final HouseReservationService houseReservationService;

	/**
	 * 결제 대기 페이지
	 */
	@GetMapping("/pending")
	public String paymentPendingPage(Model model) {
		try {
			// TODO: 실제 로그인한 사용자의 memberId 가져오기
			// 회원 정보 가져오기
			MemberVO member = (MemberVO) model.getAttribute("member");

			//            Long memberId = 2L; // 임시로 고정값 사용
			Long memberId = member.getMemberId(); 

			List<PaymentPendingVO> paymentPendings = paymentPendingService.getPaymentPendingByMember(memberId);
			
			// status가 0(대기)인 것만 필터링
			List<PaymentPendingVO> pendingOnly = paymentPendings.stream()
				.filter(payment -> payment.getStatus() == PaymentPendingVO.STATUS_PENDING)
				.toList();

			if (pendingOnly.isEmpty()) {
				model.addAttribute("paymentPendings", pendingOnly);
				model.addAttribute("emptyPaymentPending", true);
				model.addAttribute("currentMenu", "payment-pending");
				return "views/member/payment-pending";
			}

			// 각 결제대기 항목의 상세 정보 조회
			Map<Long, List<Map<String, Object>>> paymentPendingDetails = new HashMap<>();
			
			for (PaymentPendingVO paymentPending : pendingOnly) {
				List<PaymentPendingItemVO> items = paymentPendingService.getPaymentPendingItems(paymentPending.getPaymentPendingId());
				List<Map<String, Object>> itemDetails = new ArrayList<>();
				
				for (PaymentPendingItemVO item : items) {
					Map<String, Object> detail = new HashMap<>();
					
					if (PaymentPendingItemVO.TYPE_HOUSE.equals(item.getReservationType())) {
						// 숙소 예약 정보 조회
						HouseReservationVO houseReservation = houseReservationService.getReservation(item.getReservationId());
						if (houseReservation != null) {
							HouseVO house = houseService.selectHouse(houseReservation.getHouseId());
							if (house != null) {
								detail.put("type", "숙소");
								detail.put("name", house.getTitle());
								detail.put("startDate", houseReservation.getReservationStart());
								detail.put("endDate", houseReservation.getReservationEnd());
								detail.put("price", houseReservation.getPrice());
								itemDetails.add(detail);
							}
						}
					} else if (PaymentPendingItemVO.TYPE_CAR.equals(item.getReservationType())) {
						// 차량 예약 정보 조회
						CarReservationVO carReservation = carService.getReservation(item.getReservationId());
						if (carReservation != null) {
							CarVO car = carService.getCar(carReservation.getCarId());
							if (car != null) {
								detail.put("type", "차량");
								detail.put("name", car.getCarName());
								detail.put("startDate", carReservation.getStartDate());
								detail.put("endDate", carReservation.getEndDate());
								detail.put("price", carReservation.getPrice());
								itemDetails.add(detail);
							}
						}
					}
				}
				
				paymentPendingDetails.put(paymentPending.getPaymentPendingId(), itemDetails);
			}

			model.addAttribute("paymentPendings", pendingOnly);
			model.addAttribute("paymentPendingDetails", paymentPendingDetails);
			model.addAttribute("emptyPaymentPending", false);
			model.addAttribute("currentMenu", "payment-pending");

			return "views/member/payment-pending";

		} catch (Exception e) {
			log.error("결제 대기 페이지 로드 중 오류 발생", e);
			model.addAttribute("error", "결제 대기를 불러올 수 없습니다.");
			return "views/common/error";
		}
	}

	/**
	 * 통합 결제 페이지
	 */
	@PostMapping("/checkout")
	public String checkoutPage(@RequestParam Long paymentPendingId, @RequestParam int amount, Model model) {
		try {
			PaymentPendingVO paymentPending = paymentPendingService.getPaymentPending(paymentPendingId);

			if (paymentPending == null) {
				return "redirect:/payment/pending";
			}

			// 만료 여부 확인
			if (paymentPending.isExpired()) {
				model.addAttribute("error", "결제 대기 시간이 만료되었습니다.");
				return "views/common/error";
			}

			List<PaymentPendingItemVO> items = paymentPendingService.getPaymentPendingItems(paymentPendingId);
			
			// 실제 예약 정보를 조회하여 상품 정보 구성
			List<Map<String, Object>> houseItems = new ArrayList<>();
			List<Map<String, Object>> carItems = new ArrayList<>();
			
			for (PaymentPendingItemVO item : items) {
				if (PaymentPendingItemVO.TYPE_HOUSE.equals(item.getReservationType())) {
					// 숙소 예약 정보 조회
					HouseReservationVO houseReservation = houseReservationService.getReservation(item.getReservationId());
					if (houseReservation != null) {
						HouseVO house = houseService.selectHouse(houseReservation.getHouseId());
						if (house != null) {
							Map<String, Object> houseItem = new HashMap<>();
							houseItem.put("itemName", house.getTitle());
							houseItem.put("itemImage", house.getFirstImage());
							houseItem.put("roomType", "객실");
							houseItem.put("quantity", houseReservation.getReservationCount());
							houseItem.put("startDate", houseReservation.getReservationStart());
							houseItem.put("endDate", houseReservation.getReservationEnd());
							houseItem.put("totalPrice", houseReservation.getPrice());
							houseItems.add(houseItem);
						}
					}
				} else if (PaymentPendingItemVO.TYPE_CAR.equals(item.getReservationType())) {
					// 차량 예약 정보 조회
					CarReservationVO carReservation = carService.getReservation(item.getReservationId());
					if (carReservation != null) {
						CarVO car = carService.getCar(carReservation.getCarId());
						if (car != null) {
							Map<String, Object> carItem = new HashMap<>();
							carItem.put("itemName", car.getCarName());
							carItem.put("itemImage", car.getCarImage());
							carItem.put("itemTypeName", car.getCarType());
							carItem.put("pickupLocation", carReservation.getPickupLocation());
							carItem.put("startDate", carReservation.getStartDate());
							carItem.put("endDate", carReservation.getEndDate());
							carItem.put("totalPrice", carReservation.getPrice());
							carItems.add(carItem);
						}
					}
				}
			}

			model.addAttribute("paymentPending", paymentPending);
			model.addAttribute("items", items);
			model.addAttribute("houseItems", houseItems);
			model.addAttribute("carItems", carItems);
			model.addAttribute("paymentPendingId", paymentPendingId);
			model.addAttribute("amount", amount);

			return "views/payment/checkout";

		} catch (Exception e) {
			log.error("결제 페이지 로드 중 오류 발생", e);
			model.addAttribute("error", "결제 페이지를 불러올 수 없습니다.");
			return "views/common/error";
		}
	}

	/**
	 * 통합 결제 처리
	 */
	@GetMapping("/process/{paymentPendingId}")
	public String processPayment(@PathVariable Long paymentPendingId,
			@ModelAttribute PaymentRequestDTO paymentRequest,
			RedirectAttributes redirectAttributes) {
		try {
			PaymentPendingVO paymentPending = paymentPendingService.getPaymentPending(paymentPendingId);

			if (paymentPending == null) {
				redirectAttributes.addFlashAttribute("error", "결제 대기 정보를 찾을 수 없습니다.");
				return "redirect:/payment/pending";
			}

			// 만료 여부 확인
			if (paymentPending.isExpired(LocalDateTime.now())) {
				redirectAttributes.addFlashAttribute("error", "결제 대기 시간이 만료되었습니다. 새로운 예약을 진행해주세요.");
				return "redirect:/payment/pending";
			}

			// 결제 처리
			paymentPendingService.processPayment(paymentPendingId);

			// 결제 성공
			redirectAttributes.addFlashAttribute("message", "결제가 완료되었습니다.");
			redirectAttributes.addFlashAttribute("paymentId", "PAY_" + paymentPendingId);
			redirectAttributes.addFlashAttribute("totalAmount", paymentPending.getFinalPrice());
			redirectAttributes.addFlashAttribute("transactionId", "TXN_" + System.currentTimeMillis());

			log.info("결제 성공: paymentPendingId={}, amount={}", paymentPendingId, paymentPending.getFinalPrice());

			return "redirect:/payment/complete";

		} catch (RuntimeException e) {
			log.error("결제 처리 중 오류 발생: paymentPendingId={}, error={}", paymentPendingId, e.getMessage());
			
			// 구체적인 에러 메시지 처리
			String errorMessage;
			if (e.getMessage().contains("만료")) {
				errorMessage = "결제 대기 시간이 만료되었습니다. 새로운 예약을 진행해주세요.";
			} else if (e.getMessage().contains("찾을 수 없습니다")) {
				errorMessage = "결제 대기 정보를 찾을 수 없습니다.";
			} else if (e.getMessage().contains("실패")) {
				errorMessage = "결제 처리에 실패했습니다. 다시 시도해주세요.";
			} else {
				errorMessage = "결제 처리 중 오류가 발생했습니다: " + e.getMessage();
			}
			
			redirectAttributes.addFlashAttribute("error", errorMessage);
			return "redirect:/payment/pending";
			
		} catch (Exception e) {
			log.error("결제 처리 중 예상치 못한 오류 발생: paymentPendingId={}", paymentPendingId, e);
			redirectAttributes.addFlashAttribute("error", "결제 처리 중 예상치 못한 오류가 발생했습니다. 관리자에게 문의해주세요.");
			return "redirect:/payment/pending";
		}
	}

	/**
	 * 결제 완료 페이지
	 */
	@GetMapping("/complete")
	public String paymentComplete(Model model) {
		Object paymentIdObj = model.getAttribute("paymentId");
		Object totalAmountObj = model.getAttribute("totalAmount");
		Object transactionIdObj = model.getAttribute("transactionId");

		String paymentId = null;
		Integer totalAmount = null;
		String transactionId = null;

		if (paymentIdObj != null) {
			paymentId = paymentIdObj.toString();
		}
		if (totalAmountObj != null) {
			if (totalAmountObj instanceof Integer) {
				totalAmount = (Integer) totalAmountObj;
			} else if (totalAmountObj instanceof String) {
				totalAmount = Integer.valueOf((String) totalAmountObj);
			}
		}
		if (transactionIdObj != null) {
			transactionId = transactionIdObj.toString();
		}

		model.addAttribute("paymentId", paymentId);
		model.addAttribute("totalAmount", totalAmount);
		model.addAttribute("transactionId", transactionId);

		return "views/payment/payment-complete";
	}

	/**
	 * 결제 대기 취소
	 */
	@PostMapping("/pending/cancel/{paymentPendingId}")
	@ResponseBody
	public Map<String, Object> cancelPaymentPending(@PathVariable Long paymentPendingId) {
		try {
			paymentPendingService.cancelPaymentPending(paymentPendingId);

			return Map.of(
					"success", true,
					"message", "결제 대기가 취소되었습니다."
					);

		} catch (Exception e) {
			log.error("결제 대기 취소 중 오류 발생", e);
			return Map.of(
					"success", false,
					"message", "결제 대기 취소에 실패했습니다: " + e.getMessage()
					);
		}
	}

	@GetMapping("/success")
	public String handleSuccess(@RequestParam String paymentKey,
			@RequestParam String orderId,
			@RequestParam int amount,
			Model model) {
		// 결제 정보 검증 및 후처리
		return "views/payment/success"; // 타임리프 뷰 이름
	}

} 