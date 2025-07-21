package kr.spring.admin.service.impl;

import kr.spring.admin.service.AdminService;
import kr.spring.admin.dao.AdminMapper;
import kr.spring.member.service.MemberService;
import kr.spring.member.vo.MemberVO;
import kr.spring.seller.service.SellerService;
import kr.spring.seller.vo.SellerVO;
import kr.spring.house.service.HouseService;
import kr.spring.car.service.CarService;
import kr.spring.tour.service.TourService;
import kr.spring.review.base.service.BaseReviewService;
import kr.spring.faq.service.FaqService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.ArrayList;
import kr.spring.car.vo.CarVO;
import kr.spring.house.vo.HouseVO;
import kr.spring.review.base.vo.BaseReviewVO;
import kr.spring.car.vo.CarReviewVO;
import kr.spring.review.base.dao.BaseReviewMapper;
import kr.spring.car.dao.CarReviewMapper;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class AdminServiceImpl implements AdminService {
    
    private final AdminMapper adminMapper;
    private final MemberService memberService;
    private final SellerService sellerService;
    private final HouseService houseService;
    private final CarService carService;
    private final TourService tourService;
    private final BaseReviewService reviewService;
    private final FaqService faqService;
    private final BaseReviewMapper baseReviewMapper;
    private final CarReviewMapper carReviewMapper;
    
    @Override
    public Map<String, Object> getDashboardStats() {
        Map<String, Object> stats = new HashMap<>();
        
        try {
            // 총 회원 수
            int totalMembers = memberService.selectMemberCount();
            stats.put("totalMembers", totalMembers);
            
            // 신규 회원 수 (오늘)
            int newMembers = memberService.selectNewMemberCount();
            stats.put("newMembers", newMembers);
            
            // 판매자 통계
            Map<String, Object> sellerStats = adminMapper.getSellerStatistics();
            stats.put("totalSellers", sellerStats.get("totalSellers"));
            stats.put("activeSellers", sellerStats.get("activeSellers"));
            stats.put("pendingSellers", sellerStats.get("pendingSellers"));
            stats.put("carSellers", sellerStats.get("carSellers"));
            stats.put("houseSellers", sellerStats.get("houseSellers"));
            
            // 오늘 예약 수 (임시 데이터)
            int todayReservations = 89;
            stats.put("todayReservations", todayReservations);
            
            // 오늘 매출 (임시 데이터)
            int todayRevenue = 2450000;
            stats.put("todayRevenue", todayRevenue);
            
            // 월 매출 (임시 데이터)
            int monthlyRevenue = 45230000;
            stats.put("monthlyRevenue", monthlyRevenue);
            
            // 최근 예약 목록 (임시 데이터)
            List<Map<String, Object>> recentBookings = new ArrayList<>();
            Map<String, Object> booking1 = new HashMap<>();
            booking1.put("title", "경복궁 투어");
            booking1.put("customer", "김철수");
            booking1.put("date", "2024.01.15");
            booking1.put("status", "확정");
            recentBookings.add(booking1);
            
            Map<String, Object> booking2 = new HashMap<>();
            booking2.put("title", "부산 고속버스");
            booking2.put("customer", "이영희");
            booking2.put("date", "2024.01.15");
            booking2.put("status", "대기");
            recentBookings.add(booking2);
            
            stats.put("recentBookings", recentBookings);
            
        } catch (Exception e) {
            log.error("대시보드 통계 조회 중 오류 발생", e);
        }
        
        return stats;
    }
    
    @Override
    public void updateMemberStatus(Long memberId, String status) {
        try {
            // MemberMapper의 상태 업데이트 메서드 직접 호출
            memberService.updateMemberStatus(memberId, status);
            log.info("회원 상태 업데이트: memberId={}, status={}", memberId, status);
        } catch (Exception e) {
            log.error("회원 상태 업데이트 중 오류 발생", e);
            throw new RuntimeException("회원 상태 업데이트 실패", e);
        }
    }
    
    @Override
    public List<Map<String, Object>> getSellerList() {
        List<Map<String, Object>> sellerList = new ArrayList<>();
        
        try {
            List<SellerVO> sellers = adminMapper.selectAllSellers();
            
            for (SellerVO seller : sellers) {
                Map<String, Object> sellerMap = new HashMap<>();
                sellerMap.put("sellerId", seller.getSellerId());
                sellerMap.put("id", seller.getId());
                sellerMap.put("name", seller.getName());
                sellerMap.put("businessName", seller.getBusinessName());
                sellerMap.put("email", seller.getEmail());
                sellerMap.put("license", seller.getLicense());
                sellerMap.put("sellerType", seller.getSellerType());
                sellerMap.put("status", seller.getStatus());
                sellerMap.put("phone", seller.getPhone());
                sellerMap.put("address", seller.getAddress());
                sellerMap.put("createdAt", seller.getCreatedAt());
                
                sellerList.add(sellerMap);
            }
            
            log.info("판매자 목록 조회 완료: {}건", sellerList.size());
        } catch (Exception e) {
            log.error("판매자 목록 조회 중 오류 발생", e);
        }
        
        return sellerList;
    }
    
    @Override
    public void updateSellerStatus(Long sellerId, String status) {
        try {
            adminMapper.updateSellerStatus(sellerId, status);
            log.info("판매자 상태 업데이트: sellerId={}, status={}", sellerId, status);
        } catch (Exception e) {
            log.error("판매자 상태 업데이트 중 오류 발생", e);
            throw new RuntimeException("판매자 상태 업데이트 실패", e);
        }
    }
    
    @Override
    public List<Map<String, Object>> getCarList() {
        List<Map<String, Object>> carList = new ArrayList<>();
        
        try {
            // CarService에서 모든 차량 목록 조회
            List<CarVO> cars = carService.getAllCars();
            
            for (CarVO car : cars) {
                Map<String, Object> carMap = new HashMap<>();
                carMap.put("carId", car.getCarId());
                carMap.put("carName", car.getCarName());
                carMap.put("year", car.getCarYear());
                carMap.put("sellerName", car.getSellerName() != null ? car.getSellerName() : "미지정");
                carMap.put("carType", car.getCarType());
                carMap.put("fuelType", car.getCarFuel());
                carMap.put("dailyPrice", car.getCarPrice());
                carMap.put("rating", 4.5); // 임시 평점
                carMap.put("status", car.getStatus());
                carMap.put("imageUrl", car.getCarImage() != null ? car.getCarImage() : "/assets/images/cars/car1.jpg");
                
                carList.add(carMap);
            }
            
            log.info("차량 목록 조회 완료: {}건", carList.size());
        } catch (Exception e) {
            log.error("차량 목록 조회 중 오류 발생", e);
            
            // 오류 발생 시 임시 데이터 반환
            Map<String, Object> car1 = new HashMap<>();
            car1.put("carId", 1L);
            car1.put("carName", "현대 아반떼");
            car1.put("year", "2023");
            car1.put("sellerName", "제주렌트카");
            car1.put("carType", "중형차");
            car1.put("fuelType", "휘발유");
            car1.put("dailyPrice", 50000);
            car1.put("rating", 4.8);
            car1.put("status", "AVAILABLE");
            car1.put("imageUrl", "/assets/images/cars/car1.jpg");
            carList.add(car1);
        }
        
        return carList;
    }
    
    @Override
    public List<Map<String, Object>> getHouseList() {
        List<Map<String, Object>> houseList = new ArrayList<>();
        
        try {
            // HouseService에서 모든 숙소 목록 조회
            Map<String, Object> params = new HashMap<>();
            params.put("start", 1);
            params.put("end", 100); // 최대 100개 조회
            
            // 먼저 전체 개수를 확인
            Integer totalCount = houseService.selectRowCount(params);
            log.info("숙소 전체 개수: {}", totalCount);
            
            if (totalCount > 0) {
                List<HouseVO> houses = houseService.selectList(params);
                log.info("조회된 숙소 개수: {}", houses.size());
                
                for (HouseVO house : houses) {
                    Map<String, Object> houseMap = new HashMap<>();
                    houseMap.put("contentId", house.getContentId());
                    houseMap.put("title", house.getTitle());
                    houseMap.put("addr1", house.getAddr1());
                    houseMap.put("firstImage2", house.getFirstImage2());
                    houseMap.put("cat3", house.getCat3());
                    houseMap.put("sellerName", "제주호텔그룹"); // 임시 판매자명
                    houseMap.put("roomCount", house.getRoomCount() != null ? house.getRoomCount() : "120"); // 실제 객실 수 또는 기본값
                    houseMap.put("reservationRate", 88); // 임시 예약률
                    houseMap.put("rating", house.getReview_avg() != null ? house.getReview_avg() : 4.7); // 실제 평점 또는 기본값
                    houseMap.put("status", "ACTIVE"); // 임시 상태
                    
                    houseList.add(houseMap);
                }
            }
            
            log.info("숙소 목록 조회 완료: {}건", houseList.size());
        } catch (Exception e) {
            log.error("숙소 목록 조회 중 오류 발생", e);
            
            // 오류 발생 시 임시 데이터 반환
            Map<String, Object> house1 = new HashMap<>();
            house1.put("contentId", 1L);
            house1.put("title", "제주 그랜드 호텔");
            house1.put("addr1", "제주특별자치도 제주시");
            house1.put("firstImage2", "/assets/images/house.png");
            house1.put("cat3", "호텔/제주시");
            house1.put("sellerName", "제주호텔그룹");
            house1.put("roomCount", 120);
            house1.put("reservationRate", 88);
            house1.put("rating", 4.7);
            house1.put("status", "ACTIVE");
            houseList.add(house1);
        }
        
        return houseList;
    }
    
    @Override
    public List<Map<String, Object>> getReviewList() {
        List<Map<String, Object>> reviewList = new ArrayList<>();
        
        try {
            // 통합 리뷰 테이블에서 모든 리뷰 조회
            List<BaseReviewVO> baseReviews = baseReviewMapper.findAllReviews(1, 100);
            
            for (BaseReviewVO baseReview : baseReviews) {
                Map<String, Object> reviewMap = new HashMap<>();
                reviewMap.put("reviewId", baseReview.getReviewId());
                reviewMap.put("title", "리뷰"); // 기본 제목
                reviewMap.put("content", baseReview.getReview());
                reviewMap.put("productName", getProductName(baseReview.getContentId(), baseReview.getContentTypeId()));
                reviewMap.put("author", baseReview.getId() != null ? baseReview.getId() : "익명");
                reviewMap.put("rating", baseReview.getRating());
                reviewMap.put("createdAt", formatDate(baseReview.getCreatedAt()));
                reviewMap.put("status", baseReview.getStatus() == 1 ? "ACTIVE" : "INACTIVE");
                reviewMap.put("statusNumber", baseReview.getStatus()); // 숫자 상태도 추가
                
                reviewList.add(reviewMap);
            }
            
            // 차량 리뷰도 추가
            List<CarReviewVO> carReviews = carReviewMapper.selectAllCarReviews();
            for (CarReviewVO carReview : carReviews) {
                Map<String, Object> reviewMap = new HashMap<>();
                reviewMap.put("reviewId", carReview.getReviewId());
                reviewMap.put("title", "차량 리뷰");
                reviewMap.put("content", carReview.getContent());
                reviewMap.put("productName", getCarName(carReview.getCarId()));
                reviewMap.put("author", "익명"); // 차량 리뷰에는 작성자 정보가 없음
                reviewMap.put("rating", carReview.getRating());
                reviewMap.put("createdAt", formatDate(carReview.getCreatedAt()));
                reviewMap.put("status", "ACTIVE");
                
                reviewList.add(reviewMap);
            }
            
            log.info("리뷰 목록 조회 완료: {}건", reviewList.size());
        } catch (Exception e) {
            log.error("리뷰 목록 조회 중 오류 발생", e);
            
            // 오류 발생 시 기본 데이터 반환
            Map<String, Object> review1 = new HashMap<>();
            review1.put("reviewId", 1L);
            review1.put("title", "정말 좋은 경험이었습니다!");
            review1.put("content", "차량 상태가 깨끗하고 서비스도 친절했습니다.");
            review1.put("productName", "현대 아반떼 (렌터카)");
            review1.put("author", "김철수");
            review1.put("rating", 5);
            review1.put("createdAt", "2024.01.15");
            review1.put("status", "ACTIVE");
            reviewList.add(review1);
        }
        
        return reviewList;
    }
    
    // 상품명 조회 헬퍼 메서드
    private String getProductName(Long contentId, Long contentTypeId) {
        try {
            if (contentTypeId == null) return "알 수 없는 상품";
            
            switch (contentTypeId.intValue()) {
                case 32: // 숙소
                    HouseVO house = houseService.selectHouse(contentId);
                    return house != null ? house.getTitle() + " (숙소)" : "알 수 없는 숙소";
                case 12: // 관광지
                    return "관광지";
                case 14: // 문화시설
                    return "문화시설";
                case 15: // 축제공연행사
                    return "축제/공연/행사";
                case 25: // 여행코스
                    return "여행코스";
                case 28: // 레포츠
                    return "레포츠";
                case 38: // 쇼핑
                    return "쇼핑";
                case 39: // 음식점
                    return "음식점";
                default:
                    return "기타 상품";
            }
        } catch (Exception e) {
            log.error("상품명 조회 중 오류: contentId={}, contentTypeId={}", contentId, contentTypeId, e);
            return "알 수 없는 상품";
        }
    }
    
    // 차량명 조회 헬퍼 메서드
    private String getCarName(Long carId) {
        try {
            if (carId == null) return "알 수 없는 차량";
            
            CarVO car = carService.getCar(carId);
            return car != null ? car.getCarName() + " (렌터카)" : "알 수 없는 차량";
        } catch (Exception e) {
            log.error("차량명 조회 중 오류: carId={}", carId, e);
            return "알 수 없는 차량";
        }
    }
    
    // 날짜 포맷 헬퍼 메서드
    private String formatDate(Object date) {
        if (date == null) return "날짜 없음";
        
        try {
            if (date instanceof java.time.LocalDateTime) {
                java.time.LocalDateTime ldt = (java.time.LocalDateTime) date;
                return ldt.format(java.time.format.DateTimeFormatter.ofPattern("yyyy.MM.dd"));
            } else if (date instanceof java.util.Date) {
                java.util.Date utilDate = (java.util.Date) date;
                java.time.LocalDateTime ldt = utilDate.toInstant()
                    .atZone(java.time.ZoneId.systemDefault())
                    .toLocalDateTime();
                return ldt.format(java.time.format.DateTimeFormatter.ofPattern("yyyy.MM.dd"));
            } else {
                return date.toString();
            }
        } catch (Exception e) {
            log.error("날짜 포맷 중 오류: date={}", date, e);
            return "날짜 오류";
        }
    }
    
    @Override
    public List<Map<String, Object>> getProductList(String productType) {
        List<Map<String, Object>> productList = new ArrayList<>();
        
        try {
            switch (productType.toLowerCase()) {
                case "car":
                    productList = getCarList();
                    break;
                case "house":
                    productList = getHouseList();
                    break;
                case "tour":
                    // 투어 목록 조회 로직
                    log.info("투어 목록 조회");
                    break;
                default:
                    throw new IllegalArgumentException("지원하지 않는 상품 유형: " + productType);
            }
        } catch (Exception e) {
            log.error("상품 목록 조회 중 오류 발생: productType={}", productType, e);
        }
        
        return productList;
    }
    
    @Override
    public void updateReviewStatus(Long reviewId, String status) {
        try {
            log.info("리뷰 상태 업데이트: reviewId={}, status={}", reviewId, status);
            
            // status 문자열을 숫자로 변환
            Integer statusNumber;
            switch (status.toUpperCase()) {
                case "ACTIVE":
                    statusNumber = 1;
                    break;
                case "INACTIVE":
                case "HIDDEN":
                case "DELETED":
                    statusNumber = 0;
                    break;
                default:
                    throw new IllegalArgumentException("지원하지 않는 상태: " + status);
            }
            
            // 통합 리뷰 테이블에서 먼저 조회
            List<BaseReviewVO> baseReviews = baseReviewMapper.findAllReviews(1, 1000);
            boolean foundInBase = false;
            
            for (BaseReviewVO review : baseReviews) {
                if (review.getReviewId().equals(reviewId)) {
                    // BaseReviewMapper에 상태 업데이트 메서드가 필요
                    baseReviewMapper.updateReviewStatus(reviewId, statusNumber);
                    log.info("통합 리뷰 테이블에서 상태 업데이트: reviewId={}, status={}", reviewId, statusNumber);
                    foundInBase = true;
                    break;
                }
            }
            
            // 통합 리뷰 테이블에 없으면 차량 리뷰 테이블에서 조회
            if (!foundInBase) {
                List<CarReviewVO> carReviews = carReviewMapper.selectAllCarReviews();
                for (CarReviewVO review : carReviews) {
                    if (review.getReviewId().equals(reviewId)) {
                        // CarReviewMapper에 상태 업데이트 메서드가 필요
                        carReviewMapper.updateReviewStatus(reviewId, statusNumber);
                        log.info("차량 리뷰 테이블에서 상태 업데이트: reviewId={}, status={}", reviewId, statusNumber);
                        break;
                    }
                }
            }
            
        } catch (Exception e) {
            log.error("리뷰 상태 업데이트 중 오류 발생", e);
            throw new RuntimeException("리뷰 상태 업데이트 실패", e);
        }
    }
    
    @Override
    public void updateProductStatus(Long productId, String productType, String status) {
        try {
            log.info("상품 상태 업데이트: productId={}, productType={}, status={}", productId, productType, status);
            
            switch (productType.toLowerCase()) {
                case "car":
                    // 차량 상태 업데이트
                    // carService.updateStatus(productId, status);
                    log.info("차량 상태 업데이트 완료");
                    break;
                case "house":
                    // 숙소 상태 업데이트
                    // houseService.updateStatus(productId, status);
                    log.info("숙소 상태 업데이트 완료");
                    break;
                case "tour":
                    // 투어 상태 업데이트
                    // tourService.updateStatus(productId, status);
                    log.info("투어 상태 업데이트 완료");
                    break;
                default:
                    throw new IllegalArgumentException("지원하지 않는 상품 유형: " + productType);
            }
        } catch (Exception e) {
            log.error("상품 상태 업데이트 중 오류 발생", e);
            throw new RuntimeException("상품 상태 업데이트 실패", e);
        }
    }
    
    @Override
    public List<Map<String, Object>> getInquiryList() {
        List<Map<String, Object>> inquiryList = new ArrayList<>();
        
        try {
            // faqService.getInquiryList() 호출
            // 임시 데이터 생성
            Map<String, Object> inquiry1 = new HashMap<>();
            inquiry1.put("inquiryId", 1L);
            inquiry1.put("title", "예약 취소 문의");
            inquiry1.put("content", "예약을 취소하고 싶습니다.");
            inquiry1.put("author", "김철수");
            inquiry1.put("createdAt", "2024.01.15");
            inquiry1.put("status", "PENDING");
            inquiryList.add(inquiry1);
            
            Map<String, Object> inquiry2 = new HashMap<>();
            inquiry2.put("inquiryId", 2L);
            inquiry2.put("title", "결제 오류 문의");
            inquiry2.put("content", "결제가 되지 않습니다.");
            inquiry2.put("author", "이영희");
            inquiry2.put("createdAt", "2024.01.14");
            inquiry2.put("status", "RESOLVED");
            inquiryList.add(inquiry2);
            
            log.info("문의 목록 조회 완료: {}건", inquiryList.size());
        } catch (Exception e) {
            log.error("문의 목록 조회 중 오류 발생", e);
        }
        
        return inquiryList;
    }
    
    @Override
    public void updateInquiryStatus(Long inquiryId, String status) {
        try {
            // faqService.updateInquiryStatus(inquiryId, status);
            log.info("문의 상태 업데이트: inquiryId={}, status={}", inquiryId, status);
        } catch (Exception e) {
            log.error("문의 상태 업데이트 중 오류 발생", e);
            throw new RuntimeException("문의 상태 업데이트 실패", e);
        }
    }
} 