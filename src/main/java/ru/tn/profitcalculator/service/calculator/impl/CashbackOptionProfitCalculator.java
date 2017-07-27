package ru.tn.profitcalculator.service.calculator.impl;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import ru.tn.profitcalculator.model.CardOption;
import ru.tn.profitcalculator.model.enums.BonusOptionEnum;
import ru.tn.profitcalculator.model.enums.PosCategoryEnum;
import ru.tn.profitcalculator.service.calculator.OptionProfitCalculator;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Map;

/**
 * Калькулятор ставки и суммы кэшбека для опции мультикарты Cashback
 */
@Service
public class CashbackOptionProfitCalculator implements OptionProfitCalculator {

    @Value("${card.options.cashback.maxSum}")
    private BigDecimal maxCashbackSum;

    @Override
    public CardOption calculate(CardOption cardOption, Map<PosCategoryEnum, BigDecimal> categories2Costs) {
        BigDecimal rate = getRate(cardOption); // 1% for all purchases
        BigDecimal cashback = BigDecimal.ZERO;

        for (BigDecimal sum : categories2Costs.values()) {
            cashback = cashback.add(sum.multiply(rate));
        }
        cardOption.setRate(rate);
        cardOption.setCashback4Month(limitCashback(cashback));

        return cardOption;
    }

    protected BigDecimal limitCashback(BigDecimal cashback) {
        if (cashback.compareTo(maxCashbackSum) > 0) {
            cashback = maxCashbackSum;
        }
        return cashback;
    }

    protected BigDecimal getRate(CardOption cardOption) {
        return cardOption.getRate1();
    }

    @Override
    public BonusOptionEnum getOption() {
        return BonusOptionEnum.CASH_BACK;
    }
}
