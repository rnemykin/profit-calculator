package ru.tn.profitcalculator.service.calculator;

import ru.tn.profitcalculator.model.enums.ProductTypeEnum;

public interface CalculatorService {
    CalculateResult calculate(CalculateRequest request);
    ProductTypeEnum type();
}
