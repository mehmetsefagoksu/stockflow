package com.stockflow.controller;

import com.stockflow.dto.ProductCreateRequest;
import com.stockflow.dto.ProductResponse;
import com.stockflow.mapper.ProductMapper;
import com.stockflow.model.Product;
import com.stockflow.service.ProductService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class ProductController {
    private final ProductService productService;
    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    @GetMapping("/api/products")
    public List<ProductResponse> getProducts() {

        List<Product> products = productService.getAllProducts();

        return products.stream()
                .map(ProductMapper::toResponse)
                .toList();

    }

    @GetMapping("/api/products/{id}")
    public ProductResponse getProductById(@PathVariable Integer id) {

        Product product = productService.getProductById(id);

        return ProductMapper.toResponse(product);
    }

    @PostMapping("/api/products")
    public ResponseEntity<ProductResponse> createProduct(
            @Valid @RequestBody ProductCreateRequest request) {

        Product savedProduct = productService.createProduct(request);

        ProductResponse response = ProductMapper.toResponse(savedProduct);

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
}
