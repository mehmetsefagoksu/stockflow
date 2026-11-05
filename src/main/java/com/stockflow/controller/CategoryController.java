
package com.stockflow.controller;

import com.stockflow.dto.CategoryCreateRequest;
import com.stockflow.dto.CategoryResponse;
import com.stockflow.dto.CategoryUpdateRequest;
import com.stockflow.dto.ProductResponse;
import com.stockflow.model.Category;
import com.stockflow.service.CategoryService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/categories")
public class CategoryController {

    private final CategoryService categoryService;

    public CategoryController(CategoryService categoryService) {
        this.categoryService = categoryService;
    }

    @GetMapping
    public List<CategoryResponse> getAllCategories() {

        return categoryService.getAllCategories()
                .stream()
                .map(category -> new CategoryResponse(
                        category.getId(),
                        category.getName()
                ))
                .toList();
    }

    @GetMapping("/{id}")
    public CategoryResponse getCategoryById(@PathVariable Integer id) {

        Category category = categoryService.getCategoryById(id);

        return new CategoryResponse(
                category.getId(),
                category.getName()
        );
    }

    @PostMapping
    public ResponseEntity<CategoryResponse> createCategory(
            @Valid @RequestBody CategoryCreateRequest request) {

        Category category = categoryService.createCategory(request.name());

        CategoryResponse response = new CategoryResponse(
                category.getId(),
                category.getName()
        );

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PutMapping("/{id}")
    public ResponseEntity<CategoryResponse> updateCategory(
            @PathVariable Integer id,
            @Valid @RequestBody CategoryUpdateRequest request) {

        Category category = categoryService.updateCategory(
                id,
                request.name()
        );

        CategoryResponse response = new CategoryResponse(
                category.getId(),
                category.getName()
        );

        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCategory(@PathVariable Integer id) {

        categoryService.deleteCategory(id);

        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{id}/products")
    public List<ProductResponse> getCategoryProducts(
            @PathVariable Integer id) {

        return categoryService.getCategoryProducts(id);
    }

}
