
package com.stockflow.service;

import com.stockflow.exception.InsufficientStockException;
import com.stockflow.model.Product;
import com.stockflow.model.StockMovement;
import com.stockflow.model.StockMovementType;
import com.stockflow.repository.StockMovementRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class StockAdjustmentServiceTest {

    private StockMovementRepository movementRepository;
    private StockAdjustmentService stockAdjustmentService;
    private Product product;

    @BeforeEach
    void setUp() {
        movementRepository = mock(StockMovementRepository.class);

        stockAdjustmentService =
                new StockAdjustmentService(movementRepository);

        product = new Product(
                "Keyboard",
                new BigDecimal("2500"),
                10,
                null
        );
    }

    @Test
    void stockIn_shouldIncreaseStockAndSaveMovement() {
        stockAdjustmentService.adjust(
                product,
                StockMovementType.STOCK_IN,
                5,
                "Supplier delivery"
        );

        assertEquals(15, product.getStockQuantity());
        verify(movementRepository).save(any(StockMovement.class));
    }

    @Test
    void stockOut_shouldDecreaseStockAndSaveMovement() {
        stockAdjustmentService.adjust(
                product,
                StockMovementType.STOCK_OUT,
                4,
                "Sale"
        );

        assertEquals(6, product.getStockQuantity());
        verify(movementRepository).save(any(StockMovement.class));
    }

    @Test
    void invalidQuantity_shouldNotChangeStock() {
        assertThrows(
                IllegalArgumentException.class,
                () -> stockAdjustmentService.adjust(
                        product,
                        StockMovementType.STOCK_IN,
                        0,
                        "Invalid"
                )
        );

        assertEquals(10, product.getStockQuantity());
        verifyNoInteractions(movementRepository);
    }

    @Test
    void insufficientStock_shouldNotSaveMovement() {
        assertThrows(
                InsufficientStockException.class,
                () -> stockAdjustmentService.adjust(
                        product,
                        StockMovementType.STOCK_OUT,
                        20,
                        "Sale"
                )
        );

        assertEquals(10, product.getStockQuantity());
        verifyNoInteractions(movementRepository);
    }
}
