package com.businesstools.flowstock.product;

import com.businesstools.flowstock.productcategory.ProductCategory;
import com.businesstools.flowstock.productcategory.ProductCategoryRepository;
import com.businesstools.flowstock.unitofmeasure.UnitOfMeasure;
import com.businesstools.flowstock.unitofmeasure.UnitOfMeasureRepository;
import jakarta.transaction.Transactional;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

@Service
public class ProductImportService {

    private final ProductRepository productRepository;
    private final ProductCategoryRepository productCategoryRepository;
    private final UnitOfMeasureRepository unitOfMeasureRepository;

    public ProductImportService(ProductRepository productRepository,
                                ProductCategoryRepository productCategoryRepository,
                                UnitOfMeasureRepository unitOfMeasureRepository) {
        this.productRepository = productRepository;
        this.productCategoryRepository = productCategoryRepository;
        this.unitOfMeasureRepository = unitOfMeasureRepository;
    }

    @Transactional
    public List<Product> importProductsFromExcel(MultipartFile file) {

        List<Product> importedProducts = new ArrayList<>();

        try (InputStream inputStream = file.getInputStream();
             Workbook workbook = new XSSFWorkbook(inputStream)) {

            Sheet sheet = workbook.getSheetAt(0);
            Iterator<Row> rows = sheet.iterator();

            if (rows.hasNext()) {
                rows.next();
            }

            while (rows.hasNext()) {
                Row row = rows.next();

                if (isRowEmpty(row)) {
                    continue;
                }

                Product product = parseRowToProduct(row);
                importedProducts.add(productRepository.save(product));
            }

        } catch (Exception e) {
            throw new ProductImportException("Import failed: " + e.getMessage());
        }

        return importedProducts;
    }

    private Product parseRowToProduct(Row row) {

        int rowNumber = row.getRowNum() + 1;

        Product product = new Product();

        product.setCode(getCellValueAsString(row.getCell(0)));
        product.setName(getCellValueAsString(row.getCell(1)));
        product.setQuantity(getCellValueAsInteger(row.getCell(2)));
        String unitOfMeasureCode = getCellValueAsString(row.getCell(3));
        UnitOfMeasure unitOfMeasure = resolveUnitOfMeasure(unitOfMeasureCode, rowNumber);
        product.setPrice(getCellValueAsBigDecimal(row.getCell(4)));
        String categoryCode = getCellValueAsString(row.getCell(5));
        ProductCategory category = resolveCategory(categoryCode, rowNumber);

        product.setUnitOfMeasure(unitOfMeasure);
        product.setProductCategory(category);

        return product;
    }


    private ProductCategory resolveCategory(String rawCode, int rowNumber) {

        if (rawCode == null || rawCode.isBlank()) {
            throw new ProductImportException(
                    "Missing category code at row " + rowNumber
            );
        }

        String code = rawCode.trim().toUpperCase();

        return productCategoryRepository.findByCode(code)
                .orElseThrow(() ->
                        new ProductImportException(
                                "Product category code '" + code + "' does not exist at row " + rowNumber +
                                        ". Please create it first in the Data Center.")
                );
    }

    private UnitOfMeasure resolveUnitOfMeasure(String rawCode, int rowNumber) {

        if (rawCode == null || rawCode.isBlank()) {
            throw new ProductImportException(
                    "Missing unit of measure code at row " + rowNumber
            );
        }

        String code = rawCode.trim().toUpperCase();

        return unitOfMeasureRepository.findByCode(code)
                .orElseThrow(() ->
                        new ProductImportException(
                                "Unit of measure code '" + code + "' does not exist at row " + rowNumber +
                                        ". Please create it first in the Data Center.")
                );
    }

    private String getCellValueAsString(Cell cell) {
        if (cell == null) {
            return "";
        }

        return switch (cell.getCellType()) {
            case STRING -> cell.getStringCellValue().trim();
            case NUMERIC -> String.valueOf((long) cell.getNumericCellValue());
            case BOOLEAN -> String.valueOf(cell.getBooleanCellValue());
            case FORMULA -> cell.getCellFormula();
            default -> "";
        };
    }

    private Integer getCellValueAsInteger(Cell cell) {
        if (cell == null) {
            return 0;
        }

        return switch (cell.getCellType()) {
            case NUMERIC -> (int) cell.getNumericCellValue();
            case STRING -> {
                try {
                    yield Integer.parseInt(cell.getStringCellValue());
                } catch (NumberFormatException e) {
                    yield 0;
                }
            }
            default -> 0;
        };
    }

    private BigDecimal getCellValueAsBigDecimal(Cell cell) {
        if (cell == null) {
            return BigDecimal.ZERO;
        }

        return switch (cell.getCellType()) {
            case NUMERIC -> BigDecimal.valueOf(cell.getNumericCellValue());
            case STRING -> {
                try {
                    yield new BigDecimal(cell.getStringCellValue());
                } catch (NumberFormatException e) {
                    yield BigDecimal.ZERO;
                }
            }
            default -> BigDecimal.ZERO;
        };
    }

    private boolean isRowEmpty(Row row) {
        if (row == null) {
            return true;
        }

        for (int i = row.getFirstCellNum(); i < row.getLastCellNum(); i++) {
            Cell cell = row.getCell(i);
            if (cell != null && cell.getCellType() != CellType.BLANK) {
                return false;
            }
        }
        return true;
    }
}
