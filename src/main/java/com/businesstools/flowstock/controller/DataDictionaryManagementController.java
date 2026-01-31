package com.businesstools.flowstock.controller;

import com.businesstools.flowstock.productcategory.ProductCategory;
import com.businesstools.flowstock.productcategory.ProductCategoryRepository;
import com.businesstools.flowstock.unitofmeasure.UnitOfMeasure;
import com.businesstools.flowstock.unitofmeasure.UnitOfMeasureRepository;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;


@Controller
public class DataDictionaryManagementController {


    private final ProductCategoryRepository productCategoryRepository;
    private final UnitOfMeasureRepository unitOfMeasureRepository;

    public DataDictionaryManagementController(ProductCategoryRepository productCategoryRepository, UnitOfMeasureRepository unitOfMeasureRepository) {
        this.productCategoryRepository = productCategoryRepository;
        this.unitOfMeasureRepository = unitOfMeasureRepository;
    }

    @GetMapping("/data-center")
    public String getDataCenter(Model model) {
        model.addAttribute("activePage", "data-center");
        model.addAttribute("productCategoryCount", productCategoryRepository.count());
        model.addAttribute("unitOfMeasureCount", unitOfMeasureRepository.count());
        return "/data-center/data-center-list";
    }

    @GetMapping("/data-center/product-category")
    public String getProductCategory(Model model) {
        model.addAttribute("activePage", "data-center");
        model.addAttribute("categories", productCategoryRepository.findAll());
        return "/data-center/product-category-list";
    }

    @PostMapping("/data-center/product-category/add")
    public String addProductCategory(@RequestParam String code, @RequestParam String name, RedirectAttributes redirectAttributes) {
        try {
            ProductCategory category = new ProductCategory(code, name);
            productCategoryRepository.save(category);
            redirectAttributes.addFlashAttribute("successMessage", "Category added successfully!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Failed to add category. Code might already exist.");
        }
        return "redirect:/data-center/product-category";
    }

    @PostMapping("/data-center/product-category/edit")
    public String editProductCategory(@RequestParam Long id, @RequestParam String code, @RequestParam String name, RedirectAttributes redirectAttributes) {
        try {
            ProductCategory category = productCategoryRepository.findById(id)
                    .orElseThrow(() -> new RuntimeException("Category not found"));
            category.setCode(code);
            category.setName(name);
            productCategoryRepository.save(category);
            redirectAttributes.addFlashAttribute("successMessage", "Category updated successfully!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Failed to update category.");
        }
        return "redirect:/data-center/product-category";
    }

    @GetMapping("/data-center/product-category/delete/{id}")
    public String deleteProductCategory(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            ProductCategory category = productCategoryRepository.findById(id)
                    .orElseThrow(() -> new RuntimeException("Category not found"));

            if (category.getProducts() != null && !category.getProducts().isEmpty()) {
                redirectAttributes.addFlashAttribute("errorMessage",
                        "Cannot delete category. It has " + category.getProducts().size() + " associated products.");
                return "redirect:/data-center/product-category";
            }

            productCategoryRepository.deleteById(id);
            redirectAttributes.addFlashAttribute("successMessage", "Category deleted successfully!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Failed to delete category.");
        }
        return "redirect:/data-center/product-category";
    }


    @GetMapping("/data-center/unit-of-measure")
    public String getUnitOfMeasure(Model model) {
        model.addAttribute("activePage", "data-center");
        model.addAttribute("units", unitOfMeasureRepository.findAll());
        return "/data-center/unit-of-measure-list";
    }

    @PostMapping("/data-center/unit-of-measure/add")
    public String addUnitOfMeasure(@RequestParam String code, @RequestParam String name, RedirectAttributes redirectAttributes) {
        try {
            UnitOfMeasure unit = new UnitOfMeasure(code, name);
            unitOfMeasureRepository.save(unit);
            redirectAttributes.addFlashAttribute("successMessage", "Unit added successfully!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Failed to add unit. Code might already exist.");
        }
        return "redirect:/data-center/unit-of-measure";
    }

    @PostMapping("/data-center/unit-of-measure/edit")
    public String editUnitOfMeasure(@RequestParam Long id, @RequestParam String code, @RequestParam String name, RedirectAttributes redirectAttributes) {
        try {
            UnitOfMeasure unit = unitOfMeasureRepository.findById(id)
                    .orElseThrow(() -> new RuntimeException("Unit not found"));
            unit.setCode(code);
            unit.setName(name);
            unitOfMeasureRepository.save(unit);
            redirectAttributes.addFlashAttribute("successMessage", "Unit updated successfully!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Failed to update unit.");
        }
        return "redirect:/data-center/unit-of-measure";
    }

    @GetMapping("/data-center/unit-of-measure/delete/{id}")
    public String deleteUnitOfMeasure(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            UnitOfMeasure unit = unitOfMeasureRepository.findById(id)
                    .orElseThrow(() -> new RuntimeException("Unit not found"));

            if (unit.getProducts() != null && !unit.getProducts().isEmpty()) {
                redirectAttributes.addFlashAttribute("errorMessage",
                        "Cannot delete unit. It has " + unit.getProducts().size() + " associated products.");
                return "redirect:/data-center/unit-of-measure";
            }

            unitOfMeasureRepository.deleteById(id);
            redirectAttributes.addFlashAttribute("successMessage", "Unit deleted successfully!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Failed to delete unit.");
        }
        return "redirect:/data-center/unit-of-measure";
    }
}