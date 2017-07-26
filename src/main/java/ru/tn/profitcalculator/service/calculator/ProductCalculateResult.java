package ru.tn.profitcalculator.service.calculator;

import lombok.Builder;
import lombok.Data;
import ru.tn.profitcalculator.model.Product;

import java.math.BigDecimal;

@Data
@Builder
public class ProductCalculateResult {
    private BigDecimal profitSum;
    private BigDecimal totalSum;
    private BigDecimal maxRate;
    private BigDecimal optionMaxRate;
    private Integer daysCount;
    private Product product;
    private boolean recommendation;
}
