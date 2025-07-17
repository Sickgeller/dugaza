package kr.spring.seller.controller;

import kr.spring.car.service.CarService;
import kr.spring.car.service.CarReservationService;
import kr.spring.car.service.CarReviewService;
import kr.spring.car.dto.CarRegisterDTO;
import kr.spring.car.vo.CarVO;
import kr.spring.car.vo.CarReservationVO;
import kr.spring.car.vo.CarReviewVO;
import kr.spring.common.SellerType;
import kr.spring.seller.vo.SellerVO;
import kr.spring.util.FileUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import jakarta.servlet.http.HttpSession;
import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.util.Arrays;

@RequestMapping("/seller")
@Controller
@Slf4j
@RequiredArgsConstructor
public class SellerCarController {

    private final CarService carService;
    private final CarReservationService carReservationService;
    private final CarReviewService carReviewService;
    


    @GetMapping("/car/dashboard")
    public String dashboard(Model model) {
        return main(model);
    }
    
    @GetMapping("/car/")
    public String main(Model model) {
        try {
            SellerVO seller = (SellerVO) model.getAttribute("seller");
            if (seller != null && SellerType.CAR.getValue().equals(seller.getSellerType())) {
                List<CarVO> carList = carService.getCarListBySeller(seller.getSellerId());
                
                int totalCars = carList.size();
                int availableCars = (int) carList.stream().filter(car -> "AVAILABLE".equals(car.getStatus())).count();
                int rentedCars = totalCars - availableCars;
                int rentalRate = totalCars > 0 ? (int) Math.round((double) rentedCars / totalCars * 100) : 0;

                // 대시보드 통계 데이터
                model.addAttribute("totalCars", totalCars);
                model.addAttribute("availableCars", availableCars);
                model.addAttribute("rentedCars", rentedCars);
                model.addAttribute("rentalRate", rentalRate);

                // 최근 예약 정보 (최대 5개)
                List<CarReservationVO> reservationList = carReservationService.getReservationsBySeller(seller.getSellerId());
                model.addAttribute("recentReservations", reservationList);

                // 현재 메뉴 설정
                model.addAttribute("currentMenu", "dashboard");

                log.info("차량 판매자 대시보드 - 총 차량: {}, 대여중: {}, 대여률: {}%, 최근 예약: {}건",
                        totalCars, rentedCars, rentalRate, reservationList.size());

                return "views/seller/car/car-seller-main";
            } else {
                log.warn("잘못된 판매자 타입: {}", seller != null ? seller.getSellerType() : "null");
                model.addAttribute("error", "차량 판매자만 접근할 수 있습니다.");
                return "views/common/error";
            }
        } catch (Exception e) {
            log.error("차량 판매자 대시보드 처리 중 오류 발생", e);
            model.addAttribute("error", "대시보드 정보를 불러올 수 없습니다.");
            return "views/common/error";
        }
    }

    @GetMapping("/car/management")
    public String cars(Model model, @RequestParam(name = "page", defaultValue = "1") int page) {
        SellerVO seller = (SellerVO) model.getAttribute("seller");
        if (seller == null) return "redirect:/seller/login";
        model.addAttribute("seller", seller);

        // 페이징 처리를 위한 설정
        int pageSize = 10;
        int currentPage = Math.max(1, page);

        // 차량 목록 조회
        List<CarVO> cars = carService.getCarListBySeller(seller.getSellerId());
        int totalCars = cars.size();
        int availableCars = (int) cars.stream().filter(car -> "AVAILABLE".equals(car.getStatus())).count();

        // 모델에 데이터 추가
        model.addAttribute("cars", cars);
        model.addAttribute("totalCars", totalCars);
        model.addAttribute("availableCars", availableCars);
        model.addAttribute("currentPage", currentPage);
        model.addAttribute("totalPages", (int) Math.ceil((double) totalCars / pageSize));
        model.addAttribute("seller", seller);
        model.addAttribute("currentMenu", "management");

        return "views/seller/car/car-management";
    }

    @GetMapping("/car/register")
    public String registerForm(Model model) {
        SellerVO seller = (SellerVO) model.getAttribute("seller");
        if (seller == null) return "redirect:/seller/login";
        model.addAttribute("seller", seller);
        model.addAttribute("currentMenu", "register");
        return "views/seller/car/car-register";
    }

