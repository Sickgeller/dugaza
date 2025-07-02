package kr.spring.house.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Controller
//@PreAuthorize("hasRole('ADMIN')")
@RequestMapping("/house")
@RequiredArgsConstructor
public class HouseAdminController {
	
}
