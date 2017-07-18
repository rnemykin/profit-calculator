package ru.tn.profitcalculator.web.model;

import lombok.Data;

import java.math.BigDecimal;
import java.util.Set;

@Data
public class ProductResponse {
    private Set<ProductDto> products;
    private Set<ProductDto> optionalProducts;
    private BigDecimal profitSum;
    private BigDecimal maxRate;
}
