package ru.tn.profitcalculator.service.calculator.impl;

import org.springframework.stereotype.Service;
import ru.tn.profitcalculator.model.CardOption;
import ru.tn.profitcalculator.model.enums.BonusOptionEnum;

import java.math.BigDecimal;

/**
 * Калькулятор ставки и суммы кэшбека по опции Коллекция
 */
@Service
public class CollectionOptionProfitCalculator extends CashbackOptionProfitCalculator {

    @Override
    protected BigDecimal getRate(CardOption cardOption) {
        return cardOption.getRate3();
    }

    @Override
    protected BigDecimal limitCashback(BigDecimal cashback) {
        return cashback;
    }

    @Override
    public BonusOptionEnum getOption() {
        return BonusOptionEnum.COLLECTION;
    }
}
