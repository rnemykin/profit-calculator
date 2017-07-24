package ru.tn.profitcalculator.web.model;

import lombok.Builder;
import lombok.Data;
import ru.tn.profitcalculator.model.Product;
import ru.tn.profitcalculator.model.enums.PosCategoryEnum;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

@Data
@Builder
public class CalculateRequest {
    private Product product;
    private BigDecimal initSum;
    private Integer daysCount;
    private BigDecimal monthRefillSum;
    private BigDecimal monthWithdrawalSum;
    private Map<PosCategoryEnum, BigDecimal> categories2Costs = new HashMap<>();
    private boolean recommendation;
}
