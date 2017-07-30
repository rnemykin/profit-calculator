package ru.tn.profitcalculator.web.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.util.Pair;
import ru.tn.profitcalculator.model.enums.PosCategoryEnum;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CalculateParams {
    @NotNull
    private BigDecimal initSum;

    @NotNull
    @Min(91)
    @Max(1831)
    private Integer daysCount;
    private Boolean payrollProject;
    private Boolean creditCard;
    private BigDecimal monthRefillSum;
    private BigDecimal monthWithdrawalSum;
    private Map<PosCategoryEnum, Pair<Boolean, BigDecimal>> categories2Costs = new HashMap<>();
    private List<Transactions> transactions = new ArrayList<>();
    private List<Payroll> payroll = new ArrayList<>();
}
