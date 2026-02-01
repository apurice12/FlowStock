package com.businesstools.flowstock.orderstatus;

import com.businesstools.flowstock.productcategory.ProductCategory;
import com.businesstools.flowstock.productcategory.ProductCategoryService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/flow-stock/data-center/order-status")
public class OrderStatusController {

    private final OrderStatusService orderStatusService;

    public OrderStatusController(OrderStatusService orderStatusService) {
        this.orderStatusService = orderStatusService;
    }

    @PostMapping
    public ResponseEntity<OrderStatus> create(@RequestBody OrderStatus orderStatus) {
        OrderStatus orderStatusCreated = orderStatusService.create(orderStatus);
        return ResponseEntity.status(HttpStatus.CREATED).body(orderStatusCreated);
    }

    @GetMapping
    public ResponseEntity<List<OrderStatus>> getAll() {
        return ResponseEntity.ok(orderStatusService.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<OrderStatus> getById(@PathVariable long id) throws Exception {
        return ResponseEntity.ok(orderStatusService.findById(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<OrderStatus> update(@PathVariable Long id, @RequestBody OrderStatus orderStatus) throws Exception {
        return ResponseEntity.ok(orderStatusService.update(id, orderStatus));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) throws Exception {
        orderStatusService.delete(id);
        return ResponseEntity.noContent().build();
    }

}
