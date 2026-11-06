
package com.stockflow.service;

import com.stockflow.dto.OrderCreateRequest;
import com.stockflow.dto.OrderItemRequest;
import com.stockflow.exception.OrderAlreadyCancelledException;
import com.stockflow.model.Product;
import com.stockflow.repository.ProductRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
class OrderCancellationIntegrationTest {

    @Autowired
    private OrderService orderService;

    @Autowired
    private ProductRepository productRepository;

    @Test
    void cancellation_shouldRestoreStock() {

        Product keyboard = productRepository.save(
                new Product(
                        "Cancellation Test Keyboard",
                        new BigDecimal("100"),
                        10,
                        null
                )
        );

        Product mouse = productRepository.save(
                new Product(
                        "Cancellation Test Mouse",
                        new BigDecimal("50"),
                        8,
                        null
                )
        );

        Integer orderId = orderService.createOrder(
                new OrderCreateRequest(List.of(
                        new OrderItemRequest(keyboard.getId(), 2),
                        new OrderItemRequest(mouse.getId(), 3)
                ))
        );

        orderService.cancelOrder(orderId);

        assertEquals(10,
                productRepository.findById(keyboard.getId())
                        .orElseThrow().getStockQuantity());

        assertEquals(8,
                productRepository.findById(mouse.getId())
                        .orElseThrow().getStockQuantity());
    }


    @Test
    void cancellingTwice_shouldNotRestoreStockAgain() {

        Product product = productRepository.save(
                new Product(
                        "Double Cancellation Test",
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

        orderService.cancelOrder(orderId);

        assertThrows(
                OrderAlreadyCancelledException.class,
                () -> orderService.cancelOrder(orderId)
        );

        Product reloaded = productRepository.findById(product.getId())
                .orElseThrow();

        assertEquals(10, reloaded.getStockQuantity());
    }

}
