package com.shop.dto;

import com.shop.entity.OrderItem;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class OrderItemDto {

    private String itemNm;
    private int count;
    private int orderPrice;
    private String imgUrl;

    public OrderItemDto(OrderItem orderitem, String imgUrl) {
        this.itemNm = orderitem.getItem().getItemNm();
        this.count = orderitem.getCount();
        this.orderPrice = orderitem.getOrderPrice();
        this.imgUrl = imgUrl;
    }
}
