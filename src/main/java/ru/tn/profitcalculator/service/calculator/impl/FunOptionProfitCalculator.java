package ru.tn.profitcalculator.service.calculator.impl;

import org.springframework.stereotype.Service;
import ru.tn.profitcalculator.model.enums.BonusOptionEnum;
import ru.tn.profitcalculator.model.enums.PosCategoryEnum;

/**
 * Калькулятор ставки и суммы кэшбека по опции Развлечения (Карта впечатлений)
 */
@Service
public class FunOptionProfitCalculator extends AutoOptionProfitCalculator {

    @Override
    public BonusOptionEnum getOption() {
        return BonusOptionEnum.FUN;
    }

    @Override
    protected PosCategoryEnum getMainCategory() {
        return PosCategoryEnum.FUN;
    }
}
