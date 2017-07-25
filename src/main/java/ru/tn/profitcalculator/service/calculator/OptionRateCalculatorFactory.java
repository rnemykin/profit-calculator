package ru.tn.profitcalculator.service.calculator;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import ru.tn.profitcalculator.model.enums.BonusOptionEnum;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Component
public class OptionRateCalculatorFactory {

    private static final Map<BonusOptionEnum, OptionRateCalculator> CALCULATORS = new HashMap<>();

    @Autowired
    public OptionRateCalculatorFactory(List<OptionRateCalculator> calculators) {
        calculators.forEach(c -> CALCULATORS.put(c.getOption(), c));
    }

    public OptionRateCalculator get(BonusOptionEnum option) {
        return Optional.ofNullable(CALCULATORS.get(option))
                .orElseThrow(() -> new IllegalArgumentException("No option rate calculator for option " + option));
    }
}
