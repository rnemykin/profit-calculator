package ru.tn.profitcalculator.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.tn.profitcalculator.comparator.ProductGroupComparator;
import ru.tn.profitcalculator.model.Card;
import ru.tn.profitcalculator.model.Product;
import ru.tn.profitcalculator.model.SavingAccount;
import ru.tn.profitcalculator.service.calculator.Calculator;
import ru.tn.profitcalculator.service.calculator.CalculatorFactory;
import ru.tn.profitcalculator.service.calculator.ProductCalculateRequest;
import ru.tn.profitcalculator.web.model.CalculateParams;
import ru.tn.profitcalculator.web.model.ProductGroup;

import javax.annotation.PostConstruct;
import java.math.BigDecimal;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;

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

        Set<String> dublicateChecker = new HashSet<>();

        List<ProductGroup> offers = calculateRequests.stream()
                .map(r -> {
                    Calculator calculator = calculatorFactory.get(r.getProduct().getType());
                    return calculator.calculate(r);
                })
                .map(r -> {
                    if (r.getOptionProfitSum() != null && BigDecimal.ZERO.compareTo(r.getOptionProfitSum()) == 0) {
                        return null;
                    }

                    List<Product> products = new ArrayList<>();
                    products.add(r.getProduct());
                    Product linkedProduct = r.getProduct().getLinkedProduct();
                    if (linkedProduct != null) {
                        products.add(linkedProduct);
                        if (linkedProduct.getLinkedProduct() != null) {
                            products.add(linkedProduct.getLinkedProduct());
                        }
                    }

                    List<Product> productList = r.isRecommendation() ? emptyList() : products;
                    List<Product> optionalProducts = r.isRecommendation() ? products : emptyList();

                    Set<String> notes = new HashSet<>();
                    notes.addAll(getProductsNotes(productList));
                    notes.addAll(getProductsNotes(optionalProducts));

                    return ProductGroup.builder()
                            .notes(notes)
                            .resultSum(r.getTotalSum())
                            .optionProfitSum(r.getOptionProfitSum())
                            .profitSum(r.getProfitSum())
                            .maxRate(r.getMaxRate())
                            .products(productList)
                            .optionalProducts(optionalProducts)
                            .offerByClientProduct(r.isOfferByClientProduct())
                            .build();
                })
                .sorted((pg1, pg2) -> isHasRefillOption(pg2).compareTo(isHasRefillOption(pg1)))
                .collect(toList());

        return  offers.stream()
                .filter(Objects::nonNull)
                .filter(pg -> {
                    int p1 = pg.getProfitSum().intValue();
                    int p2 = Optional.ofNullable(pg.getOptionProfitSum()).orElse(BigDecimal.ZERO).intValue();
                    float p3 = pg.getMaxRate().floatValue();

                    String hash = MessageFormat.format("{0}:{1}:{2}", p1, p2, p3);
                    return dublicateChecker.add(hash);
                })
                .sorted(new ProductGroupComparator())
                .limit(offersCountLimit)
                .collect(toList());
    }

    private Boolean isHasRefillOption(ProductGroup pg) {
        for (Product product : pg.getProducts()) {
            if (product instanceof SavingAccount) {
                SavingAccount savingAccount = (SavingAccount) product;
                if(savingAccount.getRefillOption() != null) {
                    return true;
                }
            }
        }
        return false;
    }

    private Set<String> getProductsNotes(List<Product> products) {
        Set<String> notes = new HashSet<>();

        for (Product product : products) {
            notes.add(product.getDescription());

            if (product instanceof Card) {
                Card card = (Card) product;
                notes.add(card.getDescription());
            }
        }
        return notes;
    }
}
