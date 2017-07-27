package ru.tn.profitcalculator.service;

import lombok.extern.log4j.Log4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.tn.profitcalculator.model.Card;
import ru.tn.profitcalculator.model.Product;
import ru.tn.profitcalculator.model.RefillOption;
import ru.tn.profitcalculator.model.SavingAccount;
import ru.tn.profitcalculator.model.enums.BonusOptionEnum;
import ru.tn.profitcalculator.model.enums.CardTypeEnum;
import ru.tn.profitcalculator.model.enums.PosCategoryEnum;
import ru.tn.profitcalculator.model.enums.ProductTypeEnum;
import ru.tn.profitcalculator.repository.CardOptionRepository;
import ru.tn.profitcalculator.repository.CardRepository;
import ru.tn.profitcalculator.repository.RefillOptionRepository;
import ru.tn.profitcalculator.service.calculator.ProductCalculateRequest;
import ru.tn.profitcalculator.web.model.CalculateParams;

import java.math.BigDecimal;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

import static ru.tn.profitcalculator.model.enums.RefillOptionEventTypeEnum.FIXED_DATE;
import static ru.tn.profitcalculator.model.enums.RefillOptionSumTypeEnum.FIXED_SUM;
import static ru.tn.profitcalculator.util.MathUtils.isGreatThenZero;

@Log4j
@Service
public class CalculateRequestBuilder {

    private static final BigDecimal SAVING_ACCOUNT_MIN_SUM = BigDecimal.valueOf(5000);

    private final CardRepository cardRepository;
    private final CardOptionRepository cardOptionRepository;
    private final RefillOptionRepository refillOptionRepository;
    private final ObjectService objectService;


    @Autowired
    public CalculateRequestBuilder(CardRepository cardRepository, CardOptionRepository cardOptionRepository, RefillOptionRepository refillOptionRepository, ObjectService objectService) {
        this.cardRepository = cardRepository;
        this.cardOptionRepository = cardOptionRepository;
        this.refillOptionRepository = refillOptionRepository;
        this.objectService = objectService;
    }

    List<ProductCalculateRequest> makeRequests(List<Product> products, CalculateParams params) {
        List<ProductCalculateRequest> result = products.stream()
                .map(p -> ProductCalculateRequest.builder()
                        .product(p)
                        .params(params)
                        .build()
                )
                .collect(Collectors.toList());

        if (!isGreatThenZero(params.getMonthRefillSum()) && !isGreatThenZero(params.getMonthWithdrawalSum())) {
            result.add(makeAutoRefillRequest(getCopyOfSavingAccount(products), params));
        }

        if(params.getCategories2Costs() != null) {
            result.addAll(makeRequestsWithCardOption(products, params));
        }

        return result;
    }

    private ProductCalculateRequest makeAutoRefillRequest(SavingAccount savingAccount, CalculateParams params) {
        RefillOption autoRefillOption = refillOptionRepository.findByEventTypeAndRefillSumType(FIXED_DATE, FIXED_SUM);
        savingAccount.setRefillOption(autoRefillOption);

        return ProductCalculateRequest.builder()
                        .recommendation(true)
                        .product(savingAccount)
                        .params(params)
                        .build();
    }

    private SavingAccount getCopyOfSavingAccount(List<Product> products) {
        SavingAccount source = (SavingAccount) products.stream()
                .filter(p -> p.getType() == ProductTypeEnum.SAVING_ACCOUNT)
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Saving account product expected, but not found"));

        return objectService.clone(source);
    }

    private List<ProductCalculateRequest> makeRequestsWithCardOption(List<Product> products, CalculateParams params) {
        Map<PosCategoryEnum, BigDecimal> categories2Costs = params.getCategories2Costs();
        BigDecimal maxCostSum = categories2Costs.entrySet().stream()
                .max(Comparator.comparing(Map.Entry::getValue))
                .orElseThrow(() -> new RuntimeException("categories2Costs not set or wrong"))
                .getValue();

        BigDecimal totalSum = categories2Costs.values().stream()
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        List<PosCategoryEnum> costCategories = categories2Costs.entrySet().stream()
                .filter(e -> maxCostSum.compareTo(e.getValue()) == 0)
                .map(Map.Entry::getKey)
                .collect(Collectors.toList());

        return costCategories.stream()
                .map(category -> {
                    BonusOptionEnum bonusOption = null;
                    switch (category) {
                        case AUTO:
                            bonusOption = BonusOptionEnum.AUTO;
                            break;

                        case FUN:
                            bonusOption = BonusOptionEnum.FUN;
                            break;

                        case OTHER:
                            bonusOption = totalSum.compareTo(SAVING_ACCOUNT_MIN_SUM) < 0 ? BonusOptionEnum.CASH_BACK : BonusOptionEnum.SAVING;
                            break;
                    }

                    if(bonusOption == null) {
                        log.warn("bonusOption not found for costCategory " + category);
                        return null;
                    }

                    SavingAccount savingAccount = getCopyOfSavingAccount(products);
                    savingAccount.setLinkedProduct(getCard(bonusOption));
                    return ProductCalculateRequest.builder()
                            .product(savingAccount)
                            .params(params)
                            .build();
                })
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }

    private Card getCard(BonusOptionEnum bonusOption) {
        Card card = cardRepository.findFirstByCardTypeOrderByIdDesc(CardTypeEnum.VISA);
        card.setCardOption(cardOptionRepository.findFirstByBonusOptionOrderByIdDesc(bonusOption));
        return objectService.clone(card);
    }
}
