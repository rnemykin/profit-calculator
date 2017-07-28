package ru.tn.profitcalculator.service;

import lombok.extern.log4j.Log4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
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
import ru.tn.profitcalculator.web.model.CalculateParams;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
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
    private final ObjectService objectService;

    @Value("${savingAccount.defaultRefillSum}")
    private BigDecimal defaultRefillSum;

    @Autowired
    public CalculateRequestBuilder(CardRepository cardRepository, CardOptionRepository cardOptionRepository, RefillOptionRepository refillOptionRepository, ObjectService objectService) {
        this.cardRepository = cardRepository;
        this.cardOptionRepository = cardOptionRepository;
        this.refillOptionRepository = refillOptionRepository;
        this.objectService = objectService;
    }

    List<ProductCalculateRequest> makeRequests(List<Product> products, CalculateParams params) {
        List<ProductCalculateRequest> result = products.parallelStream()
                .map(p -> ProductCalculateRequest.builder()
                        .product(p)
                        .params(params)
                        .build()
                )
                .collect(Collectors.toList());

        if (Boolean.TRUE.equals(params.getSalaryClient())) {
            result.add(makeAutoRefillRequest(getCopyOfSavingAccount(products), params));
        }

        if (params.getCategories2Costs() != null) {
            result.addAll(makeRequestsWithCardOption(products, params));
        }

        return result;
    }

    private ProductCalculateRequest makeAutoRefillRequest(SavingAccount savingAccount, CalculateParams params) {
        RefillOption autoRefillOption = refillOptionRepository.findByEventTypeAndRefillSumType(FIXED_DATE, FIXED_SUM);
        savingAccount.setRefillOption(autoRefillOption);

        CalculateParams refillParams = objectService.clone(params);
        refillParams.setMonthRefillSum(defaultRefillSum);
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
        Map<PosCategoryEnum, BigDecimal> categories2Costs = params.getCategories2Costs();
        BigDecimal maxCostSum = categories2Costs.entrySet().parallelStream()
                .max(Comparator.comparing(Map.Entry::getValue))
                .orElseThrow(() -> new RuntimeException("categories2Costs not set or wrong"))
                .getValue();

        List<PosCategoryEnum> costCategories = categories2Costs.entrySet().parallelStream()
                .filter(e -> maxCostSum.compareTo(e.getValue()) == 0)
                .map(Map.Entry::getKey)
                .collect(Collectors.toList());

        return costCategories.parallelStream()
                .flatMap(category -> {
                    List<BonusOptionEnum> bonusOptions = new ArrayList<>();

                    switch (category) {
                        case AUTO:
                            bonusOptions.add(BonusOptionEnum.AUTO);
                            break;

                        case FUN:
                            bonusOptions.add(BonusOptionEnum.FUN);
                            break;

                        case TRAVEL:
                            bonusOptions.add(BonusOptionEnum.TRAVEL);
                            bonusOptions.add(BonusOptionEnum.RZD);
                            break;

                        case OTHER:
                            bonusOptions.add(BonusOptionEnum.SAVING);
                            bonusOptions.add(BonusOptionEnum.CASH_BACK);
                            bonusOptions.add(BonusOptionEnum.COLLECTION);
                    }

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

    private Card getCard(BonusOptionEnum bonusOption, Boolean creditCard) {
        CardCategoryEnum cardCategory = Boolean.TRUE.equals(creditCard) ? CREDIT : DEBIT;
        Card card = cardRepository.findFirstByCardCategoryOrderByIdDesc(cardCategory);
        card.setCardOption(cardOptionRepository.findFirstByBonusOptionOrderByIdDesc(bonusOption));

        if(CREDIT == cardCategory) {
            card.setLinkedProduct(cardRepository.findFirstByCardCategoryOrderByIdDesc(DEBIT));
        }
        return objectService.clone(card);
    }
}
