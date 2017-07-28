package ru.tn.profitcalculator.service.calculator.option.impl;

import org.apache.commons.lang3.Range;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.util.Pair;
import ru.tn.profitcalculator.model.CardOption;
import ru.tn.profitcalculator.model.enums.PosCategoryEnum;
import ru.tn.profitcalculator.service.calculator.option.IOptionProfitCalculator;

import javax.annotation.PostConstruct;
import java.math.BigDecimal;
import java.util.Map;

public abstract class BaseOptionProfitCalculator implements IOptionProfitCalculator {

    @Value("${card.options.saving.threshold1}")
    private BigDecimal threshold1;

    @Value("${card.options.saving.threshold2}")
    private BigDecimal threshold2;

    @Value("${card.options.saving.threshold3}")
    private BigDecimal threshold3;

    private Range<BigDecimal> range1;
    private Range<BigDecimal> range2;
    private Range<BigDecimal> range3;

    @PostConstruct
    public void init() {
        range1 = Range.between(threshold1, threshold2.subtract(BigDecimal.valueOf(0.01)));
        range2 = Range.between(threshold2, threshold3.subtract(BigDecimal.valueOf(0.01)));
        range3 = Range.between(threshold3, BigDecimal.valueOf(Double.MAX_VALUE));
    }

    @Override
    public CardOption calculate(CardOption cardOption, Map<Pair<PosCategoryEnum, Boolean>, BigDecimal> categories2Costs) {
        BigDecimal rate = BigDecimal.ZERO;
        BigDecimal totalSum = BigDecimal.ZERO;

        for (BigDecimal categoryTransactionsSum : categories2Costs.values()) {
            totalSum = totalSum.add(categoryTransactionsSum);
        }
        if (range1.contains(totalSum)) {
            rate = cardOption.getRate1();
        } else if (range2.contains(totalSum)) {
            rate = cardOption.getRate2();
        } else if (range3.contains(totalSum)) {
            rate = cardOption.getRate3();
        }
        cardOption.setRate(rate);

        return cardOption;
    }

}
