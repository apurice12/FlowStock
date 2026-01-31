package com.businesstools.flowstock.product;

import com.businesstools.flowstock.productcategory.ProductCategory;
import com.businesstools.flowstock.productcategory.ProductCategoryRepository;
import com.businesstools.flowstock.unitofmeasure.UnitOfMeasure;
import com.businesstools.flowstock.unitofmeasure.UnitOfMeasureRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ProductService {

    private final ProductRepository productRepository;
    private final ProductCategoryRepository productCategoryRepository;
    private final UnitOfMeasureRepository unitOfMeasureRepository;

    public ProductService(ProductRepository productRepository,
                          ProductCategoryRepository productCategoryRepository,
                          UnitOfMeasureRepository unitOfMeasureRepository) {
        this.productRepository = productRepository;
        this.productCategoryRepository = productCategoryRepository;
        this.unitOfMeasureRepository = unitOfMeasureRepository;
    }

    public Product create(Product product, Long productCategoryId, Long unitOfMeasureId) throws Exception {

        ProductCategory productCategory = productCategoryRepository.findById(productCategoryId)
                .orElseThrow(() -> new Exception("Product Category not found with id " + productCategoryId));

        UnitOfMeasure unitOfMeasure = unitOfMeasureRepository.findById(unitOfMeasureId)
                .orElseThrow(() -> new Exception("Unit of measure not found with id " + unitOfMeasureId));

        product.setProductCategory(productCategory);
        product.setUnitOfMeasure(unitOfMeasure);

        return productRepository.save(product);
    }

    public List<Product> findAll() {
        return productRepository.findAll();
    }

    public Product findById(Long id) throws Exception {
        return productRepository.findById(id).orElseThrow(() -> new Exception("Product not found with id " + id));
    }

    public Product update(Long id, ProductRequestDTO productRequestDTO) throws Exception {

        Product existingProduct = findById(id);

        existingProduct.setCode(productRequestDTO.getCode());
        existingProduct.setName(productRequestDTO.getName());
        existingProduct.setPrice(productRequestDTO.getPrice());
        existingProduct.setQuantity(productRequestDTO.getQuantity());

        if (productRequestDTO.getProductCategoryId() != null) {
            ProductCategory productCategory = productCategoryRepository.findById(productRequestDTO.getProductCategoryId())
                    .orElseThrow(() -> new Exception("Product Category not found with id " + productRequestDTO.getProductCategoryId()));
            existingProduct.setProductCategory(productCategory);
        }

        if (productRequestDTO.getUnitOfMeasureId() != null) {
            UnitOfMeasure unitOfMeasure = unitOfMeasureRepository.findById(productRequestDTO.getUnitOfMeasureId())
                    .orElseThrow(() -> new Exception("Unit of measure not found with id " + productRequestDTO.getUnitOfMeasureId()));
            existingProduct.setUnitOfMeasure(unitOfMeasure);
        }

        return productRepository.save(existingProduct);
    }

    public void delete(Long id) throws Exception {
        productRepository.deleteById(id);
    }

}