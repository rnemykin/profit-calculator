package ru.tn.profitcalculator.service.calculator.option.impl;

import org.springframework.data.util.Pair;
import org.springframework.stereotype.Service;
import ru.tn.profitcalculator.model.CardOption;
import ru.tn.profitcalculator.model.enums.BonusOptionEnum;
import ru.tn.profitcalculator.model.enums.PosCategoryEnum;
import ru.tn.profitcalculator.service.SettingsService;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Map;

/**
 * Калькулятор ставки и суммы кэшбека для опции мультикарты Cashback
 */
@Service
public class CashbackOptionProfitCalculator extends BaseOptionProfitCalculator {

    private BigDecimal maxCashbackSum;

    public CashbackOptionProfitCalculator(SettingsService settingsService) {
        super(settingsService);
    }

    @Override
    public void init() {
        super.init();
        maxCashbackSum = settingsService.getBigDecimal("card.cashback.maxSum");
    }

    @Override
    public CardOption calculate(CardOption cardOption, Map<PosCategoryEnum, Pair<Boolean, BigDecimal>> categories2Costs) {

        CardOption result = super.calculate(cardOption, categories2Costs);
        BigDecimal rate = result.getRate();
        BigDecimal cashback = BigDecimal.ZERO;
        BigDecimal cashbackInFirstMonth = BigDecimal.ZERO;
        BigDecimal maxRate = getMaxRate(cardOption);

        for (Map.Entry<PosCategoryEnum, Pair<Boolean, BigDecimal>> entry : categories2Costs.entrySet()) {
            PosCategoryEnum category = entry.getKey();

            if (getOptionCategory() == null || category == getOptionCategory()) {
                cashback = cashback.add(entry.getValue().getSecond().multiply(rate));
                cashbackInFirstMonth = cashbackInFirstMonth.add(entry.getValue().getSecond().multiply(maxRate));
            }
        }
        cashback = limitCashback(cashback).setScale(0, RoundingMode.HALF_UP);
        result.setCashback4Month(cashback);

        cashbackInFirstMonth = limitCashback(cashbackInFirstMonth).setScale(0, RoundingMode.HALF_UP);
        result.setCashbackInFirstMonth(cashbackInFirstMonth);

        return result;
    }

    BigDecimal limitCashback(BigDecimal cashback) {
        return cashback.compareTo(maxCashbackSum) > 0 ? maxCashbackSum : cashback;
    }

    @Override
    public BonusOptionEnum getOption() {
        return BonusOptionEnum.CASH_BACK;
    }

    private BigDecimal getMaxRate(CardOption cardOption) {
        return cardOption.getRate3();
    }
}
