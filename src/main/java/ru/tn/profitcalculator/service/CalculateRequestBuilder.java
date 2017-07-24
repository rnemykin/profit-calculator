package ru.tn.profitcalculator.service;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.tn.profitcalculator.model.Product;
import ru.tn.profitcalculator.model.RefillOption;
import ru.tn.profitcalculator.model.SavingAccount;
import ru.tn.profitcalculator.model.enums.ProductTypeEnum;
import ru.tn.profitcalculator.repository.CardRepository;
import ru.tn.profitcalculator.repository.RefillOptionRepository;
import ru.tn.profitcalculator.web.model.CalculateRequest;

import java.math.RoundingMode;
import java.util.List;
import java.util.stream.Collectors;

import static java.math.BigDecimal.valueOf;
import static ru.tn.profitcalculator.model.enums.RefillOptionEventTypeEnum.FIXED_DATE;
import static ru.tn.profitcalculator.model.enums.RefillOptionSumTypeEnum.FIXED_SUM;
import static ru.tn.profitcalculator.util.MathUtils.isGreatThenZero;

@Service
public class CalculateRequestBuilder {
    private final CardRepository cardRepository;
    private final RefillOptionRepository refillOptionRepository;

    @Autowired
    public CalculateRequestBuilder(CardRepository cardRepository, RefillOptionRepository refillOptionRepository) {
        this.cardRepository = cardRepository;
        this.refillOptionRepository = refillOptionRepository;
    }

    List<CalculateRequest> makeRequests(List<Product> products, CalculateRequest request) {
        List<CalculateRequest> result = products.stream().map(p -> CalculateRequest.builder()
                .product(p)
                .initSum(request.getInitSum())
                .daysCount(request.getDaysCount())
                .monthRefillSum(request.getMonthRefillSum())
                .monthWithdrawalSum(request.getMonthWithdrawalSum())
                .categories2Costs(request.getCategories2Costs())
                .build()
        ).collect(Collectors.toList());

        if(!isGreatThenZero(request.getMonthRefillSum()) && !isGreatThenZero(request.getMonthWithdrawalSum())) {
            SavingAccount product = (SavingAccount) products.stream()
                    .filter(p -> p.getType() == ProductTypeEnum.SAVING_ACCOUNT)
                    .findFirst()
                    .get();

            SavingAccount savingAccount = new SavingAccount();
            BeanUtils.copyProperties(product, savingAccount);
            RefillOption autoRefillOption = refillOptionRepository.findByEventTypeAndRefillSumType(FIXED_DATE, FIXED_SUM);
            savingAccount.setRefillOption(autoRefillOption);

            result.add(
                    CalculateRequest.builder()
                            .recommendation(true)
                            .product(savingAccount)
                            .initSum(request.getInitSum())
                            .daysCount(request.getDaysCount())
                            .monthRefillSum(request.getInitSum().divide(valueOf(10), 0, RoundingMode.UP))
                            .categories2Costs(request.getCategories2Costs())
                            .build()
            );
        }

        return result;
    }
}
