
package com.stockflow.service;

import com.stockflow.exception.InsufficientStockException;
import com.stockflow.model.Product;
import com.stockflow.repository.ProductRepository;
import com.stockflow.repository.StockMovementRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.math.BigDecimal;
import java.util.concurrent.*;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
class StockMovementConcurrencyTest {

    @Autowired
    private StockMovementService stockMovementService;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private StockMovementRepository movementRepository;

    @Test
    void concurrentStockOut_shouldPreventOverselling()
            throws Exception {

        Product product = productRepository.save(
                new Product(
                        "Concurrency Test Product",
                        new BigDecimal("100"),
                        5,
                        null
                )
        );

        Integer productId = product.getId();

        ExecutorService executor = Executors.newFixedThreadPool(2);
        CountDownLatch start = new CountDownLatch(1);

        try {
            Callable<Boolean> task = () -> {
                start.await();

                try {
                    stockMovementService.stockOut(
                            productId, 4, "Concurrent test"
                    );
                    return true;
                } catch (InsufficientStockException e) {
                    return false;
                }
            };

            Future<Boolean> first = executor.submit(task);
            Future<Boolean> second = executor.submit(task);

            start.countDown();

            boolean firstSuccess = first.get(10, TimeUnit.SECONDS);
            boolean secondSuccess = second.get(10, TimeUnit.SECONDS);

            assertNotEquals(firstSuccess, secondSuccess);

            Product reloaded = productRepository.findById(productId)
                    .orElseThrow();

            assertEquals(1, reloaded.getStockQuantity());

            long movementCount = movementRepository
                    .findAllByProduct_IdOrderByIdDesc(productId)
                    .size();

            assertEquals(1, movementCount);

        } finally {
            executor.shutdownNow();

            if (!executor.awaitTermination(10, TimeUnit.SECONDS)) {
                throw new IllegalStateException(
                        "Executor did not terminate"
                );
            }

            movementRepository.deleteAll(
                    movementRepository
                            .findAllByProduct_IdOrderByIdDesc(productId)
            );

            productRepository.deleteById(productId);
        }
    }
}
