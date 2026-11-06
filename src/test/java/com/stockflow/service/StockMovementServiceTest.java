
package com.stockflow.service;

import com.stockflow.model.Product;
import com.stockflow.model.StockMovementType;
import com.stockflow.repository.ProductRepository;
import com.stockflow.repository.StockMovementRepository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

class StockMovementServiceTest {

    private ProductRepository productRepository;
    private StockMovementRepository movementRepository;
    private StockAdjustmentService stockAdjustmentService;
    private StockMovementService stockMovementService;

    @BeforeEach
    void setUp() {

        productRepository = mock(ProductRepository.class);
        movementRepository = mock(StockMovementRepository.class);
        stockAdjustmentService = mock(StockAdjustmentService.class);

        stockMovementService = new StockMovementService(
                productRepository,
                movementRepository,
                stockAdjustmentService
        );
    }

    @Test
    void stockIn_shouldPropagateAdjustmentFailure() {

        Product product = new Product(
                "Keyboard",
                new BigDecimal("2500"),
                10,
                null
        );

        when(productRepository.findByIdForUpdate(1))
                .thenReturn(Optional.of(product));

        doThrow(new RuntimeException("Database error"))
                .when(stockAdjustmentService)
                .adjust(
                        product,
                        StockMovementType.STOCK_IN,
                        5,
                        "Test"
                );

        assertThrows(
                RuntimeException.class,
                () -> stockMovementService.stockIn(
                        1, 5, "Test"
                )
        );

        verify(stockAdjustmentService).adjust(
                product,
                StockMovementType.STOCK_IN,
                5,
                "Test"
        );
    }
}
