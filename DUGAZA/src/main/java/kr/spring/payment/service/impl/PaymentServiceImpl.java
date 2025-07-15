package kr.spring.payment.service.impl;

import kr.spring.cart.service.CartService;
import kr.spring.cart.vo.CartItemVO;
import kr.spring.cart.vo.CartVO;
import kr.spring.car.service.CarService;
import kr.spring.car.dto.CarReservationDTO;
import kr.spring.car.vo.CarReservationVO;
import kr.spring.house.service.HouseService;
import kr.spring.reservation.service.HouseReservationService;
import kr.spring.reservation.vo.HouseReservationVO;
import kr.spring.payment.service.PaymentService;
import kr.spring.payment.dto.PaymentRequestDTO;
import kr.spring.payment.dto.PaymentResponseDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class PaymentServiceImpl implements PaymentService {

    private final CartService cartService;
    private final CarService carService;
    private final HouseService houseService;
    private final HouseReservationService houseReservationService;

    @Override
    public PaymentResponseDTO processPayment(CartVO cart, PaymentRequestDTO paymentRequest) {
        try {
            // 1. 결제 검증
            if (!validatePayment(cart, paymentRequest)) {
                return PaymentResponseDTO.builder()
                        .status("FAILED")
                        .message("결제 검증에 실패했습니다.")
                        .paymentDate(LocalDateTime.now())
                        .build();
            }

            // 2. 결제 처리 (실제로는 외부 결제 API 호출)
            String paymentId = generatePaymentId();
            String transactionId = generateTransactionId();
            
            // 더미 결제 처리 - 실제로는 외부 결제 API 호출
            boolean paymentSuccess = processDummyPayment(paymentRequest);
            
            if (!paymentSuccess) {
                return PaymentResponseDTO.builder()
                        .paymentId(paymentId)
                        .status("FAILED")
                        .message("결제 처리에 실패했습니다.")
                        .paymentDate(LocalDateTime.now())
                        .build();
            }

            // 3. 예약 생성
            createReservations(cart);

            // 4. 장바구니 비우기
            cartService.clearCart(cart.getMemberId());

            // 5. 결제 응답 생성
            return PaymentResponseDTO.builder()
                    .paymentId(paymentId)
                    .status("SUCCESS")
                    .message("결제가 성공적으로 처리되었습니다.")
                    .amount(cart.getTotalAmount())
                    .currency("KRW")
                    .paymentDate(LocalDateTime.now())
                    .transactionId(transactionId)
                    .cardLastFourDigits(getLastFourDigits(paymentRequest.getCardNumber()))
                    .cardType(detectCardType(paymentRequest.getCardNumber()))
                    .build();

        } catch (Exception e) {
            log.error("결제 처리 중 오류 발생", e);
            return PaymentResponseDTO.builder()
                    .status("FAILED")
                    .message("결제 처리 중 오류가 발생했습니다: " + e.getMessage())
                    .paymentDate(LocalDateTime.now())
                    .build();
        }
    }

    @Override
    public boolean validatePayment(CartVO cart, PaymentRequestDTO paymentRequest) {
        try {
            // 1. 장바구니 검증
            if (cart == null || cart.getItems() == null || cart.getItems().isEmpty()) {
                log.warn("장바구니가 비어있습니다.");
                return false;
            }

            // 2. 결제 금액 검증
            if (cart.getTotalAmount() <= 0) {
                log.warn("결제 금액이 유효하지 않습니다: {}", cart.getTotalAmount());
                return false;
            }

            // 3. 카드 정보 검증
            if (!validateCardInfo(paymentRequest)) {
                log.warn("카드 정보가 유효하지 않습니다.");
                return false;
            }

            // 4. 예약 가능성 검증
            if (!validateReservationAvailability(cart)) {
                log.warn("일부 예약이 불가능합니다.");
                return false;
            }

            return true;

        } catch (Exception e) {
            log.error("결제 검증 중 오류 발생", e);
            return false;
        }
    }

    @Override
    public boolean cancelPayment(String paymentId) {
        try {
            // 실제로는 외부 결제 API를 통해 취소 처리
            log.info("결제 취소 처리: paymentId={}", paymentId);
            return true;
        } catch (Exception e) {
            log.error("결제 취소 중 오류 발생", e);
            return false;
        }
    }

    @Override
    public String getPaymentStatus(String paymentId) {
        try {
            // 실제로는 외부 결제 API를 통해 상태 조회
            log.info("결제 상태 조회: paymentId={}", paymentId);
            return "SUCCESS";
        } catch (Exception e) {
            log.error("결제 상태 조회 중 오류 발생", e);
            return "UNKNOWN";
        }
    }

    // ===== Private Methods =====

    private String generatePaymentId() {
        return "PAY_" + UUID.randomUUID().toString().replace("-", "").substring(0, 16);
    }

    private String generateTransactionId() {
        return "TXN_" + System.currentTimeMillis();
    }

    private boolean processDummyPayment(PaymentRequestDTO paymentRequest) {
        // 더미 결제 처리 - 실제로는 외부 결제 API 호출
        try {
            // 카드 번호 유효성 검사 (간단한 검증)
            String cardNumber = paymentRequest.getCardNumber().replace("-", "");
            if (cardNumber.length() < 13 || cardNumber.length() > 19) {
                return false;
            }

            // Luhn 알고리즘 검증 (간단한 버전)
            if (!validateLuhnAlgorithm(cardNumber)) {
                return false;
            }

            // 더미 지연 (실제 결제 처리 시뮬레이션)
            Thread.sleep(1000);

            // 90% 성공률로 설정 (테스트용)
            return Math.random() > 0.1;

        } catch (Exception e) {
            log.error("더미 결제 처리 중 오류", e);
            return false;
        }
    }

    private void createReservations(CartVO cart) {
        List<CartItemVO> items = cart.getItems();
        
        for (CartItemVO item : items) {
            try {
                if ("HOUSE".equals(item.getItemType())) {
                    createHouseReservation(item);
                } else if ("CAR".equals(item.getItemType())) {
                    createCarReservation(item);
                }
            } catch (Exception e) {
                log.error("예약 생성 중 오류: itemType={}, itemId={}", item.getItemType(), item.getItemId(), e);
                throw new RuntimeException("예약 생성에 실패했습니다: " + e.getMessage());
            }
        }
    }

    private void createHouseReservation(CartItemVO item) {
        HouseReservationVO reservation = new HouseReservationVO();
        reservation.setHouseId(item.getItemId());
        reservation.setMemberId(item.getMemberId());
        reservation.setReservationStart(item.getStartDate().atStartOfDay());
        reservation.setReservationEnd(item.getEndDate().atStartOfDay());
        reservation.setReservationCount(item.getQuantity());
        reservation.setPrice(item.getTotalPrice());
        reservation.setStatus(0); // 예약 대기 상태

        houseReservationService.insertReservation(reservation);
        log.info("숙소 예약 생성 완료: houseId={}, memberId={}", item.getItemId(), item.getMemberId());
    }

    private void createCarReservation(CartItemVO item) {
        CarReservationDTO reservationDTO = new CarReservationDTO();
        reservationDTO.setCarId(item.getItemId());
        reservationDTO.setMemberId(item.getMemberId());
        reservationDTO.setPickupDate(item.getStartDate());
        reservationDTO.setReturnDate(item.getEndDate());

        carService.createReservation(reservationDTO);
        log.info("차량 예약 생성 완료: carId={}, memberId={}", item.getItemId(), item.getMemberId());
    }

    private boolean validateCardInfo(PaymentRequestDTO paymentRequest) {
        // 카드 번호 검증
        if (paymentRequest.getCardNumber() == null || paymentRequest.getCardNumber().trim().isEmpty()) {
            return false;
        }

        // 유효기간 검증
        if (paymentRequest.getExpiryDate() == null || !paymentRequest.getExpiryDate().matches("\\d{2}/\\d{2}")) {
            return false;
        }

        // CVC 검증
        if (paymentRequest.getCvc() == null || !paymentRequest.getCvc().matches("\\d{3,4}")) {
            return false;
        }

        // 카드 소유자명 검증
        if (paymentRequest.getCardHolder() == null || paymentRequest.getCardHolder().trim().isEmpty()) {
            return false;
        }

        // 생년월일 검증
        if (paymentRequest.getBirthDate() == null || !paymentRequest.getBirthDate().matches("\\d{8}")) {
            return false;
        }

        return true;
    }

    private boolean validateReservationAvailability(CartVO cart) {
        List<CartItemVO> items = cart.getItems();
        
        for (CartItemVO item : items) {
            try {
                if ("HOUSE".equals(item.getItemType())) {
                    // 숙소 예약 가능성 검증
                    if (!validateHouseAvailability(item)) {
                        return false;
                    }
                } else if ("CAR".equals(item.getItemType())) {
                    // 차량 예약 가능성 검증
                    if (!validateCarAvailability(item)) {
                        return false;
                    }
                }
            } catch (Exception e) {
                log.error("예약 가능성 검증 중 오류: itemType={}, itemId={}", item.getItemType(), item.getItemId(), e);
                return false;
            }
        }
        
        return true;
    }

    private boolean validateHouseAvailability(CartItemVO item) {
        // 실제로는 숙소 예약 가능성 검증 로직 구현
        // 현재는 더미로 항상 true 반환
        return true;
    }

    private boolean validateCarAvailability(CartItemVO item) {
        // 실제로는 차량 예약 가능성 검증 로직 구현
        // 현재는 더미로 항상 true 반환
        return true;
    }

    private boolean validateLuhnAlgorithm(String cardNumber) {
        // Luhn 알고리즘 검증 (간단한 버전)
        int sum = 0;
        boolean alternate = false;
        
        for (int i = cardNumber.length() - 1; i >= 0; i--) {
            int n = Integer.parseInt(cardNumber.substring(i, i + 1));
            if (alternate) {
                n *= 2;
                if (n > 9) {
                    n = (n % 10) + 1;
                }
            }
            sum += n;
            alternate = !alternate;
        }
        
        return (sum % 10 == 0);
    }

    private String getLastFourDigits(String cardNumber) {
        if (cardNumber == null || cardNumber.length() < 4) {
            return "****";
        }
        String cleanNumber = cardNumber.replace("-", "");
        return cleanNumber.substring(cleanNumber.length() - 4);
    }

    private String detectCardType(String cardNumber) {
        if (cardNumber == null) {
            return "UNKNOWN";
        }
        
        String cleanNumber = cardNumber.replace("-", "");
        
        // 간단한 카드 타입 감지
        if (cleanNumber.startsWith("4")) {
            return "VISA";
        } else if (cleanNumber.startsWith("5")) {
            return "MASTERCARD";
        } else if (cleanNumber.startsWith("3")) {
            return "AMEX";
        } else {
            return "UNKNOWN";
        }
    }
} 