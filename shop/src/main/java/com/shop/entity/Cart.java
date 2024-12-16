package com.shop.entity;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name="cart")
@Data
public class Cart {

    @Id
    @Column(name = "cart_id")
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    private Long id;

    @OneToOne  // 일대일로 매핑
    @JoinColumn(name = "member_id")  // 매핑할 외래키 지정
    private Member member;
}
