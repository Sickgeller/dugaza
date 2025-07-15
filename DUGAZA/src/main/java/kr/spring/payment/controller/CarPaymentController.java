package kr.spring.payment.controller;

import kr.spring.car.service.CarService;
import kr.spring.car.vo.CarReservationVO;
import kr.spring.car.vo.CarVO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/payment")
@RequiredArgsConstructor
@Slf4j
public class CarPaymentController {

    private final CarService carService;

    /**
     * 차량 결제 페이지
     */
    @GetMapping("/car/{reservationId}")
    public String carPaymentPage(@PathVariable Long reservationId, Model model) {
        try {
            // 예약 정보 조회
            CarReservationVO reservation = carService.getReservation(reservationId);
            if (reservation == null) {
                model.addAttribute("error", "예약 정보를 찾을 수 없습니다.");
                return "views/common/error";
            }

            // 차량 정보 조회
            CarVO car = carService.getCar(reservation.getCarId());
            if (car == null) {
                model.addAttribute("error", "차량 정보를 찾을 수 없습니다.");
                return "views/common/error";
            }

            // 대여 기간 계산
            long rentalDays = java.time.temporal.ChronoUnit.DAYS.between(
                reservation.getPickupDate(), reservation.getReturnDate());

            // 총 요금 계산
            int totalPrice = car.getCarPrice() * (int) rentalDays;

            model.addAttribute("reservation", reservation);
            model.addAttribute("car", car);
            model.addAttribute("rentalDays", rentalDays);
            model.addAttribute("totalPrice", totalPrice);

            return "views/payment/car-payment";

        } catch (Exception e) {
            log.error("결제 페이지 로드 중 오류 발생", e);
            model.addAttribute("error", "결제 페이지를 불러올 수 없습니다.");
            return "views/common/error";
        }
    }

    /**
     * 결제 처리
     */
    @PostMapping("/car/{reservationId}")
    public String processPayment(@PathVariable Long reservationId, 
                                RedirectAttributes redirectAttributes) {
        try {
            // TODO: 실제 결제 로직 구현
            // 1. 결제 검증
            // 2. 결제 처리
            // 3. 예약 상태 업데이트
            
            log.info("차량 결제 처리: reservationId = {}", reservationId);
            
            redirectAttributes.addFlashAttribute("message", "결제가 완료되었습니다.");
            return "redirect:/car/my-reservations";
            
        } catch (Exception e) {
            log.error("결제 처리 중 오류 발생", e);
            redirectAttributes.addFlashAttribute("error", "결제 처리에 실패했습니다.");
            return "redirect:/payment/car/" + reservationId;
        }
    }
} 