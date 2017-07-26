package ru.tn.profitcalculator.service.calculator.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.tn.profitcalculator.model.ProductRate;
import ru.tn.profitcalculator.model.enums.ProductTypeEnum;
import ru.tn.profitcalculator.repository.ProductRateRepository;
import ru.tn.profitcalculator.service.calculator.Calculator;
import ru.tn.profitcalculator.service.calculator.ProductCalculateRequest;
import ru.tn.profitcalculator.service.calculator.ProductCalculateResult;
import ru.tn.profitcalculator.web.model.CalculateParams;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;

import static java.math.BigDecimal.valueOf;
import static java.time.temporal.ChronoUnit.DAYS;
import static ru.tn.profitcalculator.util.MathUtils.isGreatThenZero;

@Service
public class DepositCalculator implements Calculator {
    private static final BigDecimal DAYS_IN_YEAR = valueOf(365);
    private static final BigDecimal V_100 = valueOf(100);
    private static final int SCALE = 10;


    private final ProductRateRepository productRateRepository;

    @Autowired
    public DepositCalculator(ProductRateRepository productRateRepository) {
        this.productRateRepository = productRateRepository;
    }

    @Override
    public ProductCalculateResult calculate(ProductCalculateRequest request) {
        CalculateParams params = request.getParams();
        ProductRate productRate = productRateRepository.findProductRate(request.getProduct().getId(), params.getDaysCount()).get(0);
        LocalDate startDate = LocalDate.now();
        LocalDate endDate = startDate.plusDays(params.getDaysCount());

        BigDecimal totalSum = params.getInitSum();
        BigDecimal profitSum = BigDecimal.ZERO;
        LocalDate nextPeriodDate = startDate;

        boolean isRefill = isGreatThenZero(params.getMonthRefillSum());
        boolean isLastPeriod = false;
        while (!isLastPeriod) {
            nextPeriodDate = nextPeriodDate.plusMonths(1);

            isLastPeriod = !nextPeriodDate.isBefore(endDate);
            long periodDays = isLastPeriod ? DAYS.between(startDate, endDate) : DAYS.between(startDate, nextPeriodDate);
            BigDecimal monthProfit = calculatePeriodSum(totalSum, productRate.getRate(), valueOf(periodDays));
            profitSum = profitSum.add(monthProfit);
            totalSum = totalSum.add(monthProfit);
            if(isRefill && !isLastPeriod) {
                totalSum = totalSum.add(params.getMonthRefillSum());
            }

            startDate = nextPeriodDate;
        }

        BigDecimal maxRate = V_100.multiply(
                valueOf(Math.pow(
                            BigDecimal.ONE.
                                    add(productRate.getRate()
                                            .multiply(valueOf(params.getDaysCount())
                                            .divide(DAYS_IN_YEAR, SCALE, RoundingMode.HALF_UP)
                                            .divide(V_100, SCALE, RoundingMode.HALF_UP))
                                    ).doubleValue(),
                            DAYS_IN_YEAR.divide(valueOf(params.getDaysCount()), SCALE, RoundingMode.HALF_UP).doubleValue()
                        )
                ).subtract(BigDecimal.ONE)
        ).setScale(2, RoundingMode.HALF_UP);

        return ProductCalculateResult.builder()
                .maxRate(maxRate)
                .totalSum(totalSum.setScale(0, RoundingMode.HALF_UP))
                .profitSum(profitSum.setScale(0, RoundingMode.HALF_UP))
                .daysCount(params.getDaysCount())
                .product(request.getProduct())
                .recommendation(request.isRecommendation())
                .build();
    }

    private BigDecimal calculatePeriodSum(BigDecimal totalSum, BigDecimal rate, BigDecimal periodDays) {
        return totalSum.multiply(
                rate.multiply(periodDays.divide(DAYS_IN_YEAR, SCALE, RoundingMode.HALF_UP))
        ).divide(V_100, SCALE, RoundingMode.HALF_UP);
    }

    @Override
    public ProductTypeEnum type() {
        return ProductTypeEnum.DEPOSIT;
    }
}
