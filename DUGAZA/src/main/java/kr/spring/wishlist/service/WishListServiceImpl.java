package kr.spring.wishlist.service;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import kr.spring.wishlist.dao.WishListMapper;
import kr.spring.wishlist.vo.WishListVO;

@Service
@Transactional
public class WishListServiceImpl implements WishListService{
	
	@Autowired
	private WishListMapper wishListMapper;

	@Override
	public void insertWishList(WishListVO vo) {
		wishListMapper.insertWishList(vo);
	}

	@Override
	public void deleteList(WishListVO vo) {
		wishListMapper.deleteList(vo);
	}

	@Override
	public void toggleLike(Long memberId, Long contentType, Long contentId) {
		// 받아온 정보
		WishListVO vo = new WishListVO();
		vo.setMemberId(memberId);
		vo.setContentId(contentId);
		
		// db의 정보
		WishListVO db_vo = wishListMapper.selectWishList(vo);
		
		if(db_vo != null) {
			insertWishList(db_vo);
		}
		
	}

	@Override
	public WishListVO selectWishList(WishListVO vo) {
		return wishListMapper.selectWishList(vo);
	}
	
}
