package ru.tn.profitcalculator.service.calculator.option.impl;

import org.springframework.stereotype.Service;
import ru.tn.profitcalculator.model.enums.BonusOptionEnum;
import ru.tn.profitcalculator.model.enums.PosCategoryEnum;

import java.math.BigDecimal;

/**
 * Калькулятор ставки и суммы кэшбека по опции Путешествия
 */
@Service
public class TravelOptionProfitCalculator extends CashbackOptionProfitCalculator {

    @Override
    public BonusOptionEnum getOption() {
        return BonusOptionEnum.TRAVEL;
    }

    @Override
    BigDecimal limitCashback(BigDecimal cashback) {
        return cashback;
    }
}
