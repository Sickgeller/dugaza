package kr.spring.wishlist.dao;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Mapper;

import kr.spring.wishlist.vo.WishListVO;

@Mapper
public interface WishListMapper {
	public void insertWishList(WishListVO vo);
	public void deleteList(WishListVO vo);
	public WishListVO selectWishList(WishListVO vo);
	public int getWishListCountByMemberId(Long memberId);
	public List<WishListVO> getWishListByMemberId(Long memberId);
}
