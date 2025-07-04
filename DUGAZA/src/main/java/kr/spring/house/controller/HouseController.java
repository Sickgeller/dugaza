package kr.spring.house.controller;

import java.util.List;
import java.util.Map;
import java.util.HashMap;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import kr.spring.house.service.HouseService;
import kr.spring.house.vo.HouseVO;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Controller
@RequestMapping("/house")
public class HouseController {
	
	@Autowired
	private HouseService houseService;

	@GetMapping("")
	public String accommodationMain(Model model) {
		log.debug("<<accommodationMain 호출>>");

		Map<String,Object> map = new HashMap<String,Object>();
		// 현재는 전체 목록을 가져오기 위해 start와 end를 임의로 설정
		// 실제 구현에서는 페이징 처리 로직이 필요
		map.put("start", 1);
		map.put("end", 10);

		List<HouseVO> list = houseService.selectList(map);
		model.addAttribute("list", list);

		return "views/sample/accommodation";
	}
}
