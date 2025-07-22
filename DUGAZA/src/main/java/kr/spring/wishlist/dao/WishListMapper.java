package kr.spring.wishlist.dao;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;

import kr.spring.wishlist.vo.WishItemVO;
import kr.spring.wishlist.vo.WishListVO;

@Mapper
public interface WishListMapper {
	public void insertWishList(WishListVO vo);
	public void deleteList(WishListVO vo);
	public WishListVO selectWishList(WishListVO vo);
	List<WishItemVO> selectWishListByMemberId(Long memberId);
	

}
