package ru.tn.profitcalculator.service.calculator.option.impl;

import org.springframework.stereotype.Service;
import ru.tn.profitcalculator.model.enums.BonusOptionEnum;
import ru.tn.profitcalculator.model.enums.PosCategoryEnum;

/**
 * Калькулятор ставки и суммы кэшбека по опции Авто
 */
@Service
public class AutoOptionProfitCalculator extends CashbackOptionProfitCalculator {

    @Override
    public BonusOptionEnum getOption() {
        return BonusOptionEnum.AUTO;
    }

    @Override
    PosCategoryEnum getOptionCategory() {
        return PosCategoryEnum.AUTO;
    }
}
