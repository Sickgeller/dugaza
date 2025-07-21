package kr.spring.house.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import kr.spring.house.service.HouseService;
import kr.spring.house.vo.HouseVO;
import kr.spring.util.PagingUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Controller
//@PreAuthorize("hasRole('ADMIN')")
@RequestMapping("/house")
@RequiredArgsConstructor
public class HouseAdminController {

	@Autowired
	private HouseService houseService;

	// 관리자 숙소 페이지 이동
	@GetMapping("/admin_house")
	public String form(
			@RequestParam(name = "pageNum", defaultValue="1") int pageNum,
			@RequestParam(name = "keyword", required = false) String keyword,
			@RequestParam(name = "category", required = false) String category,
			@RequestParam(name = "status", required = false) String status,
			@RequestParam(name = "sort", required = false) String sort,
			Model model) {
		
		log.info("숙소 관리 페이지 요청: pageNum={}, keyword={}, category={}, status={}, sort={}", 
				pageNum, keyword, category, status, sort);
		
		// 검색 조건을 Map에 담기
		Map<String, Object> searchMap = new HashMap<>();
		if (keyword != null && !keyword.trim().isEmpty()) {
			searchMap.put("keyword", keyword.trim());
		}
		if (category != null && !category.trim().isEmpty()) {
			searchMap.put("category", category.trim());
		}
		if (status != null && !status.trim().isEmpty()) {
			searchMap.put("status", status.trim());
		}
		if (sort != null && !sort.trim().isEmpty()) {
			searchMap.put("sort", sort.trim());
		}
		
		log.info("검색 조건: {}", searchMap);
		
		int count = houseService.selectRowCount(searchMap);
		log.info("총 숙소 개수: {}", count);
		
		//페이지 처리
		PagingUtil page = new PagingUtil(null,null,
				pageNum,count,10,10,
				"admin_house");
		Map<String,Object> map = 
				new HashMap<String,Object>();
		List<HouseVO> list = null;
		if(count > 0) {
			map.put("start", page.getStartRow());
			map.put("end", page.getEndRow());
			// 검색 조건 추가
			map.putAll(searchMap);
			
			list = houseService.selectAdminList(map);
			log.info("조회된 숙소 개수: {}", list != null ? list.size() : 0);
		}
		
		// HouseVO를 Map으로 변환하여 템플릿에서 필요한 필드들 추가
		List<Map<String, Object>> houseList = new ArrayList<>();
		if (list != null) {
			for (HouseVO house : list) {
				Map<String, Object> houseMap = new HashMap<>();
				houseMap.put("contentId", house.getContentId());
				houseMap.put("title", house.getTitle());
				houseMap.put("addr1", house.getAddr1());
				houseMap.put("firstImage2", house.getFirstImage2());
				houseMap.put("cat3", house.getCat3());
				houseMap.put("sellerName", "미등록"); // 기본값
				houseMap.put("roomCount", house.getRoomCount() != null ? house.getRoomCount() : "0");
				houseMap.put("reservationRate", "85"); // 기본값
				houseMap.put("rating", house.getReview_avg() != null ? String.format("%.1f", house.getReview_avg()) : "0.0");
				houseMap.put("status", house.getStatus() != null ? house.getStatus() : "suspending");
				houseList.add(houseMap);
			}
		}
		
		model.addAttribute("count", count);
		model.addAttribute("houseList", houseList);
		model.addAttribute("page", page.getPage());
		
		// 통계 데이터 계산
		int totalCount = count;
		int availableCount = 0;
		int suspendingCount = 0;
		int inavailableCount = 0;
		int repairedCount = 0;
		double totalRating = 0.0;
		int ratingCount = 0;
		
		if (list != null) {
			for (HouseVO house : list) {
				String houseStatus = house.getStatus() != null ? house.getStatus() : "suspending";
				if ("AVAILABLE".equals(houseStatus)) {
					availableCount++;
				} else if ("suspending".equals(houseStatus)) {
					suspendingCount++;
				} else if ("INAVAILABLE".equals(houseStatus)) {
					inavailableCount++;
				} else if ("REPAIRED".equals(houseStatus)) {
					repairedCount++;
				}
				
				if (house.getReview_avg() != null) {
					totalRating += house.getReview_avg();
					ratingCount++;
				}
			}
		}
		
		// 전체 통계를 위해 별도 쿼리 실행
		Map<String, Object> statsMap = new HashMap<>();
		List<HouseVO> allHouses = houseService.selectAdminList(statsMap);
		int totalAvailable = 0;
		int totalSuspending = 0;
		int totalInavailable = 0;
		int totalRepaired = 0;
		double totalAvgRating = 0.0;
		int totalRatingCount = 0;
		
		if (allHouses != null) {
			for (HouseVO house : allHouses) {
				String houseStatus = house.getStatus() != null ? house.getStatus() : "suspending";
				if ("AVAILABLE".equals(houseStatus)) {
					totalAvailable++;
				} else if ("suspending".equals(houseStatus)) {
					totalSuspending++;
				} else if ("INAVAILABLE".equals(houseStatus)) {
					totalInavailable++;
				} else if ("REPAIRED".equals(houseStatus)) {
					totalRepaired++;
				}
				
				if (house.getReview_avg() != null) {
					totalAvgRating += house.getReview_avg();
					totalRatingCount++;
				}
			}
		}
		
		double avgRating = totalRatingCount > 0 ? totalAvgRating / totalRatingCount : 0.0;
		double availablePercentage = totalCount > 0 ? (double) totalAvailable / totalCount * 100 : 0.0;
		double repairedPercentage = totalCount > 0 ? (double) totalRepaired / totalCount * 100 : 0.0;
		
		log.info("통계 계산 결과: 총숙소={}, 운영중={}, 승인대기={}, 운영정지={}, 점검중={}, 평균평점={}",
				totalCount, totalAvailable, totalSuspending, totalInavailable, totalRepaired, avgRating);
		
		// 검색 조건들을 모델에 추가
		model.addAttribute("keyword", keyword);
		model.addAttribute("category", category);
		model.addAttribute("status", status);
		model.addAttribute("sort", sort);
		
		// 통계 데이터를 모델에 추가
		model.addAttribute("totalCount", totalCount);
		model.addAttribute("availableCount", totalAvailable);
		model.addAttribute("suspendingCount", totalSuspending);
		model.addAttribute("inavailableCount", totalInavailable);
		model.addAttribute("repairedCount", totalRepaired);
		model.addAttribute("avgRating", String.format("%.1f", avgRating));
		model.addAttribute("availablePercentage", String.format("%.0f", availablePercentage));
		model.addAttribute("repairedPercentage", String.format("%.0f", repairedPercentage));

		return "views/admin/super-admin-accommodations";
	}
}
