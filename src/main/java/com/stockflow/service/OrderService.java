
package com.stockflow.service;

import com.stockflow.dto.OrderCreateRequest;
import com.stockflow.dto.OrderDetailsResponse;
import com.stockflow.dto.OrderItemRequest;
import com.stockflow.dto.OrderItemResponse;
import com.stockflow.exception.InsufficientStockException;
import com.stockflow.exception.OrderAlreadyCancelledException;
import com.stockflow.exception.OrderNotFoundException;
import com.stockflow.exception.ProductNotFoundException;
import com.stockflow.model.*;
import com.stockflow.repository.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;


@Service
public class OrderService {



    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;
    private final ProductRepository productRepository;
    private final StockAdjustmentService stockAdjustmentService;

    public OrderService(
            OrderRepository orderRepository,
            OrderItemRepository orderItemRepository,
            ProductRepository productRepository,
            StockAdjustmentService stockAdjustmentService) {

        this.orderRepository = orderRepository;
        this.orderItemRepository = orderItemRepository;
        this.productRepository = productRepository;
        this.stockAdjustmentService = stockAdjustmentService;
    }





    @Transactional
    public Integer createOrder(OrderCreateRequest request) {

        // 1. Aynı ürünlerin miktarlarını birleştir.
        Map<Integer, Integer> quantities = new TreeMap<>();

        for (OrderItemRequest item : request.items()) {
            if (item == null || item.productId() == null
                    || item.productId() <= 0
                    || item.quantity() == null
                    || item.quantity() <= 0) {
                throw new IllegalArgumentException(
                        "Invalid order item"
                );
            }

            quantities.merge(
                    item.productId(),
                    item.quantity(),
                    Math::addExact
            );
        }

        if (quantities.isEmpty()) {
            throw new IllegalArgumentException(
                    "Order must contain at least one item"
            );
        }

        // 2. Ürünleri ID sırasıyla kilitle ve stokları kontrol et.
        Map<Integer, Product> products = new TreeMap<>();

        for (Map.Entry<Integer, Integer> entry
                : quantities.entrySet()) {

            Integer productId = entry.getKey();
            int quantity = entry.getValue();

            Product product = productRepository
                    .findByIdForUpdate(productId)
                    .orElseThrow(() ->
                            new ProductNotFoundException(
                                    "Product not found " + productId
                            )
                    );

            if (product.getStockQuantity() < quantity) {
                throw new InsufficientStockException(
                        "Insufficient stock for product " + productId
                );
            }

            products.put(productId, product);
        }

        // 3. Siparişi oluştur.
        Order order = orderRepository.save(
                new Order(OrderStatus.CREATED)
        );


        // 4. Sipariş kalemlerini ve stok hareketlerini oluştur.
        for (Map.Entry<Integer, Integer> entry
                : quantities.entrySet()) {

            Integer productId = entry.getKey();
            int quantity = entry.getValue();
            Product product = products.get(productId);

            stockAdjustmentService.adjust(
                    product,
                    StockMovementType.STOCK_OUT,
                    quantity,
                    "Order #" + order.getId()
            );

            orderItemRepository.save(
                    new OrderItem(
                            order,
                            product,
                            quantity,
                            product.getPrice()
                    )
            );
        }

        return order.getId();
    }


    @Transactional(readOnly = true)
    public OrderDetailsResponse getOrderById(Integer id) {

        Order order = orderRepository.findById(id)
                .orElseThrow(() ->
                        new OrderNotFoundException(
                                "Order not found " + id
                        )
                );

        List<OrderItemResponse> items = orderItemRepository
                .findAllByOrder_Id(id)
                .stream()
                .map(item -> new OrderItemResponse(
                        item.getProduct().getId(),
                        item.getProduct().getName(),
                        item.getQuantity(),
                        item.getUnitPrice(),
                        item.getTotalPrice()
                ))
                .toList();

        BigDecimal totalAmount = items.stream()
                .map(OrderItemResponse::totalPrice)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        return new OrderDetailsResponse(
                order.getId(),
                order.getStatus(),
                items,
                totalAmount
        );
    }

    @Transactional
    public void cancelOrder(Integer orderId) {

        // 1. Siparişi kilitleyerek getir.
        Order order = orderRepository.findByIdForUpdate(orderId)
                .orElseThrow(() ->
                        new OrderNotFoundException(
                                "Order not found " + orderId
                        )
                );

        // 2. İkinci kez iptal edilmesini engelle.
        if (order.getStatus() == OrderStatus.CANCELLED) {
            throw new OrderAlreadyCancelledException(
                    "Order already cancelled: " + orderId
            );
        }

        // 3. Sipariş kalemlerini getir.
        List<OrderItem> items =
                orderItemRepository.findAllByOrder_Id(orderId);

        // 4. Aynı ürüne ait miktarları birleştir ve sırala.
        Map<Integer, Integer> quantities = new TreeMap<>();

        for (OrderItem item : items) {
            quantities.merge(
                    item.getProduct().getId(),
                    item.getQuantity(),
                    Math::addExact
            );
        }

        // 5. Ürünleri sırayla kilitle ve stokları iade et.
        for (Map.Entry<Integer, Integer> entry
                : quantities.entrySet()) {

            Integer productId = entry.getKey();
            int quantity = entry.getValue();

            Product product = productRepository
                    .findByIdForUpdate(productId)
                    .orElseThrow(() ->
                            new ProductNotFoundException(
                                    "Product not found " + productId
                            )
                    );


            stockAdjustmentService.adjust(
                    product,
                    StockMovementType.STOCK_IN,
                    quantity,
                    "Order cancellation #" + orderId
            );

        }

        // 6. Siparişi iptal et.
        order.cancel();
    }


}
