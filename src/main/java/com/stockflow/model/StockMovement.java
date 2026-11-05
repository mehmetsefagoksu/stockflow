
package com.stockflow.model;

import jakarta.persistence.*;
import java.time.OffsetDateTime;

@Entity
@Table(name = "stock_movements")
public class StockMovement {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

    @Enumerated(EnumType.STRING)
    @Column(name = "type", nullable = false, length = 20)
    private StockMovementType type;

    @Column(name = "quantity", nullable = false)
    private int quantity;

    @Column(name = "reason", length = 255)
    private String reason;

    @Column(name = "created_at", nullable = false,
            insertable = false, updatable = false)
    private OffsetDateTime createdAt;

    protected StockMovement() {
    }

    public StockMovement(
            Product product,
            StockMovementType type,
            int quantity,
            String reason) {

        this.product = product;
        this.type = type;
        this.quantity = quantity;
        this.reason = reason;
    }

    public Integer getId() {
        return id;
    }

    public Product getProduct() {
        return product;
    }

    public StockMovementType getType() {
        return type;
    }

    public int getQuantity() {
        return quantity;
    }

    public String getReason() {
        return reason;
    }

    public OffsetDateTime getCreatedAt() {
        return createdAt;
    }
}
