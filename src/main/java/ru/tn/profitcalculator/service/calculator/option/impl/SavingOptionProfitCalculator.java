package ru.tn.profitcalculator.service.calculator.option.impl;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import ru.tn.profitcalculator.model.enums.BonusOptionEnum;

import java.math.BigDecimal;
import java.math.RoundingMode;

import static java.math.BigDecimal.valueOf;

/**
 * Калькулятор ставки и профита для опции Сбережения
 */
@Service
public class SavingOptionProfitCalculator extends BaseOptionProfitCalculator {

    private static final BigDecimal DAYS_IN_YEAR = valueOf(365);

    @Value("${card.options.saving.maxSum4Rate}")
    private BigDecimal maxSum4Rate;

    public BigDecimal calculateProfitSum(BigDecimal sum, BigDecimal rate, BigDecimal days) {
        if(sum.compareTo(maxSum4Rate) > 0) {
            sum = maxSum4Rate;
        }
        BigDecimal period = days.divide(DAYS_IN_YEAR, 10, RoundingMode.HALF_UP);
        long profitSum = sum.multiply(rate.multiply(period)).longValue();

        return valueOf(profitSum);
    }

    @Override
    public BonusOptionEnum getOption() {
        return BonusOptionEnum.SAVING;
    }
}
