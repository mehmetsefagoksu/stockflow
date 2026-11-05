
package com.stockflow.service;

import com.stockflow.dto.ProductResponse;
import com.stockflow.exception.CategoryInUseException;
import com.stockflow.exception.CategoryNotFoundException;
import com.stockflow.mapper.ProductMapper;
import com.stockflow.model.Category;
import com.stockflow.repository.CategoryRepository;
import com.stockflow.repository.ProductRepository;
import org.springframework.stereotype.Service;
import com.stockflow.exception.CategoryAlreadyExistsException;
import org.springframework.transaction.annotation.Transactional;


import java.util.List;

@Service
public class CategoryService {

    private final CategoryRepository categoryRepository;
    private final ProductRepository productRepository;

    public CategoryService(
            CategoryRepository categoryRepository,
            ProductRepository productRepository) {

        this.categoryRepository = categoryRepository;
        this.productRepository = productRepository;
    }

    public List<Category> getAllCategories() {
        return categoryRepository.findAll();
    }

    public Category createCategory(String name) {

        if (categoryRepository.existsByName(name)) {
            throw new CategoryAlreadyExistsException(
                    "Category already exists: " + name
            );
        }

        Category category = new Category(name);

        return categoryRepository.save(category);
    }

    public Category getCategoryById(Integer id) {

        return categoryRepository.findById(id)
                .orElseThrow(() ->
                        new CategoryNotFoundException(
                                "Category not found " + id
                        )
                );
    }
    @Transactional
    public Category updateCategory(Integer id, String name) {

        Category category = categoryRepository.findById(id)
                .orElseThrow(() ->
                        new CategoryNotFoundException(
                                "Category not found " + id
                        )
                );

        if (categoryRepository.existsByNameAndIdNot(name, id)) {
            throw new CategoryAlreadyExistsException(
                    "Category already exists: " + name
            );
        }

        category.updateName(name);

        return category;
    }

    @Transactional
    public void deleteCategory(Integer id) {

        Category category = categoryRepository.findById(id)
                .orElseThrow(() ->
                        new CategoryNotFoundException(
                                "Category not found " + id
                        )
                );

        if (productRepository.existsByCategory_Id(id)) {
            throw new CategoryInUseException(
                    "Category is in use: " + id
            );
        }

        categoryRepository.delete(category);
    }


    @Transactional(readOnly = true)
    public List<ProductResponse> getCategoryProducts(Integer categoryId) {

        if (!categoryRepository.existsById(categoryId)) {
            throw new CategoryNotFoundException(
                    "Category not found " + categoryId
            );
        }

        return productRepository.findAllByCategory_Id(categoryId)
                .stream()
                .map(ProductMapper::toResponse)
                .toList();
    }


}
