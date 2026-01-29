package com.businesstools.flowstock.product;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

@Service
public class ProductImportService {

    private final ProductRepository productRepository;

    public ProductImportService(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    public List<Product> importProductsFromExcel(MultipartFile file) throws IOException {
        List<Product> products = new ArrayList<>();

        try (InputStream inputStream = file.getInputStream();
             Workbook workbook = new XSSFWorkbook(inputStream)) {

            Sheet sheet = workbook.getSheetAt(0);
            Iterator<Row> rows = sheet.iterator();

            if (rows.hasNext()) {
                rows.next();
            }

            while (rows.hasNext()) {
                Row currentRow = rows.next();

                if (isRowEmpty(currentRow)) {
                    continue;
                }

                Product product = parseRowToProduct(currentRow);

                if (product != null) {
                    Product savedProduct = productRepository.save(product);
                    products.add(savedProduct);
                }
            }
        }

        return products;
    }

    private Product parseRowToProduct(Row row) {
        try {
            Product product = new Product();

            // Column 0: Cod Articol (Code)
            Cell codeCell = row.getCell(0);
            if (codeCell != null) {
                product.setCode(getCellValueAsString(codeCell));
            }

            // Column 1: Nume Articol (Name)
            Cell nameCell = row.getCell(1);
            if (nameCell != null) {
                product.setName(getCellValueAsString(nameCell));
            }

            // Column 2: Cant UM Aprov (Quantity)
            Cell quantityCell = row.getCell(2);
            if (quantityCell != null) {
                product.setQuantity(getCellValueAsInteger(quantityCell));
            }

            // Column 3: UM Aprov (Unit of Measure)
            Cell unitCell = row.getCell(3);
            if (unitCell != null) {
                product.setUnitOfMeasure(getCellValueAsString(unitCell));
            }

            // Column 5: Cost Mediu Unitar (Price - using average unit cost)
            Cell priceCell = row.getCell(5);
            if (priceCell != null) {
                product.setPrice(getCellValueAsBigDecimal(priceCell));
            }

            product.setType(null);

            return product;

        } catch (Exception e) {
            System.err.println("Error parsing row: " + e.getMessage());
            return null;
        }
    }

    private String getCellValueAsString(Cell cell) {
        if (cell == null) {
            return "";
        }

        switch (cell.getCellType()) {
            case STRING:
                return cell.getStringCellValue();
            case NUMERIC:
                if (DateUtil.isCellDateFormatted(cell)) {
                    return cell.getDateCellValue().toString();
                } else {
                    return String.valueOf((long) cell.getNumericCellValue());
                }
            case BOOLEAN:
                return String.valueOf(cell.getBooleanCellValue());
            case FORMULA:
                return cell.getCellFormula();
            default:
                return "";
        }
    }

    private Integer getCellValueAsInteger(Cell cell) {
        if (cell == null) {
            return 0;
        }

        switch (cell.getCellType()) {
            case NUMERIC:
                return (int) cell.getNumericCellValue();
            case STRING:
                try {
                    return Integer.parseInt(cell.getStringCellValue());
                } catch (NumberFormatException e) {
                    return 0;
                }
            default:
                return 0;
        }
    }

    private BigDecimal getCellValueAsBigDecimal(Cell cell) {
        if (cell == null) {
            return BigDecimal.ZERO;
        }

        switch (cell.getCellType()) {
            case NUMERIC:
                return BigDecimal.valueOf(cell.getNumericCellValue());
            case STRING:
                try {
                    return new BigDecimal(cell.getStringCellValue());
                } catch (NumberFormatException e) {
                    return BigDecimal.ZERO;
                }
            default:
                return BigDecimal.ZERO;
        }
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