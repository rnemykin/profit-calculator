package ru.tn.profitcalculator.service.calculator;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import ru.tn.profitcalculator.model.enums.BonusOptionEnum;
import ru.tn.profitcalculator.service.calculator.option.IOptionProfitCalculator;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Component
public class OptionProfitCalculatorFactory {

    private static final Map<BonusOptionEnum, IOptionProfitCalculator> CALCULATORS = new HashMap<>();

    @Autowired
    public OptionProfitCalculatorFactory(List<IOptionProfitCalculator> calculators) {
        calculators.forEach(c -> CALCULATORS.put(c.getOption(), c));
    }

    public IOptionProfitCalculator get(BonusOptionEnum option) {
        return Optional.ofNullable(CALCULATORS.get(option))
                .orElseThrow(() -> new IllegalArgumentException("No bonusOption rate calculator for bonusOption " + option));
    }
}
