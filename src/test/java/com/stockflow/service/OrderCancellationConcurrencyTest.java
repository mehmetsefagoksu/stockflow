
package com.stockflow.service;

import com.stockflow.dto.OrderCreateRequest;
import com.stockflow.dto.OrderItemRequest;
import com.stockflow.exception.OrderAlreadyCancelledException;
import com.stockflow.model.OrderStatus;
import com.stockflow.model.Product;
import com.stockflow.repository.OrderItemRepository;
import com.stockflow.repository.OrderRepository;
import com.stockflow.repository.ProductRepository;
import com.stockflow.repository.StockMovementRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;


import java.math.BigDecimal;
import java.util.List;
import java.util.concurrent.*;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
class OrderCancellationConcurrencyTest {
    @Autowired
    private OrderItemRepository orderItemRepository;

    @Autowired
    private OrderService orderService;

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private StockMovementRepository movementRepository;

    @Test
    void concurrentCancellation_shouldRestoreStockOnlyOnce()
            throws Exception {

        Product product = productRepository.save(
                new Product(
                        "Concurrent Cancel Test",
                        new BigDecimal("100"),
                        10,
                        null
                )
        );

        Integer productId = product.getId();

        Integer orderId = orderService.createOrder(
                new OrderCreateRequest(List.of(
                        new OrderItemRequest(productId, 4)
                ))
        );

        ExecutorService executor = Executors.newFixedThreadPool(2);
        CountDownLatch start = new CountDownLatch(1);

        try {
            Callable<Boolean> task = () -> {
                start.await();

                try {
                    orderService.cancelOrder(orderId);
                    return true;
                } catch (OrderAlreadyCancelledException e) {
                    return false;
                }
            };

            Future<Boolean> first = executor.submit(task);
            Future<Boolean> second = executor.submit(task);

            start.countDown();

            boolean firstSuccess = first.get(15, TimeUnit.SECONDS);
            boolean secondSuccess = second.get(15, TimeUnit.SECONDS);

            assertNotEquals(firstSuccess, secondSuccess);

            Product reloaded = productRepository.findById(productId)
                    .orElseThrow();

            assertEquals(10, reloaded.getStockQuantity());

            assertEquals(
                    OrderStatus.CANCELLED,
                    orderRepository.findById(orderId)
                            .orElseThrow().getStatus()
            );

            long returnMovementCount = movementRepository
                    .findAllByProduct_IdOrderByIdDesc(productId)
                    .stream()
                    .filter(m -> m.getReason().equals(
                            "Order cancellation #" + orderId
                    ))
                    .count();

            assertEquals(1, returnMovementCount);

        }
        finally {
            executor.shutdownNow();

            if (!executor.awaitTermination(15, TimeUnit.SECONDS)) {
                throw new IllegalStateException(
                        "Executor did not terminate"
                );
            }

            movementRepository.deleteAll(
                    movementRepository
                            .findAllByProduct_IdOrderByIdDesc(productId)
            );

            orderItemRepository.deleteAll(
                    orderItemRepository.findAllByOrder_Id(orderId)
            );

            orderRepository.deleteById(orderId);

            productRepository.deleteById(productId);
        }

    }
}
