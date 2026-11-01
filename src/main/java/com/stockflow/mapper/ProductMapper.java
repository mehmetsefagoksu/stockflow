package com.stockflow.mapper;

import com.stockflow.dto.ProductResponse;
import com.stockflow.model.Product;

public class ProductMapper {

    public static ProductResponse toResponse(Product product) {

        ProductResponse response = new ProductResponse(
                product.getId(),
                product.getName(),
                product.getPrice(),
                product.getStockQuantity(),
                product.getCategoryId()
        );

        return response;
    }
}