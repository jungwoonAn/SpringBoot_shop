package com.shop.contoller;

import com.shop.dto.OrderDto;
import com.shop.dto.OrderHistDto;
import com.shop.service.OrderService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;
import java.util.Optional;

@Controller
@RequiredArgsConstructor
@Log4j2
public class OrderController {

    private final OrderService orderService;

    // 주문 하기
    @PostMapping(value = "/order")
    // @RequestBody : HTTP 요청의 본문(body)에 담긴 내용을 자바 객체로 전달
    // @ResponseBody : 자바 객체를 HTTP 요청의 body로 전달
    public @ResponseBody ResponseEntity order(@RequestBody @Valid OrderDto orderDto,
              BindingResult bindingResult, Principal principal) {

       if(bindingResult.hasErrors()) {
           StringBuilder sb = new StringBuilder();
           List<FieldError> fieldErrors = bindingResult.getFieldErrors();

           for (FieldError fieldError : fieldErrors) {
               sb.append(fieldError.getDefaultMessage());
           }

           return new ResponseEntity<String>(sb.toString(), HttpStatus.BAD_REQUEST);
       }

       String email = principal.getName();  //principal 객체에서 현재 로그인한 회원의 이메일 정보 조회
       Long orderId;

       try {
           // 주문 정보와 이메일 정보를 이용하여 주문 로직 호출
           orderId = orderService.order(orderDto, email);
       }catch(Exception e) {
           return new ResponseEntity<String>(e.getMessage(),
                   HttpStatus.BAD_REQUEST);
       }
       // 결과 값으로 생성된 주문 번호와 요청 성공 응답 코드를 반환
       return new ResponseEntity<Long>(orderId, HttpStatus.OK);
    }

    // 주문 내역 조회
    @GetMapping(value = {"/orders", "/orders/{page}"})
    public String orderHist(@PathVariable("page")Optional<Integer> page,
                    Principal principal, Model model) {
        // 한 번에 가지고 올 주문의 개수 설정
        Pageable pageable = PageRequest.of(page.isPresent() ? page.get() : 0, 4);

        // 현재 로그인한 회원의 이메일과 페이징 객체를 받아 화면에 전달한 주문 목록 반환
        Page<OrderHistDto> ordersHisDtoList = orderService.getOrderList(
                principal.getName(), pageable);

        model.addAttribute("orders", ordersHisDtoList);
        model.addAttribute("page", pageable.getPageNumber());
        model.addAttribute("maxPage", 5);

        return "order/orderHist";
    }

    // 주문 취소
    @PostMapping(value = "/order/{orderId}/cancel")
    public @ResponseBody ResponseEntity cancelOrder(@PathVariable("orderId") Long orderId,
                                                    Principal principal) {
        if(!orderService.validateOrder(orderId, principal.getName())) {
            return new ResponseEntity<String>("주문 취소 권한이 없습니다.",
                    HttpStatus.FORBIDDEN);
        }

        orderService.cancelOrder(orderId);  // 주문 취소 로직 호출

        return new ResponseEntity<Long>(orderId, HttpStatus.OK);
    }
}
