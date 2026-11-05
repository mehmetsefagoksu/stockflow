package com.stockflow.service;

import com.stockflow.dto.ProductCreateRequest;
import com.stockflow.exception.ProductNotFoundException;
import com.stockflow.model.Product;
import com.stockflow.repository.CategoryRepository;
import com.stockflow.repository.ProductRepository;
import org.springframework.stereotype.Service;

import com.stockflow.dto.ProductUpdateRequest;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import com.stockflow.model.Category;
import com.stockflow.exception.CategoryNotFoundException;

@Service
public class ProductService {
    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;

    public ProductService(
            ProductRepository productRepository,
            CategoryRepository categoryRepository) {

        this.productRepository = productRepository;
        this.categoryRepository = categoryRepository;
    }

    public List<Product> getAllProducts() {
        return productRepository.findAll();
    }


    public Product getProductById(Integer id) {

        return productRepository.findById(id)
                .orElseThrow(() -> new ProductNotFoundException("Product not found " + id));
    }

    public Product createProduct(ProductCreateRequest request) {

        Category category = findCategory(request.categoryId());

        Product product = new Product(
                request.name(),
                request.price(),
                request.stockQuantity(),
                category
        );

        return productRepository.save(product);
    }

    @Transactional
    public Product updateProduct(Integer id, ProductUpdateRequest request) {

        Product product = productRepository.findById(id)
                .orElseThrow(() ->
                        new ProductNotFoundException("Product not found " + id)
                );

        Category category = findCategory(request.categoryId());

        product.updateDetails(
                request.name(),
                request.price(),
                category
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

    private Category findCategory(Integer categoryId) {

        if (categoryId == null) {
            return null;
        }

        return categoryRepository.findById(categoryId)
                .orElseThrow(() ->
                        new CategoryNotFoundException(
                                "Category not found " + categoryId
                        )
                );
    }
}
