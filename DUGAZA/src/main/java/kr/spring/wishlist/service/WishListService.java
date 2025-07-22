package kr.spring.wishlist.service;

import java.util.List;

import kr.spring.wishlist.vo.WishItemVO;
import kr.spring.wishlist.vo.WishListVO;

public interface WishListService {
	public void insertWishList(WishListVO vo);
	public void deleteList(WishListVO vo);
	public WishListVO selectWishList(WishListVO vo);
	public boolean toggleWish(WishListVO vo);
	List<WishItemVO> selectWishListByMemberId(Long memberId);

}
