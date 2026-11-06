
package com.stockflow.repository;

import com.stockflow.model.OrderItem;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface OrderItemRepository
        extends JpaRepository<OrderItem, Integer> {

    List<OrderItem> findAllByOrder_Id(Integer orderId);
}
