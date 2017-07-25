package ru.tn.profitcalculator.service.calculator;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Builder
public class ProductCalculateResult {
    private BigDecimal profitSum;
    private BigDecimal totalSum;
    private BigDecimal maxRate;
    private Integer daysCount;
}
