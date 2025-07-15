package kr.spring.cart.controller;

import kr.spring.cart.service.CartService;
import kr.spring.cart.vo.CartItemVO;
import kr.spring.car.service.CarService;
import kr.spring.car.vo.CarVO;
import kr.spring.house.service.HouseService;
import kr.spring.house.vo.HouseVO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Map;

@RestController
@RequestMapping("/api/cart")
@RequiredArgsConstructor
@Slf4j
public class CartController {

    private final CartService cartService;
    private final CarService carService;
    private final HouseService houseService;

    /**
     * 차량을 장바구니에 추가
     */
    @PostMapping("/add/car")
    public ResponseEntity<Map<String, Object>> addCarToCart(@RequestBody Map<String, Object> request) {
        try {
            // TODO: 실제 로그인한 사용자의 memberId 가져오기
            Long memberId = 2L;
            
            Long carId = Long.valueOf(request.get("carId").toString());
            String pickupDate = request.get("pickupDate").toString();
            String returnDate = request.get("returnDate").toString();
            
            // 차량 정보 조회
            CarVO car = carService.getCar(carId);
            if (car == null) {
                return ResponseEntity.badRequest().body(Map.of(
                    "success", false,
                    "message", "차량을 찾을 수 없습니다."
                ));
            }
            
            // 대여 기간 계산
            LocalDate startDate = LocalDate.parse(pickupDate);
            LocalDate endDate = LocalDate.parse(returnDate);
            long rentalDays = java.time.temporal.ChronoUnit.DAYS.between(startDate, endDate);
            
            // 총 요금 계산
            int totalPrice = car.getCarPrice() * (int) rentalDays;
            
            // 장바구니 아이템 생성
            CartItemVO cartItem = CartItemVO.builder()
                    .memberId(memberId)
                    .itemType("CAR")
                    .itemId(carId)
                    .startDate(startDate)
                    .endDate(endDate)
                    .quantity(1)
                    .unitPrice(car.getCarPrice())
                    .totalPrice(totalPrice)
                    .itemName(car.getCarName())
                    .itemImage(car.getCarImage())
                    .itemTypeName(car.getCarType())
                    .pickupLocation(car.getLocationName())
                    .returnLocation(car.getLocationName())
                    .build();
            
            // 장바구니에 추가
            cartService.addToCart(cartItem);
            
            // 장바구니 아이템 수 업데이트
            int itemCount = cartService.getCartItemCount(memberId);
            
            return ResponseEntity.ok(Map.of(
                "success", true,
                "message", "차량이 장바구니에 추가되었습니다.",
                "itemCount", itemCount
            ));
            
        } catch (Exception e) {
            log.error("차량 장바구니 추가 중 오류 발생", e);
            return ResponseEntity.badRequest().body(Map.of(
                "success", false,
                "message", "장바구니 추가에 실패했습니다."
            ));
        }
    }

    /**
     * 숙소를 장바구니에 추가
     */
    @PostMapping("/add/house")
    public ResponseEntity<Map<String, Object>> addHouseToCart(@RequestBody Map<String, Object> request) {
        try {
            // TODO: 실제 로그인한 사용자의 memberId 가져오기
            Long memberId = 2L;
            
            Long houseId = Long.valueOf(request.get("houseId").toString());
            String checkInDate = request.get("checkInDate").toString();
            String checkOutDate = request.get("checkOutDate").toString();
            Integer guestCount = Integer.valueOf(request.get("guestCount").toString());
            String roomType = request.get("roomType").toString();
            Integer price = Integer.valueOf(request.get("price").toString());
            
            // 숙소 정보 조회
            HouseVO house = houseService.selectHouse(houseId);
            if (house == null) {
                return ResponseEntity.badRequest().body(Map.of(
                    "success", false,
                    "message", "숙소를 찾을 수 없습니다."
                ));
            }
            
            // 숙박 기간 계산
            LocalDate startDate = LocalDate.parse(checkInDate);
            LocalDate endDate = LocalDate.parse(checkOutDate);
            long stayDays = java.time.temporal.ChronoUnit.DAYS.between(startDate, endDate);
            
            // 총 요금 계산
            int totalPrice = price * (int) stayDays;
            
            // 장바구니 아이템 생성
            CartItemVO cartItem = CartItemVO.builder()
                    .memberId(memberId)
                    .itemType("HOUSE")
                    .itemId(houseId)
                    .startDate(startDate)
                    .endDate(endDate)
                    .quantity(guestCount)
                    .unitPrice(price)
                    .totalPrice(totalPrice)
                    .itemName(house.getTitle())
                    .itemImage(house.getFirstImage())
                    .itemTypeName(roomType)
                    .roomType(roomType)
                    .build();
            
            // 장바구니에 추가
            cartService.addToCart(cartItem);
            
            // 장바구니 아이템 수 업데이트
            int itemCount = cartService.getCartItemCount(memberId);
            
            return ResponseEntity.ok(Map.of(
                "success", true,
                "message", "숙소가 장바구니에 추가되었습니다.",
                "itemCount", itemCount
            ));
            
        } catch (Exception e) {
            log.error("숙소 장바구니 추가 중 오류 발생", e);
            return ResponseEntity.badRequest().body(Map.of(
                "success", false,
                "message", "장바구니 추가에 실패했습니다."
            ));
        }
    }

    /**
     * 장바구니 아이템 수 조회
     */
    @GetMapping("/count")
    public ResponseEntity<Map<String, Object>> getCartItemCount() {
        try {
            // TODO: 실제 로그인한 사용자의 memberId 가져오기
            Long memberId = 2L;
            
            int itemCount = cartService.getCartItemCount(memberId);
            
            return ResponseEntity.ok(Map.of(
                "success", true,
                "itemCount", itemCount
            ));
            
        } catch (Exception e) {
            log.error("장바구니 아이템 수 조회 중 오류 발생", e);
            return ResponseEntity.badRequest().body(Map.of(
                "success", false,
                "message", "장바구니 정보를 불러올 수 없습니다."
            ));
        }
    }
} 