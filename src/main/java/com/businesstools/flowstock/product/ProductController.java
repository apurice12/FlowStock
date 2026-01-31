package com.businesstools.flowstock.product;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/flow-stock/products")
public class ProductController {

    private final ProductService productService;

    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    @PostMapping
    public ResponseEntity<Product> create(@RequestBody ProductRequestDTO productRequestDTO) throws Exception {

        Product product = new Product();
        product.setCode(productRequestDTO.getCode());
        product.setName(productRequestDTO.getName());
        product.setPrice(productRequestDTO.getPrice());
        product.setQuantity(productRequestDTO.getQuantity());

        Product productCreated = productService.create(product, productRequestDTO.getProductCategoryId(), productRequestDTO.getUnitOfMeasureId());

        return ResponseEntity.status(HttpStatus.CREATED).body(productCreated);
    }

    @GetMapping
    public ResponseEntity<List<Product>> getAll() {
        return ResponseEntity.ok(productService.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Product> getById(@PathVariable long id) throws Exception {
        return ResponseEntity.ok(productService.findById(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Product> update(@PathVariable Long id, @RequestBody ProductRequestDTO productRequestDTO) throws Exception {

        return ResponseEntity.ok(productService.update(id, productRequestDTO));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) throws Exception {
        productService.delete(id);
        return ResponseEntity.noContent().build();
    }

}