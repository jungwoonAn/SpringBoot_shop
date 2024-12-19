package com.shop.entity;

import com.shop.constant.ItemSellStatus;
import com.shop.dto.ItemFormDto;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.time.LocalDateTime;

@Entity
@Table(name="item")
@Getter @Setter
@ToString
public class Item extends BaseEntity {

    @Id
    @Column(name="item_id")
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column(nullable = false, length = 50)
    private String itemNm;  // 상품명

    @Column(nullable = false)
    private int price;

    @Column(nullable = false)
    private int stockNumber;  // 재고수량

    @Lob
    @Column(nullable = false)
    private String itemDetail;  // 상품 상세 설명

    @Enumerated(EnumType.STRING)
    private ItemSellStatus itemSellStatus;  // 상품 판매 상태

    // 기존에 있던 regTime, updateTime 변수 삭제

    // 상품 데이터 업데이트하는 메서드(로직) : ItemFormDto -> Item
    // 데이터 변경 포인트를 한군데에서 관리할 수 있음.
//    public void updateItem(ItemFormDto itemFormDto) {
//        this.itemNm = itemFormDto.getItemNm();
//        this.price = itemFormDto.getPrice();
//        this.stockNumber = itemFormDto.getStockNumber();
//        this.itemDetail = itemFormDto.getItemDetail();
//        this.itemSellStatus = itemFormDto.getItemSellStatus();
//    }
}
