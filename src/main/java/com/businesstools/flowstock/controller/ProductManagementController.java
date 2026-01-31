package com.businesstools.flowstock.controller;

import com.businesstools.flowstock.product.Product;
import com.businesstools.flowstock.product.ProductImportException;
import com.businesstools.flowstock.product.ProductImportService;
import com.businesstools.flowstock.product.ProductRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
public class ProductManagementController {


    private final ProductRepository productRepository;
    private final ProductImportService productImportService;

    public ProductManagementController(ProductRepository productRepository, ProductImportService productImportService) {
        this.productRepository = productRepository;
        this.productImportService = productImportService;
    }


    @GetMapping("/product")
    public String listProducts(@RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "10") int size, Model model) {

        Pageable pageable = PageRequest.of(page, size, Sort.by("id").ascending());
        Page<Product> productPage = productRepository.findAll(pageable);

        model.addAttribute("products", productPage.getContent());
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", productPage.getTotalPages());
        model.addAttribute("totalItems", productPage.getTotalElements());

        model.addAttribute("allProductsCount", productRepository.count());

        model.addAttribute("activePage", "product");

        return "/product/product-list";
    }


    @PostMapping("/products/import")
    @ResponseBody
    public Map<String, String> importProducts(@RequestParam("file") MultipartFile file) {
        Map<String, String> response = new HashMap<>();

        if (file.isEmpty()) {
            response.put("error", "Please select a file to upload");
            return response;
        }

        try {
            // Simulate import logic
            List<Product> importedProducts = productImportService.importProductsFromExcel(file);

            response.put("success", "Successfully imported " + importedProducts.size() + " products");
        } catch (ProductImportException e) {
            response.put("error", "Failed to import products: " + e.getMessage());
        }

        return response;
    }

    @GetMapping("/product/edit/{id}")
    public String showEditProductForm(@PathVariable Long id, Model model) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Product not found"));
        model.addAttribute("product", product);
        model.addAttribute("isEdit", true);
        model.addAttribute("activePage", "product");
        return "/product/product-form";
    }

    @PostMapping("/product/save")
    public String saveProduct(@ModelAttribute Product product, Model model) {
        try {
            productRepository.save(product);
            return "redirect:/product";
        } catch (Exception e) {
            model.addAttribute("error", "Failed to save product: " + e.getMessage());
            model.addAttribute("isEdit", product.getId() != null);
            return "/product/product-form";
        }
    }

    @GetMapping("/product/delete/{id}")
    public String deleteProduct(@PathVariable Long id) {
        try {
            productRepository.deleteById(id);
            return "redirect:/product";
        } catch (Exception e) {
            return "redirect:/product";
        }
    }


}
