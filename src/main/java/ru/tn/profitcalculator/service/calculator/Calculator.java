package ru.tn.profitcalculator.service.calculator;

import ru.tn.profitcalculator.model.enums.ProductTypeEnum;

public interface Calculator {
    CalculateResult calculate(CalculateRequest request);
    ProductTypeEnum type();
}
