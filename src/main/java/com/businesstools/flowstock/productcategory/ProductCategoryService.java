package com.businesstools.flowstock.productcategory;

import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ProductCategoryService {

    private final ProductCategoryRepository productCategoryRepository;

    public ProductCategoryService(ProductCategoryRepository productCategoryRepository) {
        this.productCategoryRepository = productCategoryRepository;
    }

    public ProductCategory create(ProductCategory productCategory) {
        return productCategoryRepository.save(productCategory);
    }

    public List<ProductCategory> findAll() {
        return productCategoryRepository.findAll();
    }

    public ProductCategory findById(Long id) throws Exception {
        return productCategoryRepository.findById(id).orElseThrow(() -> new Exception("Product Category not found with id " + id));
    }

    public ProductCategory update(Long id, ProductCategory updatedProductCategory) throws Exception {

        ProductCategory existingProductCategory = findById(id);

        existingProductCategory.setCode(updatedProductCategory.getCode());
        existingProductCategory.setName(updatedProductCategory.getName());

        return productCategoryRepository.save(existingProductCategory);
    }

    public void delete(Long id) throws Exception {
        productCategoryRepository.deleteById(id);
    }

}
