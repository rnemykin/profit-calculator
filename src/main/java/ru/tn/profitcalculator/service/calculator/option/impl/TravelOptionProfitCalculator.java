package ru.tn.profitcalculator.service.calculator.option.impl;

import org.springframework.stereotype.Service;
import ru.tn.profitcalculator.model.enums.BonusOptionEnum;
import ru.tn.profitcalculator.model.enums.PosCategoryEnum;
import ru.tn.profitcalculator.service.SettingsService;

import java.math.BigDecimal;

/**
 * Калькулятор ставки и суммы кэшбека по опции Путешествия
 */
@Service
public class TravelOptionProfitCalculator extends CashbackOptionProfitCalculator {

    public TravelOptionProfitCalculator(SettingsService settingsService) {
        super(settingsService);
    }

    @Override
    public BonusOptionEnum getOption() {
        return BonusOptionEnum.TRAVEL;
    }

    @Override
    BigDecimal limitCashback(BigDecimal cashback) {
        return cashback;
    }
}
