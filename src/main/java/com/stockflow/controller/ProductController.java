
package com.stockflow.controller;

import com.stockflow.dto.ProductCreateRequest;
import com.stockflow.dto.ProductResponse;
import com.stockflow.dto.ProductUpdateRequest;
import com.stockflow.mapper.ProductMapper;
import com.stockflow.model.Product;
import com.stockflow.service.ProductService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/products")
public class ProductController {

    private final ProductService productService;

    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    // GET /api/products
    @GetMapping
    public List<ProductResponse> getProducts() {

        return productService.getAllProducts()
                .stream()
                .map(ProductMapper::toResponse)
                .toList();
    }

    // GET /api/products/{id}
    @GetMapping("/{id}")
    public ProductResponse getProductById(@PathVariable Integer id) {

        Product product = productService.getProductById(id);

        return ProductMapper.toResponse(product);
    }

    // POST /api/products
    @PostMapping
    public ResponseEntity<ProductResponse> createProduct(
            @Valid @RequestBody ProductCreateRequest request) {

        Product savedProduct = productService.createProduct(request);

        ProductResponse response = ProductMapper.toResponse(savedProduct);

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    // PUT /api/products/{id}
    @PutMapping("/{id}")
    public ResponseEntity<ProductResponse> updateProduct(
            @PathVariable Integer id,
            @Valid @RequestBody ProductUpdateRequest request) {

        Product updatedProduct = productService.updateProduct(id, request);

        return ResponseEntity.ok(
                ProductMapper.toResponse(updatedProduct)
        );
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteProduct(@PathVariable Integer id) {

        productService.deleteProduct(id);

        return ResponseEntity.noContent().build();
    }
}
