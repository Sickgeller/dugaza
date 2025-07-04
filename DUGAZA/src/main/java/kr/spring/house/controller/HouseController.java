package kr.spring.house.controller;

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
import kr.spring.util.PagingUtil;

import kr.spring.house.vo.HouseVO;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Controller
@RequestMapping("/house")
public class HouseController {

	@Autowired
	private HouseService houseService;

	@GetMapping("")
	public String accommodationMain(@RequestParam(defaultValue="1") int pageNum,
									@RequestParam(defaultValue = "") String keyword,
									@RequestParam(defaultValue = "0") int tag,
									Model model) {
		int count = houseService.selectRowCount();

		//페이지 처리
		PagingUtil page = new PagingUtil(null,keyword,
				pageNum,count,9,10,
				"");
		Map<String,Object> map = 
				new HashMap<String,Object>();
		List<HouseVO> list = null;
		if(count > 0) {
			map.put("start", page.getStartRow());
			map.put("end", page.getEndRow());
			map.put("keyword", keyword);
			list = houseService.selectList(map);
		}
		model.addAttribute("count", count);
		model.addAttribute("list", list);
		model.addAttribute("page", page.getPage());

		return "views/sample/accommodation";
	}
}
