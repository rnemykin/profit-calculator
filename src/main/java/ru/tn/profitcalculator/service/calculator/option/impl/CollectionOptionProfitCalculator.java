package ru.tn.profitcalculator.service.calculator.option.impl;

import org.springframework.stereotype.Service;
import ru.tn.profitcalculator.model.enums.BonusOptionEnum;
import ru.tn.profitcalculator.service.SettingsService;

/**
 * Калькулятор ставки и суммы кэшбека по опции Коллекция
 */
@Service
public class CollectionOptionProfitCalculator extends TravelOptionProfitCalculator {

    public CollectionOptionProfitCalculator(SettingsService settingsService) {
        super(settingsService);
    }

    @Override
    public BonusOptionEnum getOption() {
        return BonusOptionEnum.COLLECTION;
    }
}
