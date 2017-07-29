package ru.tn.profitcalculator.service.calculator.option.impl;

import org.springframework.stereotype.Service;
import ru.tn.profitcalculator.model.enums.BonusOptionEnum;
import ru.tn.profitcalculator.model.enums.PosCategoryEnum;
import ru.tn.profitcalculator.service.SettingsService;

/**
 * Калькулятор ставки и суммы кэшбека по опции Развлечения
 */
@Service
public class FunOptionProfitCalculator extends CashbackOptionProfitCalculator {

    public FunOptionProfitCalculator(SettingsService settingsService) {
        super(settingsService);
    }

    @Override
    public BonusOptionEnum getOption() {
        return BonusOptionEnum.FUN;
    }

    @Override
    PosCategoryEnum getOptionCategory() {
        return PosCategoryEnum.FUN;
    }
}
