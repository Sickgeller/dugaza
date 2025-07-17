package kr.spring.wishlist.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import kr.spring.wishlist.dao.WishListMapper;
import kr.spring.wishlist.vo.WishListVO;
import lombok.extern.slf4j.Slf4j;

@Service
@Transactional
@Slf4j
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
	public boolean toggleWish(WishListVO vo) {
		WishListVO db_vo = wishListMapper.selectWishList(vo);
		boolean result = false; 
		
		if(db_vo != null) {
			wishListMapper.deleteList(vo);
		} else {
			wishListMapper.insertWishList(vo);
			result = true;
		}
		
		return result;
	}

	@Override
	public WishListVO selectWishList(WishListVO vo) {
		return wishListMapper.selectWishList(vo);
	}
	
}

