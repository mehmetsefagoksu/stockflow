
package com.stockflow.service;

import com.stockflow.model.Product;
import com.stockflow.model.StockMovement;
import com.stockflow.repository.ProductRepository;
import com.stockflow.repository.StockMovementRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.math.BigDecimal;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class StockMovementServiceTest {

    private ProductRepository productRepository;
    private StockMovementRepository movementRepository;
    private StockMovementService stockMovementService;

    @BeforeEach
    void setUp() {
        productRepository = Mockito.mock(ProductRepository.class);
        movementRepository = Mockito.mock(StockMovementRepository.class);

        stockMovementService = new StockMovementService(
                productRepository,
                movementRepository
        );
    }

    @Test
    void stockIn_shouldFailWhenMovementSaveFails() {
        Product product = new Product(
                "Keyboard",
                new BigDecimal("2500"),
                10,
                null
        );

        when(productRepository.findByIdForUpdate(1))
                .thenReturn(Optional.of(product));

        when(movementRepository.save(any(StockMovement.class)))
                .thenThrow(new RuntimeException("Database error"));

        assertThrows(
                RuntimeException.class,
                () -> stockMovementService.stockIn(
                        1, 5, "Test"
                )
        );

        verify(movementRepository).save(any(StockMovement.class));
    }
}
