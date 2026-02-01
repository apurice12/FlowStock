package com.businesstools.flowstock.orderitem;


import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/order/{orderId}/items")
public class OrderItemController {

    private final OrderItemService orderItemService;

    public OrderItemController(OrderItemService orderItemService) {
        this.orderItemService = orderItemService;
    }

    @PostMapping
    public ResponseEntity<OrderItemResponse> addItem(@PathVariable Long orderId, @RequestBody AddOrderItemRequest request) {

        OrderItem orderItem = orderItemService.addItem(orderId, request.productId(), request.quantity());

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(OrderItemResponse.fromEntity(orderItem));
    }

    @PutMapping("/{itemId}/quantity")
    public ResponseEntity<Void> updateQuantity(@PathVariable Long orderId, @PathVariable Long itemId, @RequestBody UpdateQuantityRequest request) {
        orderItemService.updateQuantity(orderId, itemId, request.quantity());
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{itemId}")
    public ResponseEntity<Void> removeItem(@PathVariable Long orderId, @PathVariable Long itemId) {
        orderItemService.removeItem(orderId, itemId);
        return ResponseEntity.noContent().build();
    }
}
