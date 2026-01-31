package com.businesstools.flowstock.unitofmeasure;

import com.businesstools.flowstock.productcategory.ProductCategory;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UnitOfMeasureService {

    private final UnitOfMeasureRepository unitOfMeasureRepository;

    public UnitOfMeasureService(UnitOfMeasureRepository unitOfMeasureRepository) {
        this.unitOfMeasureRepository = unitOfMeasureRepository;
    }

    public UnitOfMeasure create(UnitOfMeasure unitOfMeasure) {
        return unitOfMeasureRepository.save(unitOfMeasure);
    }

    public List<UnitOfMeasure> findAll() {
        return unitOfMeasureRepository.findAll();
    }

    public UnitOfMeasure findById(Long id) throws Exception {
        return unitOfMeasureRepository.findById(id).orElseThrow(() -> new Exception("Unit of measure not found with id " + id));
    }

    public UnitOfMeasure update(Long id, UnitOfMeasure updatedUnitOfMeasure) throws Exception {

        UnitOfMeasure existingUnitOfMeasure = findById(id);

        existingUnitOfMeasure.setCode(updatedUnitOfMeasure.getCode());
        existingUnitOfMeasure.setName(updatedUnitOfMeasure.getName());

        return unitOfMeasureRepository.save(existingUnitOfMeasure);
    }

    public void delete(Long id) throws Exception {
        unitOfMeasureRepository.deleteById(id);
    }

}
