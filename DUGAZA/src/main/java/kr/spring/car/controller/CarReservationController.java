package kr.spring.car.controller;

import kr.spring.car.service.CarService;
import kr.spring.car.dao.CarLocationCodeMapper;
import kr.spring.car.vo.CarVO;
import kr.spring.car.vo.CarLocationCodeVO;
import kr.spring.car.vo.CarReservationVO;
import kr.spring.car.dto.CarSearchDTO;
import kr.spring.car.dto.CarReservationDTO;
import kr.spring.member.vo.MemberVO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Slf4j
@Controller
@RequestMapping("/car")
@RequiredArgsConstructor
public class CarReservationController {

    private final CarService carService;
    private final CarLocationCodeMapper locationCodeMapper;

    @GetMapping("/search")
    public String carSearchForm(Model model) {
        // 위치 코드 목록을 모델에 추가
        List<CarLocationCodeVO> locationCodes = locationCodeMapper.selectAllActiveLocationCodes();
        model.addAttribute("locationCodes", locationCodes);
        model.addAttribute("searchDTO", new CarSearchDTO());
        return "views/car/car-search";
    }

    @PostMapping("/search")
    public String carSearch(@ModelAttribute CarSearchDTO searchDTO, Model model) {
        try {
            log.info("차량 검색 요청: pickupLocationCode={}, pickupDate={}, returnDate={}", 
                    searchDTO.getPickupLocationCode(), searchDTO.getPickupDate(), searchDTO.getReturnDate());
            
            // 위치 코드로 위치명 설정
            if (searchDTO.getPickupLocationCode() != null) {
                CarLocationCodeVO pickupLocation = locationCodeMapper.selectLocationCode(searchDTO.getPickupLocationCode());
                if (pickupLocation != null) {
                    searchDTO.setPickupLocationName(pickupLocation.getLocationDetail());
                }
            }
            
            List<CarVO> availableCars = carService.searchAvailableCars(searchDTO);
            List<CarLocationCodeVO> locationCodes = locationCodeMapper.selectAllActiveLocationCodes();
            
            model.addAttribute("cars", availableCars);
            model.addAttribute("searchDTO", searchDTO);
            model.addAttribute("locationCodes", locationCodes);
            
            log.info("차량 검색 결과: {}건", availableCars.size());
            
            return "views/car/car-list";
        } catch (Exception e) {
            log.error("차량 검색 중 오류 발생", e);
            model.addAttribute("error", "차량 검색에 실패했습니다.");
            List<CarLocationCodeVO> locationCodes = locationCodeMapper.selectAllActiveLocationCodes();
            model.addAttribute("locationCodes", locationCodes);
            return "views/car/car-search";
        }
    }

    @GetMapping("/detail/{carId}")
    public String carDetail(@PathVariable Long carId, Model model) {
        try {
            CarVO car = carService.getCar(carId);
            if (car == null) {
                model.addAttribute("error", "차량을 찾을 수 없습니다.");
                return "views/common/error";
            }
            
            model.addAttribute("car", car);
            return "views/car/car-detail";
        } catch (Exception e) {
            log.error("차량 상세 정보 조회 중 오류 발생", e);
            model.addAttribute("error", "차량 정보를 불러올 수 없습니다.");
            return "views/common/error";
        }
    }

    @GetMapping("/reservation/{carId}")
    public String reservationForm(@PathVariable Long carId, Model model) {
        try {
            CarVO car = carService.getCar(carId);
            if (car == null) {
                model.addAttribute("error", "차량을 찾을 수 없습니다.");
                return "views/common/error";
            }
            
            CarReservationDTO reservationDTO = new CarReservationDTO();
            reservationDTO.setCarId(carId);
            
            // 위치 코드 목록 추가
            List<CarLocationCodeVO> locationCodes = locationCodeMapper.selectAllActiveLocationCodes();
            
            model.addAttribute("car", car);
            model.addAttribute("reservationDTO", reservationDTO);
            model.addAttribute("locationCodes", locationCodes);
            return "views/car/car-reservation";
        } catch (Exception e) {
            log.error("예약 폼 로드 중 오류 발생", e);
            model.addAttribute("error", "예약 페이지를 불러올 수 없습니다.");
            return "views/common/error";
        }
    }

