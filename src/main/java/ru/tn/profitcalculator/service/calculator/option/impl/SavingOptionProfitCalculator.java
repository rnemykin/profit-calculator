package ru.tn.profitcalculator.service.calculator.option.impl;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import ru.tn.profitcalculator.model.enums.BonusOptionEnum;

import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 * Калькулятор ставки и профита для опции Сбережения
 */
@Service
public class SavingOptionProfitCalculator extends BaseOptionProfitCalculator {

    @Value("${card.options.saving.maxSum4Rate}")
    private BigDecimal maxSum4Rate;

    public BigDecimal calculateProfitSum(BigDecimal sum, BigDecimal rate, BigDecimal days) {
        if(sum.compareTo(maxSum4Rate) > 0) {
            sum = maxSum4Rate;
        }
        BigDecimal daysInYear = BigDecimal.valueOf(365);
        BigDecimal period = days.divide(daysInYear, 10, RoundingMode.HALF_UP);
        long profitSum = sum.multiply(rate.multiply(period)).longValue();
        return BigDecimal.valueOf(profitSum);
    }

    @Override
    public BonusOptionEnum getOption() {
        return BonusOptionEnum.SAVING;
    }
}
