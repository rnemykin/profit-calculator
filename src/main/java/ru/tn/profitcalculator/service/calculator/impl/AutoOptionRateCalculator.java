package ru.tn.profitcalculator.service.calculator.impl;

import org.springframework.stereotype.Service;
import ru.tn.profitcalculator.model.CardOption;
import ru.tn.profitcalculator.model.enums.BonusOptionEnum;
import ru.tn.profitcalculator.model.enums.PosCategoryEnum;
import ru.tn.profitcalculator.service.calculator.OptionRateCalculator;

import java.math.BigDecimal;
import java.util.Map;

/**
 * Калькулятор ставки для опции Авто
 */
@Service
public class AutoOptionRateCalculator implements OptionRateCalculator {

    @Override
    public BigDecimal calculate(CardOption cardOption, Map<PosCategoryEnum, BigDecimal> categories2Costs) {
        return cardOption.getRate3(); // 5% платиновая карта
    }

    @Override
    public BonusOptionEnum getOption() {
        return BonusOptionEnum.AUTO;
    }
}
