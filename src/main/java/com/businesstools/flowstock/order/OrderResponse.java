package com.businesstools.flowstock.order;

import com.businesstools.flowstock.orderitem.OrderItemResponse;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public record OrderResponse(
        Long id,
        String orderNumber,
        Long customerId,
        String customerName,
        String statusCode,
        String statusName,
        BigDecimal totalAmount,
        BigDecimal totalTax,
        BigDecimal totalNet,
        LocalDateTime createdAt,
        LocalDateTime confirmedAt,
        List<OrderItemResponse> items
) {
    public static OrderResponse fromEntity(Order order) {
        List<OrderItemResponse> items = order.getItems() != null
                ? order.getItems().stream()
                .map(OrderItemResponse::fromEntity)
                .toList()
                : List.of();

        return new OrderResponse(
                order.getId(),
                order.getOrderNumber(),
                order.getCustomer().getId(),
                order.getCustomer().getName(),
                order.getStatus().getCode(),
                order.getStatus().getName(),
                order.getTotalAmount(),
                order.getTotalTax(),
                order.getTotalNet(),
                order.getCreatedAt(),
                order.getConfirmedAt(),
                items
        );
    }
}