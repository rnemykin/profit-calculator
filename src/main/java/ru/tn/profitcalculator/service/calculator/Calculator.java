package ru.tn.profitcalculator.service.calculator;

import ru.tn.profitcalculator.model.enums.ProductTypeEnum;

public interface Calculator {
    ProductCalculateResult calculate(ProductCalculateRequest request);
    ProductTypeEnum type();
}
