package ru.tn.profitcalculator.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.tn.profitcalculator.model.Product;
import ru.tn.profitcalculator.model.RefillOption;
import ru.tn.profitcalculator.model.SavingAccount;
import ru.tn.profitcalculator.model.enums.ProductTypeEnum;
import ru.tn.profitcalculator.repository.RefillOptionRepository;
import ru.tn.profitcalculator.service.calculator.CalculateRequest;
import ru.tn.profitcalculator.web.model.ProductSearchRequest;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

import static ru.tn.profitcalculator.model.enums.RefillOptionEventTypeEnum.FIXED_DATE;
import static ru.tn.profitcalculator.model.enums.RefillOptionSumTypeEnum.FIXED_SUM;
import static ru.tn.profitcalculator.util.MathUtils.isGreatThenZero;

@Service
public class CalculateRequestBuilder {
    @Autowired
    private RefillOptionRepository refillOptionRepository;

    List<CalculateRequest> makeRequests(List<Product> products, ProductSearchRequest request) {
        List<CalculateRequest> result = products.stream().map(p -> CalculateRequest.builder()
                .product(p)
                .daysCount(request.getDaysCount())
                .monthRefillSum(request.getMonthRefillSum())
                .monthWithdrawalSum(request.getMonthWithdrawalSum())
                .costCategories(request.getCostCategories())
                .build()
        ).collect(Collectors.toList());

        if(!isGreatThenZero(request.getMonthRefillSum()) && !isGreatThenZero(request.getMonthWithdrawalSum())) {
            Product savingAccount = products.stream()
                    .filter(p -> p.getType() == ProductTypeEnum.SAVING_ACCOUNT)
                    .findFirst()
                    .get();

            RefillOption autoRefillOption = refillOptionRepository.findByEventTypeAndRefillSumType(FIXED_DATE, FIXED_SUM);
            ((SavingAccount) savingAccount).setRefillOption(autoRefillOption);

            result.add(
                    CalculateRequest.builder()
                            .product(savingAccount)
                            .daysCount(request.getDaysCount())
                            .monthRefillSum(BigDecimal.valueOf(10000))
                            .costCategories(request.getCostCategories())
                            .build()
            );
        }

        return result;
    }
}
