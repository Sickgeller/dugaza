package kr.spring.common.search.controller;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import kr.spring.common.search.service.SearchService;
import kr.spring.tour.vo.ContentType;
import kr.spring.tour.vo.ContentTypeAdd;
import kr.spring.tour.vo.TourVO;
import lombok.extern.slf4j.Slf4j;

@Controller
@Slf4j
@RequestMapping("/search")
public class SearchController {

	@Autowired
	private SearchService searchService;

	@GetMapping("")
	public String searchResult(@RequestParam(defaultValue = "") String keyword, Model model) {
	    // 1. 실제 검색 결과 (카테고리별 최대 6개씩)
	    List<TourVO> searchResults = searchService.integratedSearch(keyword);

	    // 2. 카테고리별 총 개수
	    List<Map<String, Object>> countResults = searchService.countSearchGroupedByCategory(keyword);
	    Map<String, Integer> groupedCounts = new LinkedHashMap<>(); // 병합된 이름 기준: "touristAttraction" 등

	    for (Map<String, Object> row : countResults) {
	        Integer contentTypeId = ((BigDecimal) row.get("CONTENT_TYPE_ID")).intValue();
	        Integer total = ((BigDecimal) row.get("TOTAL")).intValue();

	        // 병합 기준에 따라 이름 가져오기
	        ContentTypeAdd type = ContentTypeAdd.fromId(contentTypeId);
	        String routeName = type.getName();

	        // 병합된 그룹이면 합산
	        groupedCounts.merge(routeName, total, Integer::sum);
	    }

	    // 3. View에 넘길 자료: 그룹별 결과 최대 6개, 병합 이름 기준으로 저장
	    Map<String, List<TourVO>> groupedResults = new LinkedHashMap<>();
	    Map<String, String> displayNames = new HashMap<>(); // 병합 이름 → 한글명 (ex. house → 숙소)

	    for (TourVO result : searchResults) {
	        int contentTypeId = result.getContentTypeId();
	        ContentTypeAdd type = ContentTypeAdd.fromId(contentTypeId);
	        String routeName = type.getName(); // 병합된 이름

	        // 6개까지만 추가
	        groupedResults.computeIfAbsent(routeName, k -> new ArrayList<>());
	        if (groupedResults.get(routeName).size() < 6) {
	            groupedResults.get(routeName).add(result);
	        }

	        // 병합된 이름 → 한글 표시
	        displayNames.put(routeName, type.getDisplayName());
	    }

	    // 4. model에 전달
	    model.addAttribute("groupedResults", groupedResults);
	    model.addAttribute("groupedCounts", groupedCounts);
	    model.addAttribute("displayNames", displayNames);
	    model.addAttribute("keyword", keyword);

	    return "views/main/search";
	}

}
