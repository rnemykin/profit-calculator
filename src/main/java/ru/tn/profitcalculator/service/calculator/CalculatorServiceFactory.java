package ru.tn.profitcalculator.service.calculator;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import ru.tn.profitcalculator.model.enums.ProductTypeEnum;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Component
public class CalculatorServiceFactory {
    private static final Map<ProductTypeEnum, CalculatorService> CALCULATORS = new HashMap<>();

    @Autowired
    public CalculatorServiceFactory(List<CalculatorService> calculatorServices) {
        calculatorServices.forEach(cs -> CALCULATORS.put(cs.type(), cs));
    }

    public CalculatorService get(ProductTypeEnum type) {
        return Optional.ofNullable(CALCULATORS.get(type))
                .orElseThrow(() -> new IllegalArgumentException("no calculator for type " + type));
    }
}
