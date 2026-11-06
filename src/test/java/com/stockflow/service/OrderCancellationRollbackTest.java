
package com.stockflow.service;

import com.stockflow.dto.OrderCreateRequest;
import com.stockflow.dto.OrderItemRequest;
import com.stockflow.model.Product;
import com.stockflow.model.OrderStatus;
import com.stockflow.repository.OrderRepository;
import com.stockflow.repository.ProductRepository;
import com.stockflow.repository.StockMovementRepository;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;

@SpringBootTest
@ActiveProfiles("test")
class OrderCancellationRollbackTest {

    @Autowired
    private OrderService orderService;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private OrderRepository orderRepository;

    @MockitoBean
    private StockMovementRepository movementRepository;

    @Test
    void cancellation_shouldRollbackWhenMovementSaveFails() {

        Product product = productRepository.save(
                new Product(
                        "Cancellation Rollback Product",
                        new BigDecimal("100"),
                        10,
                        null
                )
        );

        Integer orderId = orderService.createOrder(
                new OrderCreateRequest(List.of(
                        new OrderItemRequest(product.getId(), 4)
                ))
        );

        assertEquals(6,
                productRepository.findById(product.getId())
                        .orElseThrow().getStockQuantity());

        doThrow(new RuntimeException("Simulated failure"))
                .when(movementRepository)
                .save(any());

        assertThrows(RuntimeException.class,
                () -> orderService.cancelOrder(orderId));

        assertEquals(6,
                productRepository.findById(product.getId())
                        .orElseThrow().getStockQuantity());

        assertEquals(OrderStatus.CREATED,
                orderRepository.findById(orderId)
                        .orElseThrow().getStatus());
    }
}
