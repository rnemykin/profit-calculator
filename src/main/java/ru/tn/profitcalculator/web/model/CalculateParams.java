package ru.tn.profitcalculator.web.model;

import lombok.Builder;
import lombok.Data;
import ru.tn.profitcalculator.model.enums.PosCategoryEnum;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

@Data
@Builder
public class CalculateParams {
    @NotNull
    private BigDecimal initSum;

    @NotNull
    @Min(91)
    private Integer daysCount;
    private BigDecimal monthRefillSum;
    private BigDecimal monthWithdrawalSum;
    private Map<PosCategoryEnum, BigDecimal> categories2Costs = new HashMap<>();
}
