package ru.tn.profitcalculator.repository.specification;

import lombok.Builder;
import lombok.Data;
import ru.tn.profitcalculator.model.enums.BonusOptionEnum;
import ru.tn.profitcalculator.model.enums.ProductTypeEnum;

import java.util.List;

@Data
@Builder
public class ProductFilter {
    private boolean refill;
    private boolean withdrawal;
    private Integer monthsCount;
    private ProductTypeEnum type;
    private List<BonusOptionEnum> bonusOptions;

    public static ProductFilterBuilder depositFilter() {
        return builder().type(ProductTypeEnum.DEPOSIT);
    }
}
