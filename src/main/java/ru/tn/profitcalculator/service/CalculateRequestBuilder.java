package ru.tn.profitcalculator.service;

import lombok.extern.log4j.Log4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.util.Pair;
import org.springframework.stereotype.Service;
import ru.tn.profitcalculator.model.Card;
import ru.tn.profitcalculator.model.Product;
import ru.tn.profitcalculator.model.RefillOption;
import ru.tn.profitcalculator.model.SavingAccount;
import ru.tn.profitcalculator.model.enums.BonusOptionEnum;
import ru.tn.profitcalculator.model.enums.CardCategoryEnum;
import ru.tn.profitcalculator.model.enums.PosCategoryEnum;
import ru.tn.profitcalculator.model.enums.ProductTypeEnum;
import ru.tn.profitcalculator.repository.CardOptionRepository;
import ru.tn.profitcalculator.repository.CardRepository;
import ru.tn.profitcalculator.repository.RefillOptionRepository;
import ru.tn.profitcalculator.service.calculator.ProductCalculateRequest;
import ru.tn.profitcalculator.util.CardUtils;
import ru.tn.profitcalculator.web.model.CalculateParams;
import ru.tn.profitcalculator.web.model.ClientProduct;

import javax.annotation.PostConstruct;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

import static ru.tn.profitcalculator.model.enums.CardCategoryEnum.CREDIT;
import static ru.tn.profitcalculator.model.enums.CardCategoryEnum.DEBIT;
import static ru.tn.profitcalculator.model.enums.RefillOptionEventTypeEnum.FIXED_DATE;
import static ru.tn.profitcalculator.model.enums.RefillOptionSumTypeEnum.FIXED_SUM;

@Log4j
@Service
public class CalculateRequestBuilder {

    private final CardRepository cardRepository;
    private final CardOptionRepository cardOptionRepository;
    private final RefillOptionRepository refillOptionRepository;
    private final SettingsService settingsService;
    private final ObjectService objectService;

    private BigDecimal refillSumPercentage;

    @Autowired
    public CalculateRequestBuilder(CardRepository cardRepository, CardOptionRepository cardOptionRepository, RefillOptionRepository refillOptionRepository, SettingsService settingsService, ObjectService objectService) {
        this.cardRepository = cardRepository;
        this.cardOptionRepository = cardOptionRepository;
        this.refillOptionRepository = refillOptionRepository;
        this.settingsService = settingsService;
        this.objectService = objectService;
    }

    @PostConstruct
    public void init() {
        refillSumPercentage = settingsService.getBigDecimal("savingAccount.refillSumPercentage");
    }

    List<ProductCalculateRequest> makeRequests(List<Product> products, CalculateParams params) {
        List<ProductCalculateRequest> result = products.parallelStream()
                .map(p -> ProductCalculateRequest.builder()
                        .product(p)
                        .params(params)
                        .build()
                )
                .collect(Collectors.toList());

        if (Boolean.TRUE.equals(params.getPayrollProject())) {
            result.add(makeAutoRefillRequest(getCopyOfSavingAccount(products), params));
        }

        if (Boolean.TRUE.equals(params.getDecrease()) && params.getCategories2Costs() != null) {
            result.addAll(makeRequestsWithCardOption(products, params));
        }

        if (params.getClientProducts() != null && !params.getClientProducts().isEmpty()) {
            result.addAll(makeSavingAccountRequestsByClientProducts(products, params));
        }
        return result;
    }

    private List<ProductCalculateRequest> makeSavingAccountRequestsByClientProducts(List<Product> products, CalculateParams params) {
        List<ProductCalculateRequest> requests = new ArrayList<>();
        for (ClientProduct clientProduct : params.getClientProducts()) {

            SavingAccount savingAccount = getCopyOfSavingAccount(products);
            savingAccount.setLinkedProduct(getCard(BonusOptionEnum.SAVING, params.getCreditCard()));

            requests.add(ProductCalculateRequest.builder()
                            .product(savingAccount)
                            .clientProduct(clientProduct)
                            .params(params)
                            .build());
        }
        return requests;
    }

    private ProductCalculateRequest makeAutoRefillRequest(SavingAccount savingAccount, CalculateParams params) {
        RefillOption autoRefillOption = refillOptionRepository.findByEventTypeAndRefillSumType(FIXED_DATE, FIXED_SUM);
        savingAccount.setRefillOption(autoRefillOption);

        CalculateParams refillParams = objectService.clone(params);
        BigDecimal refillPercent = refillSumPercentage.divide(BigDecimal.valueOf(100), 10, RoundingMode.HALF_UP);
        BigDecimal monthRefillSum = params.getInitSum().multiply(refillPercent).setScale(0, RoundingMode.HALF_UP);
        refillParams.setMonthRefillSum(monthRefillSum);

        return ProductCalculateRequest.builder()
                .product(savingAccount)
                .params(refillParams)
                .build();
    }

    private SavingAccount getCopyOfSavingAccount(List<Product> products) {
        SavingAccount source = (SavingAccount) products.parallelStream()
                .filter(p -> p.getType() == ProductTypeEnum.SAVING_ACCOUNT)
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Saving account product expected, but not found"));

        return objectService.clone(source);
    }

    private List<ProductCalculateRequest> makeRequestsWithCardOption(List<Product> products, CalculateParams params) {
        Set<PosCategoryEnum> costCategories = getRelevantCategories(params);
        return costCategories.parallelStream()
                .flatMap(category -> {
                    List<BonusOptionEnum> bonusOptions = CardUtils.getBonusOptionsByPosCategories(category);
                    if (bonusOptions.isEmpty()) {
                        log.warn("bonus options not set for category " + category);
                        return null;
                    }

                    return bonusOptions.parallelStream()
                            .map(option -> {
                                SavingAccount savingAccount = getCopyOfSavingAccount(products);
                                savingAccount.setLinkedProduct(getCard(option, params.getCreditCard()));

                                return ProductCalculateRequest.builder()
                                        .product(savingAccount)
                                        .params(params)
                                        .build();
                            })
                            .collect(Collectors.toList())
                            .parallelStream();
                })
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }

    private Set<PosCategoryEnum> getRelevantCategories(CalculateParams params) {
        Map<PosCategoryEnum, Pair<Boolean, BigDecimal>> categories2Costs = params.getCategories2Costs();

        return categories2Costs.entrySet().parallelStream()
                .filter(e -> Boolean.TRUE.equals(e.getValue().getFirst()))
                .map(Map.Entry::getKey)
                .collect(Collectors.toSet());
    }

    private Card getCard(BonusOptionEnum bonusOption, Boolean creditCard) {
        CardCategoryEnum cardCategory = Boolean.TRUE.equals(creditCard) ? CREDIT : DEBIT;
        Card card = cardRepository.findFirstByCardCategoryOrderByIdDesc(cardCategory);
        card.setCardOption(cardOptionRepository.findFirstByBonusOptionOrderByIdDesc(bonusOption));

        if (CREDIT == cardCategory && BonusOptionEnum.SAVING == bonusOption) {
            card.setLinkedProduct(cardRepository.findFirstByCardCategoryOrderByIdDesc(DEBIT));
        }
        return objectService.clone(card);
    }
}
