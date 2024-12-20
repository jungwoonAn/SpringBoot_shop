package com.shop.exception;

// 상품의 주문 수량보다 재고 수량이 적을 때 예외 발생
public class OutOfStockException extends RuntimeException {

    public OutOfStockException(String message) {
        super(message);
    }
}
