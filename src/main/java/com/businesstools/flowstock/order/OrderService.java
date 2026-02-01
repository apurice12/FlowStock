package com.businesstools.flowstock.order;

import com.businesstools.flowstock.customer.Customer;
import com.businesstools.flowstock.customer.CustomerRepository;
import com.businesstools.flowstock.orderstatus.OrderStatus;
import com.businesstools.flowstock.orderstatus.OrderStatusConstants;
import com.businesstools.flowstock.orderstatus.OrderStatusRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
@Transactional
public class OrderService {

    private final OrderRepository orderRepository;
    private final OrderStatusRepository orderStatusRepository;
    private final CustomerRepository customerRepository;

    private static final BigDecimal TAX_RATE = new BigDecimal("0.19"); // 19% VAT

    public OrderService(OrderRepository orderRepository,
                        OrderStatusRepository orderStatusRepository,
                        CustomerRepository customerRepository) {
        this.orderRepository = orderRepository;
        this.orderStatusRepository = orderStatusRepository;
        this.customerRepository = customerRepository;
    }

    public Order createOrder(Long customerId) {
        Customer customer = customerRepository.findById(customerId)
                .orElseThrow(() -> new IllegalArgumentException("Customer not found: " + customerId));

        OrderStatus createdStatus = orderStatusRepository.findByCode(OrderStatusConstants.CREATED)
                .orElseThrow(() -> new IllegalStateException("Created Order Status Not Found"));

        Order order = new Order();
        order.setOrderNumber(generateOrderNumber());
        order.setCustomer(customer);
        order.setStatus(createdStatus);
        order.setCreatedAt(LocalDateTime.now());
        order.setTotalAmount(BigDecimal.ZERO);
        order.setTotalTax(BigDecimal.ZERO);
        order.setTotalNet(BigDecimal.ZERO);

        return orderRepository.save(order);
    }

    public void recalculateOrderTotals(Long orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new IllegalArgumentException("Order not found: " + orderId));

        BigDecimal totalNet = order.getItems().stream()
                .map(item -> item.getUnitPrice().multiply(BigDecimal.valueOf(item.getQuantity())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal totalTax = totalNet.multiply(TAX_RATE)
                .setScale(2, RoundingMode.HALF_UP);

        BigDecimal totalAmount = totalNet.add(totalTax);

        order.setTotalNet(totalNet);
        order.setTotalTax(totalTax);
        order.setTotalAmount(totalAmount);

        orderRepository.save(order);
    }

    public Order confirmOrder(Long orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new IllegalArgumentException("Order not found: " + orderId));

        if (!order.getStatus().getCode().equals(OrderStatusConstants.CREATED)) {
            throw new IllegalStateException("Only created orders can be confirmed");
        }

        if (order.getItems().isEmpty()) {
            throw new IllegalStateException("Cannot confirm order with no items");
        }

        recalculateOrderTotals(orderId);

        OrderStatus confirmedStatus = orderStatusRepository.findByCode(OrderStatusConstants.CONFIRMED)
                .orElseThrow(() -> new IllegalStateException("Confirmed Order Status Not Found"));

        order.setStatus(confirmedStatus);
        order.setConfirmedAt(LocalDateTime.now());

        return orderRepository.save(order);
    }

    public Order cancelOrder(Long orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new IllegalArgumentException("Order not found: " + orderId));

        OrderStatus cancelledStatus = orderStatusRepository.findByCode(OrderStatusConstants.CANCELLED)
                .orElseThrow(() -> new IllegalStateException("Cancelled Order Status Not Found"));

        order.setStatus(cancelledStatus);

        return orderRepository.save(order);
    }

    public Order getOrder(Long orderId) {
        return orderRepository.findById(orderId)
                .orElseThrow(() -> new IllegalArgumentException("Order not found: " + orderId));
    }

    public List<Order> getCustomerOrders(Long customerId) {
        return orderRepository.findByCustomerId(customerId);
    }

    private String generateOrderNumber() {
        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"));
        return "ORD-" + timestamp;
    }
}