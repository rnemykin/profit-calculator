package ru.tn.profitcalculator.service.calculator;

import ru.tn.profitcalculator.model.CardOption;
import ru.tn.profitcalculator.model.enums.BonusOptionEnum;
import ru.tn.profitcalculator.model.enums.PosCategoryEnum;

import java.math.BigDecimal;
import java.util.Map;

public interface OptionRateCalculator {

    BigDecimal calculate(CardOption cardOption, Map<PosCategoryEnum, BigDecimal> categories2Costs);

    BonusOptionEnum getOption();
}
