
package com.stockflow.service;

import com.stockflow.dto.ProductCreateRequest;
import com.stockflow.exception.ProductNotFoundException;
import com.stockflow.model.Category;
import com.stockflow.model.Product;
import com.stockflow.repository.CategoryRepository;
import com.stockflow.repository.ProductRepository;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProductServiceTest {

    @Mock
    private ProductRepository productRepository;

    @Mock
    private CategoryRepository categoryRepository;

    @InjectMocks
    private ProductService productService;

    @Test
    void shouldReturnProductWhenIdExists() {

        Category category = new Category("Aksesuar");

        Product mouse = new Product(
                "Mouse",
                new BigDecimal("750"),
                20,
                category
        );

        when(productRepository.findById(7))
                .thenReturn(Optional.of(mouse));

        Product result = productService.getProductById(7);

        assertEquals("Mouse", result.getName());
        assertEquals(new BigDecimal("750"), result.getPrice());
        assertEquals(20, result.getStockQuantity());
        assertEquals("Aksesuar", result.getCategory().getName());

        verify(productRepository, times(1)).findById(7);
        verify(productRepository, never()).save(any(Product.class));
    }

    @Test
    void shouldThrowExceptionWhenProductNotFound() {

        when(productRepository.findById(99))
                .thenReturn(Optional.empty());

        ProductNotFoundException exception = assertThrows(
                ProductNotFoundException.class,
                () -> productService.getProductById(99)
        );

        assertEquals("Product not found 99", exception.getMessage());
    }

    @Test
    void shouldCreateProduct() {

        ProductCreateRequest request = new ProductCreateRequest(
                "Keyboard",
                new BigDecimal("1500"),
                10,
                3
        );

        Category category = new Category("Aksesuar");

        when(categoryRepository.findById(3))
                .thenReturn(Optional.of(category));

        when(productRepository.save(any(Product.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        Product result = productService.createProduct(request);

        assertEquals("Keyboard", result.getName());
        assertEquals(new BigDecimal("1500"), result.getPrice());
        assertEquals(10, result.getStockQuantity());
        assertSame(category, result.getCategory());

        ArgumentCaptor<Product> productCaptor =
                ArgumentCaptor.forClass(Product.class);

        verify(productRepository).save(productCaptor.capture());
        verify(categoryRepository).findById(3);

        Product capturedProduct = productCaptor.getValue();

        assertEquals(new BigDecimal("1500"), capturedProduct.getPrice());
        assertEquals(10, capturedProduct.getStockQuantity());
        assertSame(category, capturedProduct.getCategory());
    }
}
