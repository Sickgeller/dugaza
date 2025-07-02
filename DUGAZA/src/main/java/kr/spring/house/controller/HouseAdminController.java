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

import kr.spring.content.vo.ContentVO;
import kr.spring.house.service.HouseService;
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
		
		int count = houseService.selectRowCount();
		
		//페이지 처리
		PagingUtil page = new PagingUtil(null,null,
				pageNum,count,20,10,
				"admin_list");
		Map<String,Object> map = 
				new HashMap<String,Object>();
		List<ContentVO> list = null;
		if(count > 0) {
			map.put("start", page.getStartRow());
			map.put("end", page.getEndRow());
			
			list = houseService.selectList(map);
			log.debug("<<회원목록 - list>> : {}",list);
		}

		model.addAttribute("list", list);

		return "views/admin/super-admin-accommodations";
	}
}
