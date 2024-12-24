package com.shop.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter @Setter
public class CartOrderDto {

    private Long cartItemId;

    // 장바구니에서 여러 개의 상품을 주문할 수 있도록 자기 자신을 List로 가지도록함
    private List<CartOrderDto> cartOrderDtoList;
}
