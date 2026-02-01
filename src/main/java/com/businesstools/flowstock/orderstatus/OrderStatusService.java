package com.businesstools.flowstock.orderstatus;

import com.businesstools.flowstock.productcategory.ProductCategory;
import com.businesstools.flowstock.productcategory.ProductCategoryRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class OrderStatusService {

    private final OrderStatusRepository orderStatusRepository;

    public OrderStatusService(OrderStatusRepository orderStatusRepository) {
        this.orderStatusRepository = orderStatusRepository;
    }

    public OrderStatus create(OrderStatus orderStatus) {
        return orderStatusRepository.save(orderStatus);
    }

    public List<OrderStatus> findAll() {
        return orderStatusRepository.findAll();
    }

    public OrderStatus findById(Long id) throws Exception {
        return orderStatusRepository.findById(id).orElseThrow(() -> new Exception("Order status not found with id " + id));
    }

    public OrderStatus update(Long id, OrderStatus updatedOrderStatus) throws Exception {

        OrderStatus existingOrderStatus = findById(id);

        existingOrderStatus.setCode(updatedOrderStatus.getCode());
        existingOrderStatus.setName(updatedOrderStatus.getName());

        return orderStatusRepository.save(existingOrderStatus);
    }

    public void delete(Long id) throws Exception {
        orderStatusRepository.deleteById(id);
    }

}
