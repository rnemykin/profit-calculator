package ru.tn.profitcalculator.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.tn.profitcalculator.model.Product;
import ru.tn.profitcalculator.service.calculator.Calculator;
import ru.tn.profitcalculator.service.calculator.CalculatorFactory;
import ru.tn.profitcalculator.service.calculator.ProductCalculateRequest;
import ru.tn.profitcalculator.web.model.CalculateParams;
import ru.tn.profitcalculator.web.model.ProductGroup;

import java.util.List;

import static java.util.stream.Collectors.toList;

@Service
public class CalculatorService {
    private final ProductService productService;
    private final CalculatorFactory calculatorFactory;
    private final CalculateRequestBuilder calculateRequestBuilder;

    @Autowired
    public CalculatorService(ProductService productService, CalculatorFactory calculatorFactory, CalculateRequestBuilder calculateRequestBuilder) {
        this.productService = productService;
        this.calculatorFactory = calculatorFactory;
        this.calculateRequestBuilder = calculateRequestBuilder;
    }


    public List<ProductGroup> calculateOffers(CalculateParams params) {
        List<Product> products = productService.searchProducts(params.getDaysCount(), params.getMonthRefillSum(), params.getMonthWithdrawalSum());
        List<ProductCalculateRequest> calculateRequests = calculateRequestBuilder.makeRequests(products, params);

        return calculateRequests.stream().map(r -> {
            Calculator calculator = calculatorFactory.get(r.getProduct().getType());
            return calculator.calculate(r);
        }).map(r -> {

            return new ProductGroup();
        }).collect(toList());
    }
}
