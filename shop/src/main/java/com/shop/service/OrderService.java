package com.shop.service;

import com.shop.dto.OrderDto;
import com.shop.dto.OrderHistDto;
import com.shop.dto.OrderItemDto;
import com.shop.entity.*;
import com.shop.repository.*;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.thymeleaf.util.StringUtils;

import java.util.ArrayList;
import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class OrderService {

    private final ItemRepository itemRepository;
    private final MemberRepository memberRepository;
    private final OrderRepository orderRepository;
    private final ItemImgRepository itemImgRepository;

    // 상품 주문
    public Long order(OrderDto orderDto, String email) {
        Item item = itemRepository.findById(orderDto.getItemId())  // 주문 상품 조회
                .orElseThrow(EntityNotFoundException::new);
        Member member = memberRepository.findByEmail(email); // 현재 로그인한 회원 이메일 정보 조회

        List<OrderItem> orderItemList = new ArrayList<>();
        // 주문할 상품 엔티티와 주문 수량을 받아 -> 주문 상품 엔티티 생성
        OrderItem orderItem = OrderItem.createOrderItem(item, orderDto.getCount());
        orderItemList.add(orderItem);

        // 회원 정보와 주문할 상품 리스트 정보를 받아 -> 주문 엔티티 생성
        Order order = Order.createOrder(member, orderItemList);
        orderRepository.save(order);  // 주문 엔티티 DB에 저장

        return order.getId();
    }

    // 주문 내역 목록
    @Transactional(readOnly = true)
    public Page<OrderHistDto> getOrderList(String email, Pageable pageable) {
        // 이메일과 페이징조건을 받아 주문 목록 조회
        List<Order> orders = orderRepository.findOrders(email, pageable);
        // 주문 총 갯수
        Long totalCount = orderRepository.countOrders(email);

        List<OrderHistDto> orderHisDtos = new ArrayList<>();

        for (Order order : orders) {
            // 주문 내역 페이지에 전달할 Dto 생성
            OrderHistDto orderHistDto = new OrderHistDto(order);
            List<OrderItem> orderItems = order.getOrderItems();

            for (OrderItem orderItem : orderItems) {
                // 대표 이미지 조회
                ItemImg itemImg = itemImgRepository.findByItemIdAndRepImgYn(
                        orderItem.getItem().getId(), "Y");

                OrderItemDto orderItemDto = new OrderItemDto(orderItem, itemImg.getImgUrl());
                orderHistDto.addOrderItemDto(orderItemDto);
            }

            orderHisDtos.add(orderHistDto);
        }

        return new PageImpl<OrderHistDto>(orderHisDtos, pageable, totalCount);
    }

    // 로그인한 사용자와 주문 데이터를 생성한 사용자가 같은지 검사
    @Transactional(readOnly = true)
    public boolean validateOrder(Long orderId, String email) {
        Member currentMember = memberRepository.findByEmail(email);
        Order order = orderRepository.findById(orderId)
                .orElseThrow(EntityNotFoundException::new);
        Member savedMember = order.getMember();

        if(!StringUtils.equals(currentMember.getEmail(), savedMember.getEmail())) {
            return false;
        }

        return true;
    }

    // 주문 취소
    public void cancelOrder(Long orderId){
        Order order = orderRepository.findById(orderId)
                .orElseThrow(EntityNotFoundException::new);
        order.cancelOrder();
    }

    // 장바구니에서  여러개 상품 주문
    public Long orders(List<OrderDto> orderDtoList, String email) {
        Member member = memberRepository.findByEmail(email);
        List<OrderItem> orderItemList = new ArrayList<>();

        for (OrderDto orderDto : orderDtoList) {  // 주문할 상품 리스트
            Item item = itemRepository.findById(orderDto.getItemId())
                    .orElseThrow(EntityNotFoundException::new);

            OrderItem orderItem = OrderItem.createOrderItem(item, orderDto.getCount());
            orderItemList.add(orderItem);
        }

        // 주문 엔티티 생성
        Order order = Order.createOrder(member, orderItemList);
        // 주문 데이터 저장
        orderRepository.save(order);

        return order.getId();
    }
}
