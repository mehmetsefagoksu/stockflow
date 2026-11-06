
package com.stockflow.dto;

import com.stockflow.model.OrderStatus;
import java.math.BigDecimal;
import java.util.List;

public record OrderDetailsResponse(
        Integer id,
        OrderStatus status,
        List<OrderItemResponse> items,
        BigDecimal totalAmount
) {
}
