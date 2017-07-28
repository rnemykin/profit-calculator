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
        BigDecimal rate = result.getRate();
        BigDecimal cashback = BigDecimal.ZERO;

        for (Map.Entry<PosCategoryEnum, BigDecimal> entry : categories2Costs.entrySet()) {
            PosCategoryEnum category = entry.getKey();

            if (getOptionCategory() == null || category == getOptionCategory()) {
                cashback = cashback.add(entry.getValue().multiply(rate));
            }
        }
        BigDecimal cashback4Month = limitCashback(cashback);
        result.setCashback4Month(cashback4Month);

        return result;
    }

    PosCategoryEnum getOptionCategory() {
        return null;
    }

    BigDecimal limitCashback(BigDecimal cashback) {
        return cashback.compareTo(maxCashbackSum) > 0 ? maxCashbackSum : cashback;
    }

    @Override
    public BonusOptionEnum getOption() {
        return BonusOptionEnum.CASH_BACK;
    }
}
