
package com.stockflow.mapper;

import com.stockflow.dto.ProductResponse;
import com.stockflow.model.Product;

public class ProductMapper {

    public static ProductResponse toResponse(Product product) {

        Integer categoryId = product.getCategory() != null
                ? product.getCategory().getId()
                : null;

        return new ProductResponse(
                product.getId(),
                product.getName(),
                product.getPrice(),
                product.getStockQuantity(),
                categoryId
        );
    }
}