    @PostMapping("/reservation")
    public String createReservation(@ModelAttribute CarReservationDTO reservationDTO, 
                                   Model model, 
                                   RedirectAttributes redirectAttributes) {
        try {
            // 현재 로그인한 사용자의 memberId 설정
            // TODO: 실제 인증 정보에서 memberId 가져오기
            reservationDTO.setMemberId(2L); // 임시로 고정값 사용
            
            // 예약 생성 로직
            CarReservationVO reservation = carService.createReservation(reservationDTO);
            
            redirectAttributes.addFlashAttribute("message", "차량 예약이 완료되었습니다.");
            redirectAttributes.addFlashAttribute("reservationId", reservation.getReservationId());
            
            log.info("차량 예약 완료: reservationId = {}, carId = {}", 
                    reservation.getReservationId(), reservation.getCarId());
            
            return "redirect:/car/reservation/complete";
        } catch (Exception e) {
            log.error("차량 예약 중 오류 발생", e);
            redirectAttributes.addFlashAttribute("error", "예약에 실패했습니다.");
            return "redirect:/car/reservation/" + reservationDTO.getCarId();
        }
    }

    @GetMapping("/reservation/complete")
    public String reservationComplete(Model model) {
        // FlashAttribute에서 예약 정보 가져오기
        Object reservationIdObj = model.getAttribute("reservationId");
        Long reservationId = null;
        
        if (reservationIdObj != null) {
            if (reservationIdObj instanceof Long) {
                reservationId = (Long) reservationIdObj;
            } else if (reservationIdObj instanceof String) {
                String reservationIdStr = (String) reservationIdObj;
                if (reservationIdStr.startsWith("C")) {
                    reservationId = Long.valueOf(reservationIdStr.substring(1));
                } else {
                    reservationId = Long.valueOf(reservationIdStr);
                }
            }
        }
        
        if (reservationId == null) {
            // FlashAttribute가 없는 경우 기본값 설정
            reservationId = System.currentTimeMillis();
        }
        
        model.addAttribute("reservationId", reservationId);
        
        try {
            // 실제 예약 정보를 DB에서 조회
            CarReservationVO reservation = carService.getReservation(reservationId);
            if (reservation != null) {
                // 차량 정보도 함께 조회
                CarVO car = carService.getCar(reservation.getCarId());
                model.addAttribute("reservation", reservation);
                model.addAttribute("car", car);
            } else {
                // 예약 정보가 없는 경우 기본 데이터 설정
                setDefaultReservationData(model, reservationId);
            }
        } catch (Exception e) {
            log.error("예약 정보 조회 중 오류 발생", e);
            // 오류 발생 시 기본 데이터 설정
            setDefaultReservationData(model, reservationId);
        }
        
        return "views/car/car-reservation-complete";
    }
    
    private void setDefaultReservationData(Model model, Long reservationId) {
        // 예시 데이터 (실제로는 예약 정보를 DB에서 조회)
        CarReservationVO reservation = new CarReservationVO();
        reservation.setReservationId(reservationId);
        reservation.setPickupDate(java.time.LocalDate.now().plusDays(1));
        reservation.setReturnDate(java.time.LocalDate.now().plusDays(3));
        reservation.setDriverName("홍길동");
        
        CarVO car = new CarVO();
        car.setCarName("현대 아반떼");
        car.setCarType("경차");
        car.setCarFuel("휘발유");
        car.setCarSeats(5);
        car.setCarPrice(50000);
        
        model.addAttribute("reservation", reservation);
        model.addAttribute("car", car);
    }

    @GetMapping("/my-reservations")
    public String myReservations(Model model) {
        try {
            // 현재 로그인한 사용자의 예약 목록 조회
            // MemberVO currentUser = getCurrentUser();
            // List<CarReservationVO> reservations = carService.getReservationsByMember(currentUser.getMemberId());
            
            // model.addAttribute("reservations", reservations);
            return "views/car/my-reservations";
        } catch (Exception e) {
            log.error("예약 목록 조회 중 오류 발생", e);
            model.addAttribute("error", "예약 목록을 불러올 수 없습니다.");
            return "views/common/error";
        }
    }
} 