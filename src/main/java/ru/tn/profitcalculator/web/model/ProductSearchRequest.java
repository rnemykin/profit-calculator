package ru.tn.profitcalculator.web.model;

import ru.tn.profitcalculator.model.enums.PosCategoryEnum;

import java.math.BigDecimal;
import java.util.Set;

public class ProductSearchRequest {
    private BigDecimal totalSum;
    private BigDecimal startSum;
    private Integer monthsCount;
    private BigDecimal monthRefillSum;
    private BigDecimal monthWithdrawalSum;
    private Set<PosCategoryEnum> costCategories;
}
