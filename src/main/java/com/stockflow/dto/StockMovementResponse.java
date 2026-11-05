
package com.stockflow.dto;

import com.stockflow.model.StockMovementType;
import java.time.OffsetDateTime;

public record StockMovementResponse(
        Integer id,
        Integer productId,
        StockMovementType type,
        int quantity,
        String reason,
        OffsetDateTime createdAt
) {
}
