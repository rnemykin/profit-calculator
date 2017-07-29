package ru.tn.profitcalculator.service.calculator.option.impl;

import org.apache.commons.lang3.Range;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.util.Pair;
import ru.tn.profitcalculator.model.CardOption;
import ru.tn.profitcalculator.model.enums.PosCategoryEnum;
import ru.tn.profitcalculator.service.SettingsService;
import ru.tn.profitcalculator.service.calculator.option.IOptionProfitCalculator;

import javax.annotation.PostConstruct;
import java.math.BigDecimal;
import java.util.Map;

public abstract class BaseOptionProfitCalculator implements IOptionProfitCalculator {

    private static final String SETTING_PREFIX = "card.options.saving.";

    final SettingsService settingsService;

    private Range<BigDecimal> range1;
    private Range<BigDecimal> range2;
    private Range<BigDecimal> range3;

    public BaseOptionProfitCalculator(SettingsService settingsService) {
        this.settingsService = settingsService;
    }

    @PostConstruct
    public void init() {
        BigDecimal threshold1 = settingsService.getBigDecimal(SETTING_PREFIX + "threshold1");
        BigDecimal threshold2 = settingsService.getBigDecimal(SETTING_PREFIX + "threshold2");
        BigDecimal threshold3 = settingsService.getBigDecimal(SETTING_PREFIX + "threshold3");

        range1 = Range.between(threshold1, threshold2.subtract(BigDecimal.valueOf(0.01)));
        range2 = Range.between(threshold2, threshold3.subtract(BigDecimal.valueOf(0.01)));
        range3 = Range.between(threshold3, BigDecimal.valueOf(Double.MAX_VALUE));
    }

    @Override
    public CardOption calculate(CardOption cardOption, Map<PosCategoryEnum, Pair<Boolean, BigDecimal>> categories2Costs) {
        BigDecimal rate = BigDecimal.ZERO;
        BigDecimal totalSum = BigDecimal.ZERO;

        for (Map.Entry<PosCategoryEnum, Pair<Boolean, BigDecimal>> entry : categories2Costs.entrySet()) {
            PosCategoryEnum category = entry.getKey();

            if (getOptionCategory() == null || category == getOptionCategory()) {
                totalSum = totalSum.add(entry.getValue().getSecond());
            }
        }
        if (range1.contains(totalSum)) {
            rate = cardOption.getRate1();
        } else if (range2.contains(totalSum)) {
            rate = cardOption.getRate2();
        } else if (range3.contains(totalSum)) {
            rate = cardOption.getRate3();
        }
        cardOption.setRate(rate);

        return cardOption;
    }

    PosCategoryEnum getOptionCategory() {
        return null;
    }
}
