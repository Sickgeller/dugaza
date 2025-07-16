package kr.spring.payment.controller;

import kr.spring.car.service.CarService;
import kr.spring.payment.service.PaymentPendingService;
import kr.spring.payment.vo.PaymentPendingVO;
import kr.spring.payment.vo.PaymentPendingItemVO;
import kr.spring.house.service.HouseService;
import kr.spring.house.vo.HouseVO;
import kr.spring.car.vo.CarVO;
import kr.spring.car.vo.CarReservationVO;
import kr.spring.reservation.vo.HouseReservationVO;
import kr.spring.payment.service.PaymentService;
import kr.spring.payment.dto.PaymentRequestDTO;
import kr.spring.payment.dto.PaymentResponseDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/payment")
@RequiredArgsConstructor
@Slf4j
public class IntegratedPaymentController {

    private final PaymentPendingService paymentPendingService;
    private final CarService carService;
    private final HouseService houseService;
    private final PaymentService paymentService;

    /**
     * 결제 대기 페이지
     */
    @GetMapping("/pending")
    public String paymentPendingPage(Model model) {
        try {
            // TODO: 실제 로그인한 사용자의 memberId 가져오기
            Long memberId = 2L; // 임시로 고정값 사용
            
            List<PaymentPendingVO> paymentPendings = paymentPendingService.getPaymentPendingByMember(memberId);
            
            if (paymentPendings.isEmpty()) {
                model.addAttribute("emptyPaymentPending", true);
                return "views/payment/payment-pending";
            }
            
            model.addAttribute("paymentPendings", paymentPendings);
            model.addAttribute("emptyPaymentPending", false);
            
            return "views/payment/payment-pending";
            
        } catch (Exception e) {
            log.error("결제 대기 페이지 로드 중 오류 발생", e);
            model.addAttribute("error", "결제 대기를 불러올 수 없습니다.");
            return "views/common/error";
        }
    }

    /**
     * 통합 결제 페이지
     */
    @GetMapping("/checkout/{paymentPendingId}")
    public String checkoutPage(@PathVariable Long paymentPendingId, Model model) {
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
            
            model.addAttribute("paymentPending", paymentPending);
            model.addAttribute("items", items);
            
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
    @PostMapping("/process/{paymentPendingId}")
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
            if (paymentPending.isExpired()) {
                redirectAttributes.addFlashAttribute("error", "결제 대기 시간이 만료되었습니다.");
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
            
        } catch (Exception e) {
            log.error("결제 처리 중 오류 발생", e);
            redirectAttributes.addFlashAttribute("error", "결제 처리 중 오류가 발생했습니다: " + e.getMessage());
            return "redirect:/payment/checkout/" + paymentPendingId;
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
} 