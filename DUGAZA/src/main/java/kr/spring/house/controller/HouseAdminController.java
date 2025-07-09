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
	public String form(@RequestParam(defaultValue="1") int pageNum,Model model) {
		
		int count = houseService.selectRowCount(null);
		
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
			
			list = houseService.selectList(map);
		}
		model.addAttribute("count", count);
		model.addAttribute("list", list);
		model.addAttribute("page", page.getPage());

		return "views/admin/super-admin-accommodations";
	}
}
