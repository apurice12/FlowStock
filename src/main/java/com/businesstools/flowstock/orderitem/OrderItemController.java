package com.businesstools.flowstock.orderitem;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/order/{orderId}/items")
public class OrderItemController {

    private static final Logger logger = LoggerFactory.getLogger(OrderItemController.class);
    private final OrderItemService orderItemService;

    public OrderItemController(OrderItemService orderItemService) {
        this.orderItemService = orderItemService;
    }

    @PostMapping
    public ResponseEntity<?> addItem(@PathVariable Long orderId, @RequestBody AddOrderItemRequest request) {

        try {
            if (request.productId() == null) {
                return ResponseEntity.badRequest().body(createErrorResponse("Product ID is required"));
            }

            if (request.quantity() == null || request.quantity() <= 0) {
                return ResponseEntity.badRequest().body(createErrorResponse("Quantity must be greater than 0"));
            }

            OrderItem orderItem = orderItemService.addItem(orderId, request.productId(), request.quantity());

            return ResponseEntity.status(HttpStatus.CREATED).body(OrderItemResponse.fromEntity(orderItem));

        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(createErrorResponse(e.getMessage()));

        } catch (IllegalStateException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(createErrorResponse(e.getMessage()));

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(createErrorResponse("Failed to add item. Please try again."));
        }
    }

    @PutMapping("/{itemId}/quantity")
    public ResponseEntity<?> updateQuantity(
            @PathVariable Long orderId,
            @PathVariable Long itemId,
            @RequestBody UpdateQuantityRequest request) {


        try {
            if (request.quantity() == null || request.quantity() <= 0) {
                return ResponseEntity.badRequest().body(createErrorResponse("Quantity must be greater than 0"));
            }

            orderItemService.updateQuantity(orderId, itemId, request.quantity());

            return ResponseEntity.ok().build();

        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(createErrorResponse(e.getMessage()));

        } catch (IllegalStateException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(createErrorResponse(e.getMessage()));

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(createErrorResponse("Failed to update quantity. Please try again."));
        }
    }

    @DeleteMapping("/{itemId}")
    public ResponseEntity<?> removeItem(@PathVariable Long orderId, @PathVariable Long itemId) {

        try {
            orderItemService.removeItem(orderId, itemId);
            return ResponseEntity.noContent().build();

        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(createErrorResponse(e.getMessage()));

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(createErrorResponse("Failed to remove item. Please try again."));
        }
    }

    private Map<String, Object> createErrorResponse(String message) {
        Map<String, Object> error = new HashMap<>();
        error.put("error", message);
        error.put("timestamp", System.currentTimeMillis());
        return error;
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<?> handleException(Exception e) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(createErrorResponse("An unexpected error occurred"));
    }
}