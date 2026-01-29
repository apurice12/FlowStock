package com.businesstools.flowstock.product;

import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ProductService {

    private final ProductRepository productRepository;

    public ProductService(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    public Product create(Product product) {
        return productRepository.save(product);
    }

    public List<Product> findAll() {
        return productRepository.findAll();
    }

    public Product findById(Long id) throws Exception {
        return productRepository.findById(id).orElseThrow(() -> new Exception("Product not found with id " + id));
    }

    public Product update(Long id, Product updatedProduct) throws Exception {

        Product existingProduct = findById(id);

        existingProduct.setCode(updatedProduct.getCode());
        existingProduct.setType(updatedProduct.getType());
        existingProduct.setName(updatedProduct.getName());
        existingProduct.setPrice(updatedProduct.getPrice());
        existingProduct.setQuantity(updatedProduct.getQuantity());
        existingProduct.setUnitOfMeasure(updatedProduct.getUnitOfMeasure());

        return productRepository.save(existingProduct);
    }

    public void delete(Long id) throws Exception {
        productRepository.deleteById(id);
    }

}