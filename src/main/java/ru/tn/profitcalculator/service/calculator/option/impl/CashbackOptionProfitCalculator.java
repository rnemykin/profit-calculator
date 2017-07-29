package ru.tn.profitcalculator.service.calculator.option.impl;

import org.springframework.data.util.Pair;
import org.springframework.stereotype.Service;
import ru.tn.profitcalculator.model.CardOption;
import ru.tn.profitcalculator.model.enums.BonusOptionEnum;
import ru.tn.profitcalculator.model.enums.PosCategoryEnum;
import ru.tn.profitcalculator.service.SettingsService;

import javax.annotation.PostConstruct;
import java.math.BigDecimal;
import java.util.Map;

import static java.math.BigDecimal.valueOf;

/**
 * Калькулятор ставки и суммы кэшбека для опции мультикарты Cashback
 */
@Service
public class CashbackOptionProfitCalculator extends BaseOptionProfitCalculator {

    private BigDecimal maxCashbackSum;

    public CashbackOptionProfitCalculator(SettingsService settingsService) {
        super(settingsService);
    }

    @PostConstruct
    public void init() {
        maxCashbackSum = settingsService.getBigDecimal("card.options.cashback.maxSum");
    }

    @Override
    public CardOption calculate(CardOption cardOption, Map<PosCategoryEnum, Pair<Boolean, BigDecimal>> categories2Costs) {

        CardOption result = super.calculate(cardOption, categories2Costs);
        BigDecimal rate = result.getRate();
        BigDecimal cashback = BigDecimal.ZERO;

        for (Map.Entry<PosCategoryEnum, Pair<Boolean, BigDecimal>> entry : categories2Costs.entrySet()) {
            PosCategoryEnum category = entry.getKey();

            if (getOptionCategory() == null || category == getOptionCategory()) {
                cashback = cashback.add(entry.getValue().getSecond().multiply(rate));
            }
        }
        long cashback4Month = limitCashback(cashback).longValue();
        result.setCashback4Month(valueOf(cashback4Month));

        return result;
    }

    BigDecimal limitCashback(BigDecimal cashback) {
        return cashback.compareTo(maxCashbackSum) > 0 ? maxCashbackSum : cashback;
    }

    @Override
    public BonusOptionEnum getOption() {
        return BonusOptionEnum.CASH_BACK;
    }
}
