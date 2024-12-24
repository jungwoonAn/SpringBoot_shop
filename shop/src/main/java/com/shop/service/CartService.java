package com.shop.service;

import com.shop.dto.CartDetailDto;
import com.shop.dto.CartItemDto;
import com.shop.dto.CartOrderDto;
import com.shop.dto.OrderDto;
import com.shop.entity.Cart;
import com.shop.entity.CartItem;
import com.shop.entity.Item;
import com.shop.entity.Member;
import com.shop.repository.CartItemRepository;
import com.shop.repository.CartRepository;
import com.shop.repository.ItemRepository;
import com.shop.repository.MemberRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.thymeleaf.util.StringUtils;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class CartService {

    private final ItemRepository itemRepository;
    private final MemberRepository memberRepository;
    private final CartRepository cartRepository;
    private final CartItemRepository cartItemRepository;
    private final OrderService orderService;

    // 장바구니 추가 로직
    public Long addCart(CartItemDto cartItemDto, String email) {
        // 장바구니에 담을 상품 조회
        Item item = itemRepository.findById(cartItemDto.getItemId())
                .orElseThrow(EntityNotFoundException::new);
        // 현재 로그인한 회원 조회
        Member member = memberRepository.findByEmail(email);

        // 현재 로그인한 회원의 장바구니 엔티티 조회
        Cart cart = cartRepository.findByMemberId(member.getId());
        // 상품을 처음 장바구니에 담을 경우 회원 장바구니 생성
        if(cart == null) {
            cart = Cart.createCart(member);
            cartRepository.save(cart);
        }

        // 현재 상품이 장바구니에 있는지 조회
        CartItem savedCartItem =
                cartItemRepository.findByIdAndItemId(cart.getId(), item.getId());
        if(savedCartItem != null) {  // 같은 상품이 장바구니에 있는 경우
            savedCartItem.addCount(cartItemDto.getCount());  // 기존 수량 + 새로 담을 수량
            return savedCartItem.getId();
        }else {
            // 같은 상품이 장바구니에 없는 경우 CartItem 생성
            CartItem cartItem =
                    CartItem.createCartItem(cart, item, cartItemDto.getCount());
            cartItemRepository.save(cartItem);  // 장바구니에 들어갈 상품 저장
            return cartItem.getId();
        }
    }

    // 장바구니에 들어잇는 상품 조회 로직
    @Transactional(readOnly = true)
    public List<CartDetailDto> getCartList(String email) {
        List<CartDetailDto> cartDetailDtoList = new ArrayList<>();

        Member member = memberRepository.findByEmail(email);
        // 회원의 장바구니 조회
        Cart cart = cartRepository.findByMemberId(member.getId());
        if(cart == null) {  // 장바구니가 없는 경우
            return cartDetailDtoList;  // 빈 리스트 반환
        }

        // 장바구니에 담겨있는 상품 정보 조회
        cartDetailDtoList = cartItemRepository.findCartDetailDtoList(cart.getId());

        return cartDetailDtoList;
    }

    // 자바스크립트 코드에서 업데이트할 장바구기 상품번호는 조작이 가능하므로
    // 현재 로그인한 회원과 해당 장바구니 상품을 저장한 회원이 같은지 검사하는 로직 작성
    @Transactional(readOnly = true)
    public boolean validateCartItem(Long cartItemId, String email) {
        // 현재 로그인한 회원 조회
        Member curMember = memberRepository.findByEmail(email);
        CartItem cartItem = cartItemRepository.findById(cartItemId)
                .orElseThrow(EntityNotFoundException::new);
        // 장바구니 상품을 저장한 회원 조회
        Member savedMember = cartItem.getCart().getMember();

        if(!StringUtils.equals(curMember.getEmail(), savedMember.getEmail())) {
            return false;  // 현재 로그인한 회원과 장바구니 상품을 저장한 회원이 다른 경우
        }
        return true;  // 같은 경우
    }

    // 상품 수량 업데이트하는 로직
    public void updateCartItemCount(Long cartItemId, int count) {
        CartItem cartItem = cartItemRepository.findById(cartItemId)
                .orElseThrow(EntityNotFoundException::new);

        cartItem.updateCount(count);
    }

    // 상품 삭제 로직
    public void deleteCartItem(Long cartItemId) {
        CartItem cartItem = cartItemRepository.findById(cartItemId)
                .orElseThrow(EntityNotFoundException::new);

        cartItemRepository.delete(cartItem);
    }

    // 장바구니 상품 주문 로직
    public Long orderCartItem(List<CartOrderDto> cartOrderDtoList, String email) {
        List<OrderDto> orderDtoList = new ArrayList<>();

        // 상품번호를 주문 로직으로 전달할 orderDto 객체 생성
        for(CartOrderDto cartOrderDto : cartOrderDtoList) {
            CartItem cartItem = cartItemRepository.findById(cartOrderDto.getCartItemId())
                    .orElseThrow(EntityNotFoundException::new);

            OrderDto orderDto = new OrderDto();
            orderDto.setItemId(cartItem.getItem().getId());
            orderDto.setCount(cartItem.getCount());
            orderDtoList.add(orderDto);
        }

        // 장바구니에 담은 상품 주문 로직 호출
        Long orderId = orderService.orders(orderDtoList, email);

        // 주문한 상품들 장바구니에서 제거
        for(CartOrderDto cartOrderDto : cartOrderDtoList) {
            CartItem cartItem = cartItemRepository.findById(cartOrderDto.getCartItemId())
                    .orElseThrow(EntityNotFoundException::new);

            cartItemRepository.delete(cartItem);
        }

        return orderId;
    }
}
