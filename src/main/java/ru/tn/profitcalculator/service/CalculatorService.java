package ru.tn.profitcalculator.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.tn.profitcalculator.comparator.ProductGroupComparator;
import ru.tn.profitcalculator.model.Product;
import ru.tn.profitcalculator.service.calculator.Calculator;
import ru.tn.profitcalculator.service.calculator.CalculatorFactory;
import ru.tn.profitcalculator.service.calculator.ProductCalculateRequest;
import ru.tn.profitcalculator.web.model.CalculateParams;
import ru.tn.profitcalculator.web.model.ProductGroup;

import javax.annotation.PostConstruct;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static java.util.Collections.emptyList;
import static java.util.stream.Collectors.toList;

@Service
public class CalculatorService {

    private final ProductService productService;
    private final CalculatorFactory calculatorFactory;
    private final CalculateRequestBuilder calculateRequestBuilder;
    private final SettingsService settingsService;

    private long offersCountLimit;

    @Autowired
    public CalculatorService(ProductService productService, CalculatorFactory calculatorFactory, CalculateRequestBuilder calculateRequestBuilder, SettingsService settingsService) {
        this.productService = productService;
        this.calculatorFactory = calculatorFactory;
        this.calculateRequestBuilder = calculateRequestBuilder;
        this.settingsService = settingsService;
    }

    @PostConstruct
    public void init() {
        offersCountLimit = settingsService.getInt("offer.countLimit");
    }

    public List<ProductGroup> calculateOffers(CalculateParams params) {
        Integer daysCount = params.getDaysCount();
        BigDecimal monthRefillSum = params.getMonthRefillSum();
        BigDecimal monthWithdrawalSum = params.getMonthWithdrawalSum();

        List<Product> foundProducts = productService.searchProducts(daysCount, monthRefillSum, monthWithdrawalSum);
        List<ProductCalculateRequest> calculateRequests = calculateRequestBuilder.makeRequests(foundProducts, params);
        return calculateRequests.stream()
                .map(r -> {
                    Calculator calculator = calculatorFactory.get(r.getProduct().getType());
                    return calculator.calculate(r);
                })
                .map(r -> {
                    if(r.getOptionProfitSum() != null && BigDecimal.ZERO.compareTo(r.getOptionProfitSum()) == 0) {
                        return null;
                    }

                    List<Product> products = new ArrayList<>();
                    products.add(r.getProduct());
                    if (r.getProduct().getLinkedProduct() != null) {
                        products.add(r.getProduct().getLinkedProduct());
                    }

                    return ProductGroup.builder()
                            .resultSum(r.getTotalSum())
                            .optionProfitSum(r.getOptionProfitSum())
                            .profitSum(r.getProfitSum())
                            .maxRate(r.getMaxRate())
                            .products(r.isRecommendation() ? emptyList() : products)
                            .optionalProducts(r.isRecommendation() ? products : emptyList())
                            .build();
                })
                .filter(Objects::nonNull)
                .sorted(new ProductGroupComparator())
                .limit(offersCountLimit)
                .collect(toList());
    }
}
