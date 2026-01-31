package com.businesstools.flowstock.unitofmeasure;

import com.businesstools.flowstock.productcategory.ProductCategory;
import com.businesstools.flowstock.productcategory.ProductCategoryService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/flow-stock/data-center/unit-of-measure")
public class UnitOfMeasureController {

    private final UnitOfMeasureService unitOfMeasureService;

    public UnitOfMeasureController(UnitOfMeasureService unitOfMeasureService) {
        this.unitOfMeasureService = unitOfMeasureService;
    }

    @PostMapping
    public ResponseEntity<UnitOfMeasure> create(@RequestBody UnitOfMeasure unitOfMeasure) {
        UnitOfMeasure unitOfMeasureCreated = unitOfMeasureService.create(unitOfMeasure);
        return ResponseEntity.status(HttpStatus.CREATED).body(unitOfMeasureCreated);
    }

    @GetMapping
    public ResponseEntity<List<UnitOfMeasure>> getAll() {
        return ResponseEntity.ok(unitOfMeasureService.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<UnitOfMeasure> getById(@PathVariable long id) throws Exception {
        return ResponseEntity.ok(unitOfMeasureService.findById(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<UnitOfMeasure> update(@PathVariable Long id, @RequestBody UnitOfMeasure unitOfMeasure) throws Exception {
        return ResponseEntity.ok(unitOfMeasureService.update(id, unitOfMeasure));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) throws Exception {
        unitOfMeasureService.delete(id);
        return ResponseEntity.noContent().build();
    }

}
