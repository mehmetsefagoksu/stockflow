package com.stockflow.controller;

import com.stockflow.dto.ProductCreateRequest;
import com.stockflow.exception.ProductNotFoundException;
import com.stockflow.model.Product;
import com.stockflow.service.ProductService;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

import org.springframework.http.MediaType;

import static org.mockito.ArgumentMatchers.any;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;

@WebMvcTest(ProductController.class)
class ProductControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private ProductService productService;

    @Test
    void shouldReturnProductWhenIdExists() throws Exception {

        // Arrange
        Product mouse = new Product(
                "Mouse",
                new BigDecimal("750"),
                20,
                3
        );

        when(productService.getProductById(7))
                .thenReturn(mouse);

        // Act & Assert
        mockMvc.perform(get("/api/products/7"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Mouse"))
                .andExpect(jsonPath("$.price").value(750))
                .andExpect(jsonPath("$.stockQuantity").value(20))
                .andExpect(jsonPath("$.categoryId").value(3));
    }

    @Test
    void shouldReturn404WhenProductNotFound() throws Exception {

        // Arrange
        when(productService.getProductById(99))
                .thenThrow(new ProductNotFoundException("Product not found 99"));

        // Act & Assert
        mockMvc.perform(get("/api/products/99"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.message").value("Product not found 99"));
    }

    @Test
    void shouldCreateProduct() throws Exception {

        // Arrange
        Product savedProduct = new Product(
                "Keyboard",
                new BigDecimal("1500"),
                10,
                3
        );

        when(productService.createProduct(any(ProductCreateRequest.class)))
                .thenReturn(savedProduct);

        // Act & Assert
        mockMvc.perform(post("/api/products")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                            {
                                "name": "Keyboard",
                                "price": 1500,
                                "stockQuantity": 10,
                                "categoryId": 3
                            }
                            """))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name").value("Keyboard"))
                .andExpect(jsonPath("$.price").value(1500))
                .andExpect(jsonPath("$.stockQuantity").value(10))
                .andExpect(jsonPath("$.categoryId").value(3));
    }



}