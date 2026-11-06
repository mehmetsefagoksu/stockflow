
package com.stockflow.service;

import com.stockflow.dto.StockMovementResponse;
import com.stockflow.exception.ProductNotFoundException;
import com.stockflow.model.Product;
import com.stockflow.model.StockMovementType;
import com.stockflow.repository.ProductRepository;
import com.stockflow.repository.StockMovementRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class StockMovementService {

    private final ProductRepository productRepository;
    private final StockMovementRepository movementRepository;
    private final StockAdjustmentService stockAdjustmentService;


    public StockMovementService(
            ProductRepository productRepository,
            StockMovementRepository movementRepository,
            StockAdjustmentService stockAdjustmentService) {

        this.productRepository = productRepository;
        this.movementRepository = movementRepository;
        this.stockAdjustmentService = stockAdjustmentService;
    }



    @Transactional
    public Integer stockIn(
            Integer productId,
            int quantity,
            String reason) {

        validateQuantity(quantity);

        Product product = findProductForUpdate(productId);

        stockAdjustmentService.adjust(
                product,
                StockMovementType.STOCK_IN,
                quantity,
                reason
        );

        return product.getStockQuantity();
    }

    @Transactional
    public Integer stockOut(
            Integer productId,
            int quantity,
            String reason) {

        validateQuantity(quantity);

        Product product = findProductForUpdate(productId);

        stockAdjustmentService.adjust(
                product,
                StockMovementType.STOCK_OUT,
                quantity,
                reason
        );

        return product.getStockQuantity();
    }


    private Product findProductForUpdate(Integer productId) {
        return productRepository.findByIdForUpdate(productId)
                .orElseThrow(() ->
                        new ProductNotFoundException(
                                "Product not found " + productId
                        )
                );
    }

    private void validateQuantity(int quantity) {
        if (quantity <= 0) {
            throw new IllegalArgumentException(
                    "Quantity must be positive"
            );
        }
    }

    @Transactional(readOnly = true)
    public List<StockMovementResponse> getMovements(Integer productId) {

        if (!productRepository.existsById(productId)) {
            throw new ProductNotFoundException(
                    "Product not found " + productId
            );
        }

        return movementRepository
                .findAllByProduct_IdOrderByIdDesc(productId)
                .stream()
                .map(movement -> new StockMovementResponse(
                        movement.getId(),
                        movement.getProduct().getId(),
                        movement.getType(),
                        movement.getQuantity(),
                        movement.getReason(),
                        movement.getCreatedAt()
                ))
                .toList();
    }

}
