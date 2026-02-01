package com.businesstools.flowstock.controller;

import com.businesstools.flowstock.customer.Customer;
import com.businesstools.flowstock.customer.CustomerRepository;
import com.businesstools.flowstock.order.Order;
import com.businesstools.flowstock.order.OrderRepository;
import com.businesstools.flowstock.product.ProductRepository;
import com.businesstools.flowstock.productcategory.ProductCategoryRepository;
import com.businesstools.flowstock.unitofmeasure.UnitOfMeasureRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
public class CustomerManagementController {

    private final CustomerRepository customerRepository;
    private final ProductRepository productRepository;
    private final OrderRepository orderRepository;

    public CustomerManagementController(CustomerRepository customerRepository, ProductRepository productRepository, OrderRepository orderRepository) {
        this.customerRepository = customerRepository;
        this.productRepository = productRepository;
        this.orderRepository = orderRepository;
    }

    @GetMapping("/customer")
    public String listCustomers(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String search,
            Model model) {

        model.addAttribute("activePage", "customer");

        Pageable pageable = PageRequest.of(page, size);
        Page<Customer> customerPage;

        if (search != null && !search.trim().isEmpty()) {
            customerPage = customerRepository.findByNameContainingIgnoreCaseOrEmailContainingIgnoreCase(
                    search, search, pageable);
            model.addAttribute("searchQuery", search);
        } else {
            customerPage = customerRepository.findAll(pageable);
        }

        model.addAttribute("customers", customerPage.getContent());
        model.addAttribute("currentPage", customerPage.getNumber());
        model.addAttribute("totalPages", customerPage.getTotalPages());
        model.addAttribute("totalItems", customerPage.getTotalElements());
        model.addAttribute("allCustomersCount", customerRepository.count());

        return "/customer-list/customer-list";
    }

    @GetMapping("/customer/edit")
    public String showAddForm(Model model) {
        model.addAttribute("activePage", "customer");
        model.addAttribute("customer", new Customer());
        return "/customer-list/customer-edit";
    }

    @GetMapping("/customer/edit/{id}")
    public String showEditForm(@PathVariable Long id, Model model) {
        model.addAttribute("activePage", "customer");
        Customer customer = customerRepository.findById(id).orElse(null);
        model.addAttribute("customer", customer);
        return "/customer-list/customer-edit";
    }

    @PostMapping("/customer/save")
    public String saveCustomer(@ModelAttribute Customer customer) {
        customerRepository.save(customer);
        return "redirect:/customer";
    }

    @GetMapping("/customer/delete/{id}")
    public String deleteCustomer(@PathVariable Long id) {
        customerRepository.deleteById(id);
        return "redirect:/customer";
    }

    @GetMapping("/customer/details/{id}")
    public String customerDetails(@PathVariable Long id, Model model) {
        model.addAttribute("activePage", "customer");
        Customer customer = customerRepository.findById(id).orElse(null);
        model.addAttribute("customer", customer);
        List<Order> orders = orderRepository.findByCustomerId(id);
        model.addAttribute("orders", orders);
        return "/customer-list/customer-details";
    }

    @GetMapping("/customer/{id}/create-order")
    public String showCreateOrder(@PathVariable Long id, Model model) {
        model.addAttribute("activePage", "customer");
        Customer customer = customerRepository.findById(id).orElse(null);
        model.addAttribute("customer", customer);
        model.addAttribute("products", productRepository.findAll());
        return "/customer-list/customer-create-order";
    }


}
