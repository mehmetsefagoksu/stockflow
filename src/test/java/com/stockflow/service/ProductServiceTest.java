package com.stockflow.service;
import static org.mockito.ArgumentMatchers.any;
import com.stockflow.dto.ProductCreateRequest;
import com.stockflow.exception.ProductNotFoundException;
import com.stockflow.model.Product;
import com.stockflow.repository.ProductRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import java.util.Optional;
import static org.mockito.Mockito.never;

import org.junit.jupiter.api.BeforeEach;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.times;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;
import static org.junit.jupiter.api.Assertions.assertEquals;
import java.math.BigDecimal;
import org.mockito.ArgumentCaptor;

@ExtendWith(MockitoExtension.class)
class ProductServiceTest {

    @Mock
    private ProductRepository productRepository;

    @InjectMocks
    private ProductService productService;
    

    @Test
    void shouldReturnProductWhenIdExists() {

        // Arrange
        Product mouse = new Product(
                "Mouse",
                new BigDecimal("750"),
                20,
                3
        );

        when(productRepository.findById(7))
                .thenReturn(Optional.of(mouse));

        // Act
        Product result = productService.getProductById(7);

        // Assert
        assertEquals("Mouse", result.getName());
        assertEquals(new BigDecimal("750"), result.getPrice());
        assertEquals(20, result.getStockQuantity());
        verify(productRepository, times(1)).findById(7);
        verify(productRepository, never()).save(any(Product.class));


    }

    @Test
    void shouldThrowExceptionWhenProductNotFound() {

        when(productRepository.findById(99))
                .thenReturn(Optional.empty());

        ProductNotFoundException exception = assertThrows(
                ProductNotFoundException.class,
                () -> productService.getProductById(99));

                assertEquals("Product not found 99", exception.getMessage()
        );
    }

    @Test
    void shouldCreateProduct() {

        // Arrange
        ProductCreateRequest request = new ProductCreateRequest(
                "Keyboard",
                new BigDecimal("1500"),
                10,
                3
        );

        when(productRepository.save(any(Product.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        // Act
        Product result = productService.createProduct(request);


        assertEquals("Keyboard", result.getName());
        assertEquals(new BigDecimal("1500"), result.getPrice());
        assertEquals(10, result.getStockQuantity());
        assertEquals(3, result.getCategoryId());

        ArgumentCaptor<Product> productCaptor =
                ArgumentCaptor.forClass(Product.class);

        verify(productRepository).save(productCaptor.capture());

        Product capturedProduct = productCaptor.getValue();

        assertEquals(new BigDecimal("1500"), capturedProduct.getPrice());
        assertEquals(10, capturedProduct.getStockQuantity());


        }



}