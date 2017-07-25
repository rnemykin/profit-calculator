package ru.tn.profitcalculator.service.calculator.impl;

import org.apache.commons.lang3.Range;
import org.springframework.stereotype.Service;
import ru.tn.profitcalculator.model.CardOption;
import ru.tn.profitcalculator.model.enums.BonusOptionEnum;
import ru.tn.profitcalculator.model.enums.PosCategoryEnum;
import ru.tn.profitcalculator.service.calculator.OptionRateCalculator;

import java.math.BigDecimal;
import java.util.Map;

@Service
public class SavingOptionRateCalculator implements OptionRateCalculator {

    private static final Range<BigDecimal> RANGE_1 = Range.between(BigDecimal.valueOf(5000), BigDecimal.valueOf(14999.99));
    private static final Range<BigDecimal> RANGE_2 = Range.between(BigDecimal.valueOf(15000), BigDecimal.valueOf(74999.99));
    private static final Range<BigDecimal> RANGE_3 = Range.between(BigDecimal.valueOf(75000), BigDecimal.valueOf(Double.MAX_VALUE));

    @Override
    public BigDecimal calculate(CardOption cardOption, Map<PosCategoryEnum, BigDecimal> categories2Costs) {
        BigDecimal totalTransactionSum = BigDecimal.ZERO;

        for (BigDecimal categoryTransactionsSum : categories2Costs.values()) {
            totalTransactionSum = totalTransactionSum.add(categoryTransactionsSum);
        }
        if (RANGE_1.contains(totalTransactionSum)) {
            return cardOption.getRate1();
        } else if (RANGE_2.contains(totalTransactionSum)) {
            return cardOption.getRate2();
        } else if (RANGE_3.contains(totalTransactionSum)) {
            return cardOption.getRate3();
        }
        return BigDecimal.ZERO;
    }

    @Override
    public BonusOptionEnum getOption() {
        return BonusOptionEnum.SAVING;
    }
}
