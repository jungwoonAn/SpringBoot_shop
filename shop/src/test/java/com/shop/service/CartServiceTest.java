package com.shop.service;

import com.shop.constant.ItemSellStatus;
import com.shop.dto.CartItemDto;
import com.shop.entity.CartItem;
import com.shop.entity.Item;
import com.shop.entity.Member;
import com.shop.repository.CartItemRepository;
import com.shop.repository.ItemRepository;
import com.shop.repository.MemberRepository;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
class CartServiceTest {

    @Autowired
    ItemRepository itemRepository;

    @Autowired
    MemberRepository memberRepository;

    @Autowired
    CartItemRepository cartItemRepository;

    @Autowired
    CartService cartService;

    // 장바구니에 담을 상품 저장
    public Item saveItem(){
        Item item = new Item();
        item.setItemNm("테스트 상품");
        item.setPrice(10000);
        item.setItemDetail("테스트 상품 상세 설명");
        item.setItemSellStatus(ItemSellStatus.SELL);
        item.setStockNumber(100);
        return itemRepository.save(item);
    }

    // 회원정보 저장
    public Member saveMember(){
        Member member = new Member();
        member.setEmail("cart1@test.com");
        return memberRepository.save(member);
    }

    @Test
    @DisplayName("장바구니 담기 테스트")
    public void addCart(){
        Item item = saveItem();
        Member member = saveMember();

        CartItemDto cartItemDto = new CartItemDto();
        cartItemDto.setItemId(item.getId());  // 장바구니에 담을 상품
        cartItemDto.setCount(5);  // 장바구니에 담을 상품 수량

        // 상품을 장바구니에 담는 로직 호출
        Long cartItemId = cartService.addCart(cartItemDto, member.getEmail());

        // 장바구니 상품 아이디로 장바구니에 넣을 상품 정보 조회
        CartItem cartItem = cartItemRepository.findById(cartItemId)
                .orElseThrow(EntityNotFoundException::new);

        // 상품 아이디와 장바구니에 저장된 상품 아이디가 같으면 테스트 통과
        assertEquals(item.getId(), cartItem.getItem().getId());
        // 장바구니에 담았던 수량과 실제로 장바구니에 저장된 수량이 같으면 테스트 통과
        assertEquals(cartItemDto.getCount(), cartItem.getCount());
    }
}