package kr.hhplus.be.server.interfaces.api.order.controller;


import io.swagger.v3.oas.annotations.Operation;
import kr.hhplus.be.server.domain.order.Order;
import kr.hhplus.be.server.domain.order.OrderDetailRequest;
import kr.hhplus.be.server.domain.order.OrderFacade;
import kr.hhplus.be.server.domain.order.OrderService;
import kr.hhplus.be.server.interfaces.api.common.SingleResponseDto;
import kr.hhplus.be.server.interfaces.api.order.dto.OrderDetailRequestInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class OrderController {

    private static final Logger logger = LoggerFactory.getLogger(OrderController.class);
    private final OrderService orderService;

    private final OrderFacade orderFacade;

    public OrderController(OrderService orderService, OrderFacade orderFacade) {
        this.orderService = orderService;
        this.orderFacade = orderFacade;
    }

    @Operation(summary = "주문 진행", description = "주문을 진행을합니다.")
    //주문
    @PostMapping("/api/order")
    public ResponseEntity order(@RequestBody OrderDetailRequestInfo OrderDetailRequestInfo){
        logger.info("Received request: body={}", OrderDetailRequestInfo);

        Order order =orderFacade.createOrder(OrderDetailRequestInfo);

        logger.info("Sending response: {}", order);
        return new ResponseEntity<>(new SingleResponseDto<>(order), HttpStatus.CREATED);
    }

}
