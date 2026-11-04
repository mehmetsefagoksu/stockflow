
package com.stockflow.controller;

import com.stockflow.dto.ProductCreateRequest;
import com.stockflow.exception.ProductNotFoundException;
import com.stockflow.model.Category;
import com.stockflow.model.Product;
import com.stockflow.service.ProductService;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ProductController.class)
class ProductControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private ProductService productService;

    private Category mockCategory() {

        Category category = mock(Category.class);

        when(category.getId()).thenReturn(3);

        return category;
    }

    @Test
    void shouldReturnProductWhenIdExists() throws Exception {

        Product mouse = new Product(
                "Mouse",
                new BigDecimal("750"),
                20,
                mockCategory()
        );

        when(productService.getProductById(7))
                .thenReturn(mouse);

        mockMvc.perform(get("/api/products/7"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Mouse"))
                .andExpect(jsonPath("$.price").value(750))
                .andExpect(jsonPath("$.stockQuantity").value(20))
                .andExpect(jsonPath("$.categoryId").value(3));
    }

    @Test
    void shouldReturn404WhenProductNotFound() throws Exception {

        when(productService.getProductById(99))
                .thenThrow(new ProductNotFoundException("Product not found 99"));

        mockMvc.perform(get("/api/products/99"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.message").value("Product not found 99"));
    }

    @Test
    void shouldCreateProduct() throws Exception {

        Product savedProduct = new Product(
                "Keyboard",
                new BigDecimal("1500"),
                10,
                mockCategory()
        );

        when(productService.createProduct(any(ProductCreateRequest.class)))
                .thenReturn(savedProduct);

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
