package com.businesstools.flowstock.product;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductRepository extends JpaRepository<Product, Long> {

    Page<Product> findByProductCategoryId(Long categoryId, Pageable pageable);

    Page<Product> findByCodeContainingIgnoreCaseOrNameContainingIgnoreCase(
            String code, String name, Pageable pageable);

    Page<Product> findByProductCategoryIdAndCodeContainingIgnoreCaseOrProductCategoryIdAndNameContainingIgnoreCase(
            Long categoryId1, String code, Long categoryId2, String name, Pageable pageable);

}
