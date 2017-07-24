package ru.tn.profitcalculator.service.calculator;

import ru.tn.profitcalculator.model.enums.ProductTypeEnum;
import ru.tn.profitcalculator.web.model.CalculateRequest;

public interface Calculator {
    CalculateResult calculate(CalculateRequest request);
    ProductTypeEnum type();
}
