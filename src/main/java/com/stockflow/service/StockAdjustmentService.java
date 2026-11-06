
package com.stockflow.service;

import com.stockflow.model.Product;
import com.stockflow.model.StockMovement;
import com.stockflow.model.StockMovementType;
import com.stockflow.repository.StockMovementRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Service
public class StockAdjustmentService {

    private final StockMovementRepository movementRepository;

    public StockAdjustmentService(
            StockMovementRepository movementRepository) {

        this.movementRepository = movementRepository;
    }

    @Transactional(propagation = Propagation.MANDATORY)
    public void adjust(
            Product product,
            StockMovementType type,
            int quantity,
            String reason) {

        if (quantity <= 0) {
            throw new IllegalArgumentException(
                    "Quantity must be positive"
            );
        }

        if (type == null) {
            throw new IllegalArgumentException(
                    "Movement type is required"
            );
        }

        switch (type) {
            case STOCK_IN -> product.increaseStock(quantity);
            case STOCK_OUT -> product.decreaseStock(quantity);
        }

        StockMovement movement = new StockMovement(
                product,
                type,
                quantity,
                reason
        );

        movementRepository.save(movement);
    }
}
