package kr.spring.cart.service.impl;

import kr.spring.cart.dao.CartMapper;
import kr.spring.cart.service.CartService;
import kr.spring.cart.vo.CartItemVO;
import kr.spring.cart.vo.CartVO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class CartServiceImpl implements CartService {
    
    private final CartMapper cartMapper;
    
    @Override
    public void addToCart(CartItemVO cartItem) {
        // 장바구니 아이템 ID 생성
        cartItem.setCartItemId(UUID.randomUUID().toString());
        cartItem.setCreatedAt(LocalDateTime.now());
        cartItem.setUpdatedAt(LocalDateTime.now());
        
        cartMapper.insertCartItem(cartItem);
        log.info("장바구니에 아이템 추가: memberId={}, itemType={}, itemId={}", 
                cartItem.getMemberId(), cartItem.getItemType(), cartItem.getItemId());
    }
    
    @Override
    public void updateCartItem(CartItemVO cartItem) {
        cartItem.setUpdatedAt(LocalDateTime.now());
        cartMapper.updateCartItem(cartItem);
        log.info("장바구니 아이템 수정: cartItemId={}", cartItem.getCartItemId());
    }
    
    @Override
    public void removeFromCart(String cartItemId) {
        cartMapper.deleteCartItem(cartItemId);
        log.info("장바구니에서 아이템 삭제: cartItemId={}", cartItemId);
    }
    
    @Override
    public CartVO getCart(Long memberId) {
        List<CartItemVO> items = getCartItems(memberId);
        int totalAmount = calculateTotalAmount(memberId);
        int itemCount = getCartItemCount(memberId);
        
        return CartVO.builder()
                .memberId(memberId)
                .items(items)
                .totalAmount(totalAmount)
                .itemCount(itemCount)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
    }
    
    @Override
    public List<CartItemVO> getCartItems(Long memberId) {
        return cartMapper.selectCartItemsByMember(memberId);
    }
    
    @Override
    public void clearCart(Long memberId) {
        cartMapper.clearCartByMember(memberId);
        log.info("장바구니 비우기: memberId={}", memberId);
    }
    
    @Override
    public boolean isItemInCart(Long memberId, String itemType, Long itemId) {
        int count = cartMapper.checkCartItemExists(memberId, itemType, itemId);
        return count > 0;
    }
    
    @Override
    public int calculateTotalAmount(Long memberId) {
        List<CartItemVO> items = getCartItems(memberId);
        return items.stream()
                .mapToInt(CartItemVO::getTotalPrice)
                .sum();
    }
    
    @Override
    public int getCartItemCount(Long memberId) {
        List<CartItemVO> items = getCartItems(memberId);
        return items.size();
    }
} 