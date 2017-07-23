package ru.tn.profitcalculator.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.tn.profitcalculator.model.Product;
import ru.tn.profitcalculator.service.calculator.CalculateRequest;
import ru.tn.profitcalculator.service.calculator.CalculatorFactory;
import ru.tn.profitcalculator.web.model.ProductGroup;
import ru.tn.profitcalculator.web.model.ProductSearchRequest;

import java.util.List;

import static java.util.Collections.emptyList;

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


    public List<ProductGroup> calculateOffers(ProductSearchRequest request) {
        List<Product> products = productService.searchProducts(request.getDaysCount(), request.getMonthRefillSum(), request.getMonthWithdrawalSum());
        List<CalculateRequest> calculateRequests = calculateRequestBuilder.makeRequests(products, request);

        return emptyList();
    }
}
