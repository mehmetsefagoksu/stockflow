
package com.stockflow.service;

import com.stockflow.model.Product;
import com.stockflow.repository.ProductRepository;
import com.stockflow.repository.StockMovementRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;

@SpringBootTest
@ActiveProfiles("test")
class StockMovementIntegrationTest {

    @Autowired
    private StockMovementService stockMovementService;

    @Autowired
    private ProductRepository productRepository;

    @MockitoBean
    private StockMovementRepository movementRepository;

    @Test
    void stockIn_shouldRollbackWhenMovementSaveFails() {

        Product product = productRepository.save(
                new Product(
                        "Rollback Test Product",
                        new BigDecimal("100"),
                        10,
                        null
                )
        );

        Integer productId = product.getId();

        doThrow(new RuntimeException("Simulated movement failure"))
                .when(movementRepository)
                .save(any());

        assertThrows(RuntimeException.class, () ->
                stockMovementService.stockIn(
                        productId,
                        5,
                        "Rollback test"
                )
        );

        Product reloaded = productRepository.findById(productId)
                .orElseThrow();

        assertEquals(10, reloaded.getStockQuantity());
        productRepository.deleteById(productId);
    }
}
