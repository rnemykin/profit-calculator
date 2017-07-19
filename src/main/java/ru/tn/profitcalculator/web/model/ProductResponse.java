package ru.tn.profitcalculator.web.model;

import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
public class ProductResponse {
    private List<ProductDto> products;
    private List<ProductDto> optionalProducts;
    private BigDecimal profitSum;
    private BigDecimal maxRate;
}
