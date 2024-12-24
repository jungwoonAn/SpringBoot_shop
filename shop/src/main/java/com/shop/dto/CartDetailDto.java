package com.shop.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter @Setter
@AllArgsConstructor
@NoArgsConstructor
public class CartDetailDto {

    private Long cartItemId;

    private String itemNm;

    private int price;

    private int count;

    private String imgUrl;

}
