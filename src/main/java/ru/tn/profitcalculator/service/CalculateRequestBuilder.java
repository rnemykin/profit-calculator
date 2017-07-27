package ru.tn.profitcalculator.service;

import org.springframework.beans.BeanUtils;
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
import java.util.stream.Collectors;

import static ru.tn.profitcalculator.model.enums.RefillOptionEventTypeEnum.FIXED_DATE;
import static ru.tn.profitcalculator.model.enums.RefillOptionSumTypeEnum.FIXED_SUM;
import static ru.tn.profitcalculator.util.MathUtils.isGreatThenZero;

@Service
public class CalculateRequestBuilder {

    private static final BigDecimal CASH_BACK_AND_SAVING_OPTION_THRESHOLD = BigDecimal.valueOf(5000);

    private final CardRepository cardRepository;
    private final CardOptionRepository cardOptionRepository;
    private final RefillOptionRepository refillOptionRepository;

    @Autowired
    public CalculateRequestBuilder(CardRepository cardRepository, CardOptionRepository cardOptionRepository, RefillOptionRepository refillOptionRepository) {
        this.cardRepository = cardRepository;
        this.cardOptionRepository = cardOptionRepository;
        this.refillOptionRepository = refillOptionRepository;
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
            result.add(makeRequestWithCardOption(getCopyOfSavingAccount(products), params));
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

        SavingAccount savingAccount = new SavingAccount();
        BeanUtils.copyProperties(source, savingAccount);
        return savingAccount;
    }

    private ProductCalculateRequest makeRequestWithCardOption(SavingAccount savingAccount, CalculateParams params) {
        Map<PosCategoryEnum, BigDecimal> categories2Costs = params.getCategories2Costs();
        PosCategoryEnum maxCostCategory = categories2Costs.entrySet().stream()
                .max(Comparator.comparing(Map.Entry::getValue))
                .orElseThrow(() -> new RuntimeException("categories2Costs not set or wrong"))
                .getKey();

        BigDecimal totalSum = categories2Costs.values().stream()
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BonusOptionEnum bonusOption;
        switch (maxCostCategory) {
            case AUTO:
                bonusOption = BonusOptionEnum.AUTO; break;
            case FUN:
                bonusOption = BonusOptionEnum.FUN; break;
            default:
                bonusOption = totalSum.compareTo(CASH_BACK_AND_SAVING_OPTION_THRESHOLD) < 0 ? BonusOptionEnum.CASH_BACK : BonusOptionEnum.SAVING;
        }

        Card card = cardRepository.findFirstByCardTypeOrderByIdDesc(CardTypeEnum.VISA);
        card.setCardOption(cardOptionRepository.findFirstByBonusOptionOrderByIdDesc(bonusOption));
        savingAccount.setLinkedProduct(card);
        return ProductCalculateRequest.builder()
                .product(savingAccount)
                .params(params)
                .build();
    }
}
