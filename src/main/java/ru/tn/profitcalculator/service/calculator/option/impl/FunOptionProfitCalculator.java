package ru.tn.profitcalculator.service.calculator.option.impl;

import org.springframework.stereotype.Service;
import ru.tn.profitcalculator.model.enums.BonusOptionEnum;
import ru.tn.profitcalculator.model.enums.PosCategoryEnum;

/**
 * Калькулятор ставки и суммы кэшбека по опции Развлечения
 */
@Service
public class FunOptionProfitCalculator extends CashbackOptionProfitCalculator {

    @Override
    public BonusOptionEnum getOption() {
        return BonusOptionEnum.FUN;
    }

    @Override
    PosCategoryEnum getOptionCategory() {
        return PosCategoryEnum.FUN;
    }
}
