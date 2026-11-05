
package com.stockflow.repository;

import com.stockflow.model.StockMovement;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface StockMovementRepository
        extends JpaRepository<StockMovement, Integer> {

    List<StockMovement> findAllByProduct_IdOrderByIdDesc(
            Integer productId
    );
}
