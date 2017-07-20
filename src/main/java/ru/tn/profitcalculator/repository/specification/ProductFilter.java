package ru.tn.profitcalculator.repository.specification;

import lombok.Builder;
import lombok.Data;
import ru.tn.profitcalculator.model.enums.PosCategoryEnum;
import ru.tn.profitcalculator.model.enums.ProductTypeEnum;

import java.math.BigDecimal;
import java.util.List;

@Data
@Builder
public class ProductFilter {
    private BigDecimal sum;
    private Integer monthsCount;
    private ProductTypeEnum type;
    private BigDecimal monthRefillSum;
    private BigDecimal monthWithdrawalSum;
    private List<PosCategoryEnum> costCategories;

    public static ProductFilterBuilder depositFilter() {
        return builder().type(ProductTypeEnum.DEPOSIT);
    }
}
