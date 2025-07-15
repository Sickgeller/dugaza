package kr.spring.cart.service;

import kr.spring.cart.vo.CartItemVO;
import kr.spring.cart.vo.CartVO;

import java.util.List;

public interface CartService {
    
    /**
     * 장바구니에 아이템 추가
     */
    void addToCart(CartItemVO cartItem);
    
    /**
     * 장바구니 아이템 수정
     */
    void updateCartItem(CartItemVO cartItem);
    
    /**
     * 장바구니에서 아이템 삭제
     */
    void removeFromCart(String cartItemId);
    
    /**
     * 회원의 장바구니 조회
     */
    CartVO getCart(Long memberId);
    
    /**
     * 장바구니 아이템 목록 조회
     */
    List<CartItemVO> getCartItems(Long memberId);
    
    /**
     * 장바구니 비우기
     */
    void clearCart(Long memberId);
    
    /**
     * 장바구니 아이템 존재 여부 확인
     */
    boolean isItemInCart(Long memberId, String itemType, Long itemId);
    
    /**
     * 장바구니 총 금액 계산
     */
    int calculateTotalAmount(Long memberId);
    
    /**
     * 장바구니 아이템 개수 조회
     */
    int getCartItemCount(Long memberId);
} 