package com.businesstools.flowstock.controller;

import com.businesstools.flowstock.customer.Customer;
import com.businesstools.flowstock.customer.CustomerService;
import com.businesstools.flowstock.order.Order;
import com.businesstools.flowstock.order.OrderRepository;
import com.businesstools.flowstock.order.OrderResponse;
import com.businesstools.flowstock.order.OrderService;
import com.businesstools.flowstock.orderitem.*;
import com.businesstools.flowstock.orderstatus.OrderStatusRepository;
import com.businesstools.flowstock.product.Product;
import com.businesstools.flowstock.product.ProductRepository;
import com.businesstools.flowstock.product.ProductService;
import com.businesstools.flowstock.productcategory.ProductCategoryService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

@Controller
public class OrderManagementController {

    private final OrderService orderService;
    private final ProductService productService;
    private final ProductCategoryService productCategoryService;
    private final CustomerService customerService;
    private final OrderItemService orderItemService;
    private final OrderRepository orderRepository;
    private final OrderStatusRepository orderStatusRepository;

    public OrderManagementController(OrderService orderService,
                                     ProductService productService,
                                     ProductCategoryService productCategoryService,
                                     CustomerService customerService,
                                     OrderItemService orderItemService,
                                     OrderRepository orderRepository,
                                     OrderStatusRepository orderStatusRepository) {
        this.orderService = orderService;
        this.productService = productService;
        this.productCategoryService = productCategoryService;
        this.customerService = customerService;
        this.orderItemService = orderItemService;
        this.orderRepository = orderRepository;
        this.orderStatusRepository = orderStatusRepository;
    }

    @GetMapping("orders/create/{customerId}")
    public String showCreateOrderPage(@PathVariable Long customerId, Model model) {
        Customer customer = customerService.findById(customerId);
        model.addAttribute("customer", customer);
        model.addAttribute("products", productService.findAll());
        model.addAttribute("categories", productCategoryService.findAll());
        return "customer-list/customer-create-order";
    }


    @GetMapping("/orders")
    public String listOrders(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) Long statusId,
            @RequestParam(required = false) String search,
            Model model) {

        Pageable pageable = PageRequest.of(page, size);
        Page<Order> orderPage;

        boolean hasSearch = search != null && !search.trim().isEmpty();
        boolean hasStatus = statusId != null;

        if (hasSearch && hasStatus) {
            orderPage = orderRepository.findByStatusIdAndCustomer_NameContainingIgnoreCase(statusId, search, pageable);
        } else if (hasSearch) {
            orderPage = orderRepository.findByCustomer_NameContainingIgnoreCase(search, pageable);
        } else if (hasStatus) {
            orderPage = orderRepository.findByStatusId(statusId, pageable);
        } else {
            orderPage = orderRepository.findAll(pageable);
        }

        model.addAttribute("orders", orderPage.getContent());
        model.addAttribute("currentPage", orderPage.getNumber());
        model.addAttribute("totalPages", orderPage.getTotalPages());
        model.addAttribute("totalItems", orderPage.getTotalElements());
        model.addAttribute("searchQuery", search);
        model.addAttribute("selectedStatusId", statusId);
        model.addAttribute("statuses", orderStatusRepository.findAll());

        return "/order-list/order-list";
    }

}