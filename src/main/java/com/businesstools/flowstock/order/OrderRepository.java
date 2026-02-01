package com.businesstools.flowstock.order;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface OrderRepository extends JpaRepository<Order, Long> {

    List<Order> findByCustomerId(Long customerId);

    Page<Order> findByStatusId(Long statusId, Pageable pageable);

    Page<Order> findByCustomer_NameContainingIgnoreCase(
            String name,
            Pageable pageable
    );

    Page<Order> findByStatusIdAndCustomer_NameContainingIgnoreCase(
            Long statusId,
            String name,
            Pageable pageable
    );

}