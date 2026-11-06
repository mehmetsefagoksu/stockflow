
package com.stockflow.controller;

import com.stockflow.dto.OrderCreateRequest;
import com.stockflow.dto.OrderDetailsResponse;
import com.stockflow.dto.OrderResponse;
import com.stockflow.service.OrderService;

import jakarta.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/orders")
public class OrderController {

    private final OrderService orderService;

    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    @PostMapping
    public ResponseEntity<OrderResponse> createOrder(
            @Valid @RequestBody OrderCreateRequest request) {

        Integer orderId = orderService.createOrder(request);

        OrderResponse response = new OrderResponse(
                orderId,
                "Order created successfully"
        );

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(response);
    }
    @GetMapping("/{id}")
    public ResponseEntity<OrderDetailsResponse> getOrderById(
            @PathVariable Integer id) {

        return ResponseEntity.ok(
                orderService.getOrderById(id)
        );
    }


    @PostMapping("/{id}/cancel")
    public ResponseEntity<Void> cancelOrder(
            @PathVariable Integer id) {

        orderService.cancelOrder(id);

        return ResponseEntity.noContent().build();
    }

}
