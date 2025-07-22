package kr.spring.wishlist.service;

import kr.spring.wishlist.vo.WishListVO;

import java.util.List;

public interface WishListService {
	public void insertWishList(WishListVO vo);
	public void deleteList(WishListVO vo);
	public WishListVO selectWishList(WishListVO vo);
	public boolean toggleWish(WishListVO vo);
	public int getWishListCountByMemberId(Long memberId);
	public List<WishListVO> getWishListByMemberId(Long memberId);
}
