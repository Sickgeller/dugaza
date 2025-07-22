package kr.spring.wishlist.controller;

import java.security.Principal;
import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import kr.spring.member.vo.MemberVO;
import kr.spring.wishlist.service.WishListService;
import kr.spring.wishlist.vo.WishListVO;
import lombok.extern.slf4j.Slf4j;

@Controller
@Slf4j
@RequestMapping("/wish")
public class WishListController {

	@Autowired
	private WishListService wishListService;

	@PostMapping("/toggle")
	public ResponseEntity<Map<String, Object>> toggleLike(@RequestBody WishListVO dto, Principal principal, Model model) {
		// 회원 정보 가져오기
        MemberVO member = (MemberVO) model.getAttribute("member");
        Long memberId = member.getMemberId();

		boolean liked = wishListService.toggleLike(memberId, dto.getContentType(), dto.getContentId());

		Map<String, Object> response = new HashMap<>();
		response.put("liked", liked);

		return ResponseEntity.ok(response);
	}

}
