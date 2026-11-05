
package com.stockflow.controller;

import com.stockflow.dto.StockMovementRequest;
import com.stockflow.dto.StockMovementResponse;
import com.stockflow.service.StockMovementService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/products/{productId}")
public class StockMovementController {

    private final StockMovementService stockMovementService;

    public StockMovementController(
            StockMovementService stockMovementService) {
        this.stockMovementService = stockMovementService;
    }

    @PostMapping("/stock-in")
    public ResponseEntity<Map<String, Integer>> stockIn(
            @PathVariable Integer productId,
            @Valid @RequestBody StockMovementRequest request) {

        Integer newStock = stockMovementService.stockIn(
                productId,
                request.quantity(),
                request.reason()
        );

        return ResponseEntity.ok(
                Map.of("stockQuantity", newStock)
        );
    }

    @PostMapping("/stock-out")
    public ResponseEntity<Map<String, Integer>> stockOut(
            @PathVariable Integer productId,
            @Valid @RequestBody StockMovementRequest request) {

        Integer newStock = stockMovementService.stockOut(
                productId,
                request.quantity(),
                request.reason()
        );

        return ResponseEntity.ok(
                Map.of("stockQuantity", newStock)
        );
    }

    @GetMapping("/movements")
    public List<StockMovementResponse> getMovements(
            @PathVariable Integer productId) {

        return stockMovementService.getMovements(productId);
    }
}
