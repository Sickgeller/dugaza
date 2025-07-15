package kr.spring.cart.dao;

import kr.spring.cart.vo.CartItemVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface CartMapper {
    
    /**
     * 장바구니 아이템 추가
     */
    void insertCartItem(CartItemVO cartItem);
    
    /**
     * 장바구니 아이템 수정
     */
    void updateCartItem(CartItemVO cartItem);
    
    /**
     * 장바구니 아이템 삭제
     */
    void deleteCartItem(@Param("cartItemId") String cartItemId);
    
    /**
     * 회원의 장바구니 아이템 목록 조회
     */
    List<CartItemVO> selectCartItemsByMember(@Param("memberId") Long memberId);
    
    /**
     * 장바구니 아이템 상세 조회
     */
    CartItemVO selectCartItem(@Param("cartItemId") String cartItemId);
    
    /**
     * 회원의 장바구니 비우기
     */
    void clearCartByMember(@Param("memberId") Long memberId);
    
    /**
     * 장바구니 아이템 존재 여부 확인
     */
    int checkCartItemExists(@Param("memberId") Long memberId, 
                           @Param("itemType") String itemType, 
                           @Param("itemId") Long itemId);
} 