package com.stockflow.service;

import com.stockflow.dto.ProductCreateRequest;
import com.stockflow.exception.ProductNotFoundException;
import com.stockflow.model.Product;
import com.stockflow.repository.ProductRepository;
import org.springframework.stereotype.Service;

import com.stockflow.dto.ProductUpdateRequest;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@Service
public class ProductService {
    private final ProductRepository productRepository;

    public ProductService(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    public List<Product> getAllProducts() {
        return productRepository.findAll();
    }


    public Product getProductById(Integer id) {

        return productRepository.findById(id)
                .orElseThrow(() -> new ProductNotFoundException("Product not found " + id));
    }

    public Product createProduct(ProductCreateRequest request) {
        Product product = new Product(
                request.name(),
                request.price(),
                request.stockQuantity(),
                request.categoryId()
        );
        return productRepository.save(product);
    }

    @Transactional
    public Product updateProduct(Integer id, ProductUpdateRequest request) {

        Product product = productRepository.findById(id)
                .orElseThrow(() ->
                        new ProductNotFoundException("Product not found " + id)
                );

        product.updateDetails(
                request.name(),
                request.price(),
                request.stockQuantity(),
                request.categoryId()
        );

        return product;
    }

    @Transactional
    public void deleteProduct(Integer id) {

        Product product = productRepository.findById(id)
                .orElseThrow(() ->
                        new ProductNotFoundException("Product not found " + id)
                );

        productRepository.delete(product);
    }
}
