package ru.tn.profitcalculator.service.calculator;

import lombok.Builder;
import lombok.Data;
import ru.tn.profitcalculator.model.Product;
import ru.tn.profitcalculator.web.model.CalculateParams;
import ru.tn.profitcalculator.web.model.ClientProduct;

@Data
@Builder
public class ProductCalculateRequest {
    private Product product;
    private boolean recommendation;
    private CalculateParams params;
    private ClientProduct clientProduct;
}
