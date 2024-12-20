package com.shop.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "order_item")
@Getter @Setter
public class OrderItem extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "order_item_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "item_id")
    private Item item;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id")
    private Order order;

    private int orderPrice;

    private int count;

    // 기존에 있던 regTime, updateTime 변수 삭제
    
    // 주문 상품과 주문 수량을 받아 OrderItem객체 만드는 메서드
    public static OrderItem createOrderItem(Item item, int count){
        OrderItem orderItem = new OrderItem();
        orderItem.setItem(item);
        orderItem.setCount(count);
        orderItem.setOrderPrice(item.getPrice());
        item.removeStock(count);  // 주문 수량만큼 상품의 재고 수량 감소
        
        return orderItem;
    }

    // 주문한 충 가격 계산하는 메서드
    public int getTotalPrice(){
        return orderPrice * count;
    }

    // 주문 취소시 재고 수량 더해주는 메서드 호출
    public void cancel(){
        this.getItem().addStock(count);
    }
}
