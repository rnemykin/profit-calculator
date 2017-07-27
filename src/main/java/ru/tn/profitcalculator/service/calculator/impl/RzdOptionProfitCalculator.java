package ru.tn.profitcalculator.service.calculator.impl;

import org.springframework.stereotype.Service;
import ru.tn.profitcalculator.model.CardOption;
import ru.tn.profitcalculator.model.enums.BonusOptionEnum;
import ru.tn.profitcalculator.model.enums.PosCategoryEnum;
import ru.tn.profitcalculator.service.calculator.OptionProfitCalculator;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Map;

/**
 * Калькулятор ставки и суммы кэшбека для опции мультикарты РЖД
 */
@Service
public class RzdOptionProfitCalculator implements OptionProfitCalculator {

    @Override
    public CardOption calculate(CardOption cardOption, Map<PosCategoryEnum, BigDecimal> categories2Costs) {
        BigDecimal rate = cardOption.getRate3(); // 1 bonus for every 15 roubles for all purchases
        BigDecimal cashback = BigDecimal.ZERO;

        for (BigDecimal sum : categories2Costs.values()) {
            cashback = cashback.add(sum);
        }
        cashback = cashback.divide(rate, 10, RoundingMode.HALF_UP);
        cardOption.setRate(rate);
        cardOption.setCashback4Month(cashback);

        return cardOption;
    }

    @Override
    public BonusOptionEnum getOption() {
        return BonusOptionEnum.RZD;
    }
}
