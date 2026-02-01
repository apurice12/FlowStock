package com.businesstools.flowstock.controller;

import com.businesstools.flowstock.customer.Customer;
import com.businesstools.flowstock.customer.CustomerService;
import com.businesstools.flowstock.order.Order;
import com.businesstools.flowstock.order.OrderResponse;
import com.businesstools.flowstock.order.OrderService;
import com.businesstools.flowstock.orderitem.AddOrderItemRequest;
import com.businesstools.flowstock.orderitem.OrderItem;
import com.businesstools.flowstock.orderitem.OrderItemRepository;
import com.businesstools.flowstock.orderitem.OrderItemResponse;
import com.businesstools.flowstock.product.Product;
import com.businesstools.flowstock.product.ProductRepository;
import com.businesstools.flowstock.product.ProductService;
import com.businesstools.flowstock.productcategory.ProductCategoryService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;

@Controller
public class OrderManagementController {

    private final OrderService orderService;
    private final ProductService productService;
    private final ProductCategoryService productCategoryService;
    private final CustomerService customerService;
    private final OrderItemRepository orderItemRepository;
    private final ProductRepository productRepository;

    public OrderManagementController(OrderService orderService,
                                     ProductService productService,
                                     ProductCategoryService productCategoryService,
                                     CustomerService customerService,
                                     OrderItemRepository orderItemRepository,
                                     ProductRepository productRepository) {
        this.orderService = orderService;
        this.productService = productService;
        this.productCategoryService = productCategoryService;
        this.customerService = customerService;
        this.orderItemRepository = orderItemRepository;
        this.productRepository = productRepository;
    }

    @GetMapping("orders/create/{customerId}")
    public String showCreateOrderPage(@PathVariable Long customerId, Model model) {
        Customer customer = customerService.findById(customerId);
        model.addAttribute("customer", customer);
        model.addAttribute("products", productService.findAll());
        model.addAttribute("categories", productCategoryService.findAll());
        return "customer-list/customer-create-order";
    }

    @PostMapping("/api/orders/{orderId}/items")
    @ResponseBody
    public ResponseEntity<?> addItemToOrder(@PathVariable Long orderId, @RequestBody AddOrderItemRequest request) {

        try {
            Order order = orderService.getOrder(orderId);

            Product product = productRepository.findById(request.productId())
                    .orElseThrow(() -> new RuntimeException("Product not found: " + request.productId()));

            BigDecimal lineTotal = request.unitPrice().multiply(BigDecimal.valueOf(request.quantity()));

            // Create order item
            OrderItem orderItem = new OrderItem();
            orderItem.setOrder(order);
            orderItem.setProduct(product);
            orderItem.setQuantity(request.quantity());
            orderItem.setUnitPrice(request.unitPrice());
            orderItem.setLineTotal(lineTotal);

            OrderItem savedItem = orderItemRepository.save(orderItem);

            orderService.recalculateOrderTotals(orderId);

            return ResponseEntity.status(HttpStatus.CREATED).body(OrderItemResponse.fromEntity(savedItem));

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ErrorResponse("Failed to add item: " + e.getMessage()));
        }
    }
}

