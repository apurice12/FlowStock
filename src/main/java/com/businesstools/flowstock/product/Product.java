package com.businesstools.flowstock.product;


import com.businesstools.flowstock.productcategory.ProductCategory;
import com.businesstools.flowstock.unitofmeasure.UnitOfMeasure;
import jakarta.persistence.*;

import java.math.BigDecimal;

@Entity
@Table(name = "product")
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column
    private String code;
    @Column
    private String name;
    @Column
    private long quantity;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="unit_of_measure_id", nullable = false)
    private UnitOfMeasure unitOfMeasure;

    @Column
    private BigDecimal price;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id", nullable = false)

    private ProductCategory productCategory;

    public Product() {
    }

    public Product(String code, String name, Integer quantity, UnitOfMeasure unitOfMeasure, BigDecimal price, ProductCategory productCategory) {
        this.code = code;
        this.name = name;
        this.quantity = quantity;
        this.unitOfMeasure = unitOfMeasure;
        this.price = price;
        this.productCategory = productCategory;
    }

    public Long getId() {return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public long getQuantity() {
        return quantity;
    }

    public void setQuantity(long quantity) {
        this.quantity = quantity;
    }

    public UnitOfMeasure getUnitOfMeasure() {
        return unitOfMeasure;
    }

    public void setUnitOfMeasure(UnitOfMeasure unitOfMeasure) {
        this.unitOfMeasure = unitOfMeasure;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public ProductCategory getProductCategory() {
        return productCategory;
    }

    public void setProductCategory(ProductCategory productCategory) {
        this.productCategory = productCategory;
    }
}