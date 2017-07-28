package ru.tn.profitcalculator.service.calculator.option.impl;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import ru.tn.profitcalculator.model.CardOption;
import ru.tn.profitcalculator.model.enums.BonusOptionEnum;
import ru.tn.profitcalculator.model.enums.PosCategoryEnum;

import java.math.BigDecimal;
import java.util.Map;

/**
 * Калькулятор ставки и суммы кэшбека для опции мультикарты Cashback
 */
@Service
public class CashbackOptionProfitCalculator extends BaseOptionProfitCalculator {

    @Value("${card.options.cashback.maxSum}")
    private BigDecimal maxCashbackSum;

    @Override
    public CardOption calculate(CardOption cardOption, Map<PosCategoryEnum, BigDecimal> categories2Costs) {
        CardOption result = super.calculate(cardOption, categories2Costs);

        BigDecimal cashback4Month = result.getCashback4Month();
        result.setCashback4Month(limitCashback(cashback4Month));

        return result;
    }

    BigDecimal limitCashback(BigDecimal cashback) {
        if (cashback.compareTo(maxCashbackSum) > 0) {
            cashback = maxCashbackSum;
        }
        return cashback;
    }

    @Override
    public BonusOptionEnum getOption() {
        return BonusOptionEnum.CASH_BACK;
    }
}
