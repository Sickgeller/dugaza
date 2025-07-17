package kr.spring.wishlist.controller;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import kr.spring.auth.security.CustomUserDetails;
import kr.spring.wishlist.service.WishListService;
import kr.spring.wishlist.vo.WishListVO;
import lombok.extern.slf4j.Slf4j;

@Controller
@Slf4j
public class WishListController {

	@Autowired
	private WishListService wishListService;

	@PostMapping("/wishlist/toggle")
	@ResponseBody
	public ResponseEntity<Map<String, Object>> toggleWish(@RequestParam("contentId") Long contentId,
	                                                     @RequestParam("contentTypeId") Long contentTypeId) {
	    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
	    Map<String, Object> response = new HashMap<>();

	    if (authentication == null || !authentication.isAuthenticated() || !(authentication.getPrincipal() instanceof CustomUserDetails)) {
	        response.put("wished", false);
	        response.put("message", "로그인이 필요합니다.");
	        return ResponseEntity.status(401).body(response);
	    }

	    CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
	    long memberId = userDetails.getMember().getMemberId();

	    WishListVO wishListVO = new WishListVO();
	    wishListVO.setMemberId(memberId);
	    wishListVO.setContentId(contentId);
	    wishListVO.setContentType(contentTypeId);

	    boolean wished = wishListService.toggleWish(wishListVO);
	    response.put("wished", wished);

	    return ResponseEntity.ok(response);
	}
}
