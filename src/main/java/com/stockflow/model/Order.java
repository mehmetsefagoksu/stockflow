
package com.stockflow.model;

import jakarta.persistence.*;
import java.time.OffsetDateTime;

@Entity
@Table(name = "orders")
public class Order {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 20)
    private OrderStatus status;

    @Column(
            name = "created_at",
            nullable = false,
            insertable = false,
            updatable = false
    )
    private OffsetDateTime createdAt;

    protected Order() {
    }

    public Order(OrderStatus status) {
        this.status = status;
    }

    public Integer getId() {
        return id;
    }

    public OrderStatus getStatus() {
        return status;
    }

    public OffsetDateTime getCreatedAt() {
        return createdAt;
    }

    public void cancel() {
        if (this.status == OrderStatus.CANCELLED) {
            throw new IllegalStateException(
                    "Order is already cancelled"
            );
        }

        this.status = OrderStatus.CANCELLED;
    }
}
