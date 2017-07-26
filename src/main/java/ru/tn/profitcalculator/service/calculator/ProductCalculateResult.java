package ru.tn.profitcalculator.service.calculator;

import lombok.Builder;
import lombok.Data;
import ru.tn.profitcalculator.model.Product;
import ru.tn.profitcalculator.model.enums.BonusOptionEnum;

import java.math.BigDecimal;

@Data
@Builder
public class ProductCalculateResult {
    private BigDecimal profitSum;
    private BigDecimal totalSum;
    private BigDecimal maxRate;
    private BonusOptionEnum option;
    private BigDecimal optionMaxRate;
    private Integer daysCount;
    private Product product;
    private boolean recommendation;
}
