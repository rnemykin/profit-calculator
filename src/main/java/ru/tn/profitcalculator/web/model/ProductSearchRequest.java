package ru.tn.profitcalculator.web.model;

import lombok.Builder;
import lombok.Data;
import ru.tn.profitcalculator.model.enums.PosCategoryEnum;

import java.math.BigDecimal;
import java.util.List;

@Data
@Builder
public class ProductSearchRequest {
    private BigDecimal totalSum;
    private BigDecimal startSum;
    private Integer daysCount;
    private BigDecimal monthRefillSum;
    private BigDecimal monthWithdrawalSum;
    private List<PosCategoryEnum> costCategories;
}
