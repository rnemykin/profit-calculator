package ru.tn.profitcalculator.service.calculator;

import lombok.Builder;
import lombok.Data;
import ru.tn.profitcalculator.model.Product;
import ru.tn.profitcalculator.model.enums.PosCategoryEnum;

import java.math.BigDecimal;
import java.util.List;

@Data
@Builder
public class CalculateRequest {
    private Product product;
    private BigDecimal initSum;
    private Integer daysCount;
    private BigDecimal monthRefillSum;
    private BigDecimal monthWithdrawalSum;
    private List<PosCategoryEnum> costCategories;
}
