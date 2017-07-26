package ru.tn.profitcalculator.service.calculator.impl;

import org.apache.commons.lang3.Range;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import ru.tn.profitcalculator.model.CardOption;
import ru.tn.profitcalculator.model.enums.BonusOptionEnum;
import ru.tn.profitcalculator.model.enums.PosCategoryEnum;
import ru.tn.profitcalculator.service.calculator.OptionProfitCalculator;

import javax.annotation.PostConstruct;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Map;

/**
 * Калькулятор ставки и профита для опции Сбережения
 */
@Service
public class SavingOptionProfitCalculator implements OptionProfitCalculator {

    @Value("${card.options.saving.threshold1}")
    private BigDecimal threshold1;

    @Value("${card.options.saving.threshold2}")
    private BigDecimal threshold2;

    @Value("${card.options.saving.threshold3}")
    private BigDecimal threshold3;

    @Value("${card.options.saving.maxSum4Rate}")
    private BigDecimal maxSum4Rate;

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
    public CardOption calculate(CardOption cardOption, Map<PosCategoryEnum, BigDecimal> categories2Costs) {
        BigDecimal rate = BigDecimal.ZERO;
        BigDecimal totalTransactionSum = BigDecimal.ZERO;

        for (BigDecimal categoryTransactionsSum : categories2Costs.values()) {
            totalTransactionSum = totalTransactionSum.add(categoryTransactionsSum);
        }
        if (range1.contains(totalTransactionSum)) {
            rate = cardOption.getRate1();
        } else if (range2.contains(totalTransactionSum)) {
            rate = cardOption.getRate2();
        } else if (range3.contains(totalTransactionSum)) {
            rate = cardOption.getRate3();
        }
        cardOption.setRate(rate);

        return cardOption;
    }

    protected BigDecimal calculateProfitSum(BigDecimal sum, BigDecimal rate, BigDecimal days) {
        if(sum.compareTo(maxSum4Rate) > 0) {
            sum = maxSum4Rate;
        }
        return sum.multiply(
                        rate.multiply(
                                days.divide(BigDecimal.valueOf(365), 10, RoundingMode.HALF_UP)))
                .divide(BigDecimal.valueOf(100), 0, RoundingMode.HALF_UP);
    }

    @Override
    public BonusOptionEnum getOption() {
        return BonusOptionEnum.SAVING;
    }
}
