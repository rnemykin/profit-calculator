package ru.tn.profitcalculator.service.calculator.option.impl;

import org.springframework.stereotype.Service;
import ru.tn.profitcalculator.model.enums.BonusOptionEnum;

/**
 * Калькулятор ставки и суммы кэшбека по опции Коллекция
 */
@Service
public class CollectionOptionProfitCalculator extends TravelOptionProfitCalculator {

    @Override
    public BonusOptionEnum getOption() {
        return BonusOptionEnum.COLLECTION;
    }
}