    @PostMapping("/car/register")
    public String registerCar(CarRegisterDTO carRegisterDTO, Model model, RedirectAttributes redirectAttributes) {
        try {
            SellerVO seller = (SellerVO) model.getAttribute("seller");
            if (seller == null) return "redirect:/seller/login";

            // CarVO 객체 생성
            CarVO carVO = CarVO.builder()
                    .carName(carRegisterDTO.getCarName())
                    .carType(carRegisterDTO.getCarType())
                    .carNumber(carRegisterDTO.getCarNumber())
                    .carYear(carRegisterDTO.getCarYear())
                    .carColor(carRegisterDTO.getCarColor())
                    .carFuel(carRegisterDTO.getCarFuel())
                    .carSeats(carRegisterDTO.getCarSeats())
                    .carPrice(carRegisterDTO.getCarPrice())
                    .sellerId(seller.getSellerId())
                    .status("AVAILABLE")
                    .build();

            // 파일 업로드 처리
            if (carRegisterDTO.getCarImage() != null && !carRegisterDTO.getCarImage().isEmpty()) {
                try {
                    String imagePath = FileUtil.uploadFile(carRegisterDTO.getCarImage(), "car");
                    carVO.setCarImage(imagePath);
                    log.info("차량 이미지 업로드 성공: {}", imagePath);
                } catch (Exception e) {
                    log.error("차량 이미지 업로드 실패", e);
                    redirectAttributes.addFlashAttribute("error", "이미지 업로드에 실패했습니다.");
                    return "redirect:/seller/car/register";
                }
            }

            carService.registerCar(carVO);

            redirectAttributes.addFlashAttribute("message", "차량이 성공적으로 등록되었습니다.");
            log.info("차량 등록 성공: sellerId = {}, carName = {}, carId = {}", seller.getSellerId(), carVO.getCarName(), carVO.getCarId());

            return "redirect:/seller/car/management";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "차량 등록에 실패했습니다.");
            log.error("차량 등록 중 예외 발생", e);
            return "redirect:/seller/car/register";
        }
    }

    @GetMapping("/car/edit/{carId}")
    public String editForm(@PathVariable("carId") Long carId, Model model) {
        try {
            CarVO car = carService.getCar(carId);
            if (car == null) {
                model.addAttribute("error", "차량을 찾을 수 없습니다.");
                return "views/common/error";
            }

            model.addAttribute("car", car);
            model.addAttribute("currentMenu", "edit");
            return "views/seller/car/car-edit";
        } catch (Exception e) {
            log.error("차량 수정 폼 로드 중 오류 발생", e);
            model.addAttribute("error", "차량 정보를 불러올 수 없습니다.");
            return "views/common/error";
        }
    }

    @PostMapping("/car/edit")
    public String editCar(CarVO carVO, @RequestParam(value = "carImageFile", required = false) MultipartFile carImage, Model model, RedirectAttributes redirectAttributes) {
        try {
            // 기존 차량 정보 조회
            CarVO existingCar = carService.getCar(carVO.getCarId());
            if (existingCar == null) {
                redirectAttributes.addFlashAttribute("error", "차량을 찾을 수 없습니다.");
                return "redirect:/seller/car/management";
            }

            // 새 이미지가 업로드된 경우 처리
            if (carImage != null && !carImage.isEmpty()) {
                try {
                    String imagePath = FileUtil.uploadFile(carImage, "car");
                    carVO.setCarImage(imagePath);
                    log.info("차량 이미지 업로드 성공: {}", imagePath);
                } catch (Exception e) {
                    log.error("차량 이미지 업로드 실패", e);
                    redirectAttributes.addFlashAttribute("error", "이미지 업로드에 실패했습니다.");
                    return "redirect:/seller/car/edit/" + carVO.getCarId();
                }
            } else {
                // 기존 이미지 유지
                carVO.setCarImage(existingCar.getCarImage());
            }

            carService.updateCar(carVO);
            redirectAttributes.addFlashAttribute("message", "차량 정보가 성공적으로 수정되었습니다.");
            log.info("차량 수정 성공: carId = {}", carVO.getCarId());
            return "redirect:/seller/car/management";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "차량 수정에 실패했습니다.");
            log.error("차량 수정 중 예외 발생", e);
            return "redirect:/seller/car/edit/" + carVO.getCarId();
        }
    }

    @PostMapping("/car/delete/{carId}")
    public String deleteCar(@PathVariable("carId") Long carId, Model model, RedirectAttributes redirectAttributes) {
        try {
            carService.deleteCar(carId);
            redirectAttributes.addFlashAttribute("message", "차량이 성공적으로 삭제되었습니다.");
            log.info("차량 삭제 성공: carId = {}", carId);
            return "redirect:/seller/car/management";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "차량 삭제에 실패했습니다.");
            log.error("차량 삭제 중 예외 발생", e);
            return "redirect:/seller/car/management";
        }
    }

    @GetMapping("/car/reservations")
    public String reservations(Model model) {
        try {
            SellerVO seller = (SellerVO) model.getAttribute("seller");
            if (seller == null) return "redirect:/seller/login";

            List<CarReservationVO> reservations = carReservationService.getReservationsBySeller(seller.getSellerId());
            
            // 데이터 확인을 위한 로그 추가
            log.info("예약 목록 조회 - sellerId: {}, 예약 개수: {}", seller.getSellerId(), reservations.size());
            if (!reservations.isEmpty()) {
                CarReservationVO firstReservation = reservations.get(0);
                log.info("첫 번째 예약 데이터 - reservationId: {}, carName: {}, memberName: {}, startDate: {}, endDate: {}, status: {}, createdAt: {}", 
                    firstReservation.getReservationId(), firstReservation.getCarName(), firstReservation.getMemberName(),
                    firstReservation.getStartDate(), firstReservation.getEndDate(), firstReservation.getStatus(), firstReservation.getCreatedAt());
            }
            
            model.addAttribute("reservations", reservations);
            model.addAttribute("seller", seller);
            model.addAttribute("currentMenu", "reservations");

            return "views/seller/car/car-reservations";
        } catch (Exception e) {
            log.error("예약 목록 조회 중 오류 발생", e);
            model.addAttribute("error", "예약 정보를 불러올 수 없습니다.");
            return "views/common/error";
        }
    }

    @GetMapping("/car/reviews")
    public String reviews(Model model) {
        try {
            SellerVO seller = (SellerVO) model.getAttribute("seller");
            if (seller == null) return "redirect:/seller/login";

            List<CarVO> cars = carService.getCarListBySeller(seller.getSellerId());
            
            // 각 차량의 리뷰 정보 조회
            for (CarVO car : cars) {
                List<CarReviewVO> reviews = carReviewService.getReviewsByCar(car.getCarId());
                car.setReviews(reviews);
            }
            
            model.addAttribute("cars", cars);
            model.addAttribute("seller", seller);
            model.addAttribute("currentMenu", "reviews");

            return "views/seller/car/car-reviews";
        } catch (Exception e) {
            log.error("리뷰 목록 조회 중 오류 발생", e);
            model.addAttribute("error", "리뷰 정보를 불러올 수 없습니다.");
            return "views/common/error";
        }
    }

    @GetMapping("/car/sales")
    public String sales(Model model) {
        try {
            SellerVO seller = (SellerVO) model.getAttribute("seller");
            if (seller == null) return "redirect:/seller/login";

            // 매출 통계 데이터 (예시)
            Map<String, Object> salesData = new HashMap<>();
            salesData.put("totalRevenue", 15000000);
            salesData.put("monthlyRevenue", 2500000);
            salesData.put("totalReservations", 45);
            salesData.put("averageRating", 4.5);

            model.addAttribute("salesData", salesData);
            model.addAttribute("seller", seller);
            model.addAttribute("currentMenu", "sales");

            return "views/seller/car/car-sales";
        } catch (Exception e) {
            log.error("매출 정보 조회 중 오류 발생", e);
            model.addAttribute("error", "매출 정보를 불러올 수 없습니다.");
            return "views/common/error";
        }
    }

    @GetMapping("/car/analytics")
    public String analytics(Model model) {
        try {
            SellerVO seller = (SellerVO) model.getAttribute("seller");
            if (seller == null) return "redirect:/seller/login";

            // 분석 데이터 (예시)
            Map<String, Object> analyticsData = new HashMap<>();
            analyticsData.put("popularCars", Arrays.asList("아반떼", "쏘나타", "그랜저"));
            analyticsData.put("peakHours", Arrays.asList("09:00", "14:00", "18:00"));
            analyticsData.put("customerSatisfaction", 4.3);

            model.addAttribute("analyticsData", analyticsData);
            model.addAttribute("seller", seller);
            model.addAttribute("currentMenu", "analytics");

            return "views/seller/car/car-analytics";
        } catch (Exception e) {
            log.error("분석 정보 조회 중 오류 발생", e);
            model.addAttribute("error", "분석 정보를 불러올 수 없습니다.");
            return "views/common/error";
        }
    }

    @GetMapping("/car/settings")
    public String settings(Model model) {
        try {
            SellerVO seller = (SellerVO) model.getAttribute("seller");
            if (seller == null) return "redirect:/seller/login";

            model.addAttribute("seller", seller);
            model.addAttribute("currentMenu", "settings");

            return "views/seller/car/car-settings";
        } catch (Exception e) {
            log.error("설정 페이지 로드 중 오류 발생", e);
            model.addAttribute("error", "설정 페이지를 불러올 수 없습니다.");
            return "views/common/error";
        }
    }

    @PostMapping("/car/settings/update")
    public String updateSettings(@RequestParam Map<String, String> settings, Model model, RedirectAttributes redirectAttributes) {
        try {
            SellerVO seller = (SellerVO) model.getAttribute("seller");
            if (seller == null) return "redirect:/seller/login";

            // 설정 업데이트 로직 (예시)
            log.info("설정 업데이트: {}", settings);
            
            redirectAttributes.addFlashAttribute("message", "설정이 성공적으로 업데이트되었습니다.");
            return "redirect:/seller/car/settings";
        } catch (Exception e) {
            log.error("설정 업데이트 중 오류 발생", e);
            redirectAttributes.addFlashAttribute("error", "설정 업데이트에 실패했습니다.");
            return "redirect:/seller/car/settings";
        }
    }

    @GetMapping("/logout")
    public String logout(HttpSession session, RedirectAttributes redirectAttributes) {
        try {
            session.invalidate();
            redirectAttributes.addFlashAttribute("message", "로그아웃되었습니다.");
            return "redirect:/seller/login";
        } catch (Exception e) {
            log.error("로그아웃 중 오류 발생", e);
            return "redirect:/seller/login";
        }
    }
} 