package kr.spring.restaurant.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import kr.spring.restaurant.service.RestaurantService;
import kr.spring.restaurant.vo.RestaurantVO;
import kr.spring.tour.vo.TourVO;
import kr.spring.tour.vo.TouristAttractionVO;
import kr.spring.util.PagingUtil;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Controller
@RequestMapping("/restaurant")
public class RestaurantController {

	@Autowired
	private RestaurantService restaurantService;
	@GetMapping("")
	public String restaurantMain(@RequestParam(defaultValue="1") int pageNum,
			@RequestParam(defaultValue = "") String keyword,
			Model model) {

		int count = restaurantService.selectRowCount();

		//페이지 처리
		PagingUtil page = new PagingUtil(null,keyword,
				pageNum,count,9,10,
				"");
		Map<String,Object> map = 
				new HashMap<String,Object>();
		List<TourVO> list = null;
		if(count > 0) {
			map.put("start", page.getStartRow());
			map.put("end", page.getEndRow());
			map.put("keyword", keyword);
			list = restaurantService.selectList(map);
		}
		model.addAttribute("count", count);
		model.addAttribute("list", list);
		model.addAttribute("page", page.getPage());
		return "views/restaurant/restaurant";
	}

	// 항목 자세히 보기
	@GetMapping("/detail")
	public String restaurantDetail(@RequestParam Long id, Model model) {
		RestaurantVO vo = restaurantService.selectRestaurant(id);

		model.addAttribute("info",vo);

		return "views/restaurant/restaurant-detail";
	}
}
