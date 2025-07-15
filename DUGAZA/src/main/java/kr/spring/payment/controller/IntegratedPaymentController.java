package kr.spring.payment.controller;

import kr.spring.car.service.CarService;
import kr.spring.cart.service.CartService;
import kr.spring.cart.vo.CartItemVO;
import kr.spring.cart.vo.CartVO;
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

    private final CartService cartService;
    private final CarService carService;
    private final HouseService houseService;
    private final PaymentService paymentService;

    /**
     * 장바구니 페이지
     */
    @GetMapping("/cart")
    public String cartPage(Model model) {
        try {
            // TODO: 실제 로그인한 사용자의 memberId 가져오기
            Long memberId = 2L; // 임시로 고정값 사용
            
            CartVO cart = cartService.getCart(memberId);
            
            if (cart.getItems().isEmpty()) {
                model.addAttribute("emptyCart", true);
                return "views/payment/cart";
            }
            
            // 아이템 타입별로 분류
            Map<String, List<CartItemVO>> itemsByType = cart.getItems().stream()
                    .collect(Collectors.groupingBy(CartItemVO::getItemType));
            
            model.addAttribute("cart", cart);
            model.addAttribute("houseItems", itemsByType.get("HOUSE"));
            model.addAttribute("carItems", itemsByType.get("CAR"));
            model.addAttribute("emptyCart", false);
            
            return "views/payment/cart";
            
        } catch (Exception e) {
            log.error("장바구니 페이지 로드 중 오류 발생", e);
            model.addAttribute("error", "장바구니를 불러올 수 없습니다.");
            return "views/common/error";
        }
    }

    /**
     * 통합 결제 페이지
     */
    @GetMapping("/checkout")
    public String checkoutPage(Model model) {
        try {
            // TODO: 실제 로그인한 사용자의 memberId 가져오기
            Long memberId = 2L; // 임시로 고정값 사용
            
            CartVO cart = cartService.getCart(memberId);
            
            if (cart.getItems().isEmpty()) {
                return "redirect:/payment/cart";
            }
            
            // 아이템 타입별로 분류
            Map<String, List<CartItemVO>> itemsByType = cart.getItems().stream()
                    .collect(Collectors.groupingBy(CartItemVO::getItemType));
            
            model.addAttribute("cart", cart);
            model.addAttribute("houseItems", itemsByType.get("HOUSE"));
            model.addAttribute("carItems", itemsByType.get("CAR"));
            
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
    @PostMapping("/process")
    public String processPayment(@ModelAttribute PaymentRequestDTO paymentRequest,
                                RedirectAttributes redirectAttributes) {
        try {
            // TODO: 실제 로그인한 사용자의 memberId 가져오기
            Long memberId = 2L; // 임시로 고정값 사용
            
            CartVO cart = cartService.getCart(memberId);
            
            if (cart.getItems().isEmpty()) {
                redirectAttributes.addFlashAttribute("error", "장바구니가 비어있습니다.");
                return "redirect:/payment/cart";
            }
            
            // 결제 처리
            PaymentResponseDTO paymentResponse = paymentService.processPayment(cart, paymentRequest);
            
            if ("SUCCESS".equals(paymentResponse.getStatus())) {
                // 결제 성공
                redirectAttributes.addFlashAttribute("message", paymentResponse.getMessage());
                redirectAttributes.addFlashAttribute("paymentId", paymentResponse.getPaymentId());
                redirectAttributes.addFlashAttribute("totalAmount", paymentResponse.getAmount());
                redirectAttributes.addFlashAttribute("transactionId", paymentResponse.getTransactionId());
                
                log.info("결제 성공: paymentId={}, amount={}, transactionId={}", 
                        paymentResponse.getPaymentId(), paymentResponse.getAmount(), paymentResponse.getTransactionId());
                
                return "redirect:/payment/complete";
            } else {
                // 결제 실패
                redirectAttributes.addFlashAttribute("error", paymentResponse.getMessage());
                log.warn("결제 실패: {}", paymentResponse.getMessage());
                return "redirect:/payment/checkout";
            }
            
        } catch (Exception e) {
            log.error("결제 처리 중 오류 발생", e);
            redirectAttributes.addFlashAttribute("error", "결제 처리 중 오류가 발생했습니다.");
            return "redirect:/payment/checkout";
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
     * 장바구니에서 아이템 삭제
     */
    @PostMapping("/cart/remove/{cartItemId}")
    @ResponseBody
    public Map<String, Object> removeFromCart(@PathVariable String cartItemId) {
        try {
            cartService.removeFromCart(cartItemId);
            
            // TODO: 실제 로그인한 사용자의 memberId 가져오기
            Long memberId = 2L;
            int itemCount = cartService.getCartItemCount(memberId);
            int totalAmount = cartService.calculateTotalAmount(memberId);
            
            return Map.of(
                "success", true,
                "message", "아이템이 장바구니에서 삭제되었습니다.",
                "itemCount", itemCount,
                "totalAmount", totalAmount
            );
            
        } catch (Exception e) {
            log.error("장바구니 아이템 삭제 중 오류 발생", e);
            return Map.of(
                "success", false,
                "message", "아이템 삭제에 실패했습니다."
            );
        }
    }
} 