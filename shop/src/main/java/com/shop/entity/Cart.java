package com.shop.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name="cart")
@Getter @Setter
public class Cart extends BaseEntity {

    @Id
    @Column(name = "cart_id")
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    private Long id;

    // @OneToOne(fetch = FetchType.EAGER)  // 일대일로 매핑 -> 즉시 로딩(default): 매핑된 객체 join
    @OneToOne(fetch = FetchType.LAZY)  // 일대일로 매핑 -> 지연 로딩 : Member객체 사용되기 직전 select, 필요한 시점에만 연관 데이터를 조회하므로 초기 로딩 시 성능을 최적화 할 수 있음.
    @JoinColumn(name = "member_id")  // 매핑할 외래키 지정
    private Member member;

    // 회원 1명당 1개의 장바구니
    public static Cart createCart(Member member) {
        Cart cart = new Cart();
        cart.setMember(member);
        return cart;
    }
}
