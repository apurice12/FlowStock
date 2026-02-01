package com.businesstools.flowstock.orderitem;

import java.math.BigDecimal;

public record AddOrderItemRequest(Long productId, Integer quantity, BigDecimal unitPrice) {
}
