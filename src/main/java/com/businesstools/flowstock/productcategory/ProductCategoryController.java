package com.businesstools.flowstock.productcategory;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/flow-stock/data-center/product-category")
public class ProductCategoryController {

    private final ProductCategoryService productCategoryService;

    public ProductCategoryController(ProductCategoryService productCategoryService) {
        this.productCategoryService = productCategoryService;
    }

    @PostMapping
    public ResponseEntity<ProductCategory> create(@RequestBody ProductCategory productCategory) {
        ProductCategory productCategoryCreated = productCategoryService.create(productCategory);
        return ResponseEntity.status(HttpStatus.CREATED).body(productCategoryCreated);
    }

    @GetMapping
    public ResponseEntity<List<ProductCategory>> getAll() {
        return ResponseEntity.ok(productCategoryService.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProductCategory> getById(@PathVariable long id) throws Exception {
        return ResponseEntity.ok(productCategoryService.findById(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ProductCategory> update(@PathVariable Long id, @RequestBody ProductCategory productCategory) throws Exception {
        return ResponseEntity.ok(productCategoryService.update(id, productCategory));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) throws Exception {
        productCategoryService.delete(id);
        return ResponseEntity.noContent().build();
    }

}
