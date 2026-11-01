package com.stockflow.dto;

import java.math.BigDecimal;

public record ProductResponse(
        Integer id,
        String name,
        BigDecimal price,
        int stockQuantity,
        Integer categoryId
) {
}