package ru.tn.profitcalculator.service.calculator.impl;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import ru.tn.profitcalculator.model.CardOption;
import ru.tn.profitcalculator.model.enums.BonusOptionEnum;
import ru.tn.profitcalculator.model.enums.PosCategoryEnum;
import ru.tn.profitcalculator.service.calculator.OptionProfitCalculator;

import java.math.BigDecimal;
import java.util.Map;

/**
 * Калькулятор ставки и суммы кэшбека по опции Авто
 */
@Service
public class AutoOptionProfitCalculator implements OptionProfitCalculator {

    @Value("${card.options.cashback.maxSum}")
    private BigDecimal maxCashbackSum;

    @Override
    public CardOption calculate(CardOption cardOption, Map<PosCategoryEnum, BigDecimal> categories2Costs) {
        BigDecimal maxRate = cardOption.getRate3();
        BigDecimal rateOther = cardOption.getRate2();
        BigDecimal cashback = BigDecimal.ZERO;

        for (Map.Entry<PosCategoryEnum, BigDecimal> entry : categories2Costs.entrySet()) {
            PosCategoryEnum category = entry.getKey();
            BigDecimal sumByCategory = entry.getValue();

            if(category == getMainCategory()) {
                cashback = cashback.add(sumByCategory.multiply(maxRate)); // 5% for auto purchases
            } else {
                cashback = cashback.add(sumByCategory.multiply(rateOther)); // 1% for other purchases
            }
        }
        if (cashback.compareTo(maxCashbackSum) > 0) {
            cashback = maxCashbackSum;
        }
        cardOption.setRate(maxRate); // max rate = 5%, platinum card
        cardOption.setCashback4Month(cashback);

        return cardOption;
    }

    protected PosCategoryEnum getMainCategory() {
        return PosCategoryEnum.AUTO;
    }

    @Override
    public BonusOptionEnum getOption() {
        return BonusOptionEnum.AUTO;
    }
}
