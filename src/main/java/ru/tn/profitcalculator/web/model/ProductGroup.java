package ru.tn.profitcalculator.web.model;

import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
public class ProductGroup {
    private List<ProductResponse> products;
    private List<ProductResponse> optionalProducts;
    private BigDecimal resultSum;
    private BigDecimal profitSum;
    private BigDecimal maxRate;
}
