package kr.hhplus.be.server.interfaces.api.order.controller;


import io.swagger.v3.oas.annotations.Operation;
import kr.hhplus.be.server.domain.order.Order;
import kr.hhplus.be.server.domain.order.OrderDetailRequest;
import kr.hhplus.be.server.domain.order.OrderService;
import kr.hhplus.be.server.interfaces.api.common.MultiResoponseDto;
import kr.hhplus.be.server.interfaces.api.common.SingleResponseDto;
import kr.hhplus.be.server.interfaces.api.order.dto.OrderResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class OrderController {

    private final OrderService orderService;

    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    @Operation(summary = "주문 진행", description = "주문을 진행을합니다.")
    //주문
    @PostMapping("/api/order")
    public ResponseEntity order(@RequestParam Long userId, @RequestBody List<OrderDetailRequest> orderDetails){

        Order order = orderService.createOrder(userId, orderDetails);

        return new ResponseEntity<>(new SingleResponseDto<>(order), HttpStatus.CREATED);
    }

}
