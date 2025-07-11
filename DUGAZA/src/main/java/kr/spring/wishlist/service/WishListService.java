package kr.spring.wishlist.service;

import java.util.Map;

import kr.spring.wishlist.vo.WishListVO;

public interface WishListService {
	public void insertWishList(WishListVO vo);
	public void deleteList(WishListVO vo);
	public WishListVO selectWishList(WishListVO vo);
	public void toggleLike(Long memberId, Long contentType, Long contentId);
}
