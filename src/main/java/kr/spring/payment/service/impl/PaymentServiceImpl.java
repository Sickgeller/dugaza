package kr.spring.payment.service.impl;

import kr.spring.payment.service.PaymentPendingService;
import kr.spring.payment.vo.PaymentPendingVO;
import kr.spring.payment.vo.PaymentPendingItemVO;
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

    private final PaymentPendingService paymentPendingService;
    private final CarService carService;
    private final HouseService houseService;
    private final HouseReservationService houseReservationService;

    @Override
    public PaymentResponseDTO processPayment(PaymentPendingVO paymentPending, PaymentRequestDTO paymentRequest) {
        try {
            // 1. 결제 검증
            if (!validatePayment(paymentPending, paymentRequest)) {
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

            // 3. 결제 대기 처리 (예약 확정)
            paymentPendingService.processPayment(paymentPending.getPaymentPendingId());

            // 4. 결제 응답 생성
            return PaymentResponseDTO.builder()
                    .paymentId(paymentId)
                    .status("SUCCESS")
                    .message("결제가 성공적으로 처리되었습니다.")
                    .amount(paymentPending.getFinalPrice())
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
    public boolean validatePayment(PaymentPendingVO paymentPending, PaymentRequestDTO paymentRequest) {
        try {
            // 1. 결제 대기 검증
            if (paymentPending == null) {
                log.warn("결제 대기 정보가 없습니다.");
                return false;
            }

            // 2. 결제 대기 상태 검증
            if (!paymentPending.isPending()) {
                log.warn("결제 대기 상태가 유효하지 않습니다: {}", paymentPending.getStatus());
                return false;
            }

            // 3. 만료 여부 검증
            if (paymentPending.isExpired(LocalDateTime.now())) {
                log.warn("결제 대기 시간이 만료되었습니다.");
                return false;
            }

            // 4. 결제 금액 검증
            if (paymentPending.getFinalPrice() <= 0) {
                log.warn("결제 금액이 유효하지 않습니다: {}", paymentPending.getFinalPrice());
                return false;
            }

            // 5. 카드 정보 검증
            if (!validateCardInfo(paymentRequest)) {
                log.warn("카드 정보가 유효하지 않습니다.");
                return false;
            }

            // 6. 예약 가능성 검증
            if (!validateReservationAvailability(paymentPending)) {
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

    private boolean validateReservationAvailability(PaymentPendingVO paymentPending) {
        try {
            List<PaymentPendingItemVO> items = paymentPendingService.getPaymentPendingItems(paymentPending.getPaymentPendingId());
            
            for (PaymentPendingItemVO item : items) {
                if (PaymentPendingItemVO.TYPE_HOUSE.equals(item.getReservationType())) {
                    if (!validateHouseAvailability(item)) {
                        return false;
                    }
                } else if (PaymentPendingItemVO.TYPE_CAR.equals(item.getReservationType())) {
                    if (!validateCarAvailability(item)) {
                        return false;
                    }
                }
            }
            
            return true;
            
        } catch (Exception e) {
            log.error("예약 가능성 검증 중 오류 발생", e);
            return false;
        }
    }

    private boolean validateHouseAvailability(PaymentPendingItemVO item) {
        // 숙소 예약 가능성 검증 로직
        // 실제로는 숙소 서비스에서 검증
        return true;
    }

    private boolean validateCarAvailability(PaymentPendingItemVO item) {
        // 차량 예약 가능성 검증 로직
        // 실제로는 차량 서비스에서 검증
        return true;
    }

    private boolean validateCardInfo(PaymentRequestDTO paymentRequest) {
        try {
            // 카드 번호 검증
            String cardNumber = paymentRequest.getCardNumber().replace("-", "");
            if (cardNumber.length() < 13 || cardNumber.length() > 19) {
                return false;
            }

            // 유효기간 검증
            String expiryDate = paymentRequest.getExpiryDate();
            if (expiryDate == null || expiryDate.length() != 5) {
                return false;
            }

            // CVC 검증
            String cvc = paymentRequest.getCvc();
            if (cvc == null || cvc.length() < 3 || cvc.length() > 4) {
                return false;
            }

            // 카드 소유자명 검증
            String cardHolder = paymentRequest.getCardHolder();
            if (cardHolder == null || cardHolder.trim().isEmpty()) {
                return false;
            }

            return true;

        } catch (Exception e) {
            log.error("카드 정보 검증 중 오류 발생", e);
            return false;
        }
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
        String cleanNumber = cardNumber.replace("-", "");
        if (cleanNumber.length() >= 4) {
            return cleanNumber.substring(cleanNumber.length() - 4);
        }
        return "****";
    }

    private String detectCardType(String cardNumber) {
        String cleanNumber = cardNumber.replace("-", "");
        
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