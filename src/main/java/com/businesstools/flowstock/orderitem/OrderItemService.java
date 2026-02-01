package com.businesstools.flowstock.orderitem;

import com.businesstools.flowstock.order.Order;
import com.businesstools.flowstock.order.OrderRepository;
import com.businesstools.flowstock.orderstatus.OrderStatusConstants;
import com.businesstools.flowstock.product.Product;
import com.businesstools.flowstock.product.ProductRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
@Transactional
public class OrderItemService {

    private final OrderRepository orderRepository;
    private final ProductRepository productRepository;

    public OrderItemService(OrderRepository orderRepository, ProductRepository productRepository) {
        this.orderRepository = orderRepository;
        this.productRepository = productRepository;
    }


    public OrderItem addItem(Long orderId, Long productId, int quantity) {

        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new IllegalArgumentException("Order not found" + orderId));

        if (!order.getStatus().getCode().equals("CREATED")) {
            throw new IllegalStateException("Order cannot be modified in status: " + order.getStatus().getName());
        }

        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new IllegalArgumentException("Product not found" + productId));

        OrderItem orderItem = order.getItems().stream()
                .filter(item -> item.getProduct().getId().equals(productId))
                .findFirst()
                .orElse(null);

        if (orderItem != null) {
            int newQty = orderItem.getQuantity() + quantity;

            if (product.getQuantity() < quantity) {
                throw new IllegalStateException("Not enough stock");
            }

            orderItem.setQuantity(newQty);
            orderItem.setLineTotal(
                    orderItem.getUnitPrice().multiply(BigDecimal.valueOf(newQty))
            );

            product.setQuantity(product.getQuantity() - quantity);
        } else {
            if (product.getQuantity() < quantity) {
                throw new IllegalStateException("Not enough stock");
            }

            orderItem = new OrderItem();
            orderItem.setOrder(order);
            orderItem.setProduct(product);
            orderItem.setUnitPrice(product.getPrice());
            orderItem.setQuantity(quantity);
            orderItem.setLineTotal(
                    product.getPrice().multiply(BigDecimal.valueOf(quantity))
            );

            order.getItems().add(orderItem);
            product.setQuantity(product.getQuantity() - quantity);
        }

        recalculateTotals(order);
        orderRepository.save(order);
        productRepository.save(product);

        return orderItem;
    }

    public void removeItem(Long orderId, Long orderItemId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new IllegalArgumentException("Order not found: " + orderId));

        if (!order.getStatus().getCode().equals(OrderStatusConstants.DRAFT)) {
            throw new IllegalStateException("Order cannot be modified in status: " + order.getStatus().getName());
        }

        OrderItem itemToRemove = order.getItems().stream()
                .filter(item -> item.getId().equals(orderItemId))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Order item not found: " + orderItemId));

        order.getItems().remove(itemToRemove);
        recalculateTotals(order);
        orderRepository.save(order);

    }

    public void updateQuantity(Long orderId, Long orderItemId, int newQuantity) {
        if (newQuantity <= 0) {
            throw new IllegalArgumentException("Quantity must be positive");
        }

        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new IllegalArgumentException("Order not found: " + orderId));

        if (!order.getStatus().getCode().equals(OrderStatusConstants.CREATED)) {
            throw new IllegalStateException("Order cannot be modified in status: " + order.getStatus().getName());
        }

        OrderItem orderItem = order.getItems().stream()
                .filter(item -> item.getId().equals(orderItemId))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Order item not found: " + orderItemId));

        orderItem.setQuantity(newQuantity);
        orderItem.setLineTotal(orderItem.getUnitPrice().multiply(BigDecimal.valueOf(newQuantity)));

        recalculateTotals(order);
        orderRepository.save(order);
    }

    private void recalculateTotals(Order order) {
        BigDecimal total = order.getItems().stream()
                .map(OrderItem::getLineTotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        order.setTotalAmount(total);
        order.setTotalTax(BigDecimal.ZERO);
        order.setTotalNet(total);
    }


}
