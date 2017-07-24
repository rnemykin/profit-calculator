package ru.tn.profitcalculator.service.calculator.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.tn.profitcalculator.model.ProductRate;
import ru.tn.profitcalculator.model.enums.ProductTypeEnum;
import ru.tn.profitcalculator.repository.ProductRateRepository;
import ru.tn.profitcalculator.service.calculator.CalculateRequest;
import ru.tn.profitcalculator.service.calculator.CalculateResult;
import ru.tn.profitcalculator.service.calculator.Calculator;

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
    public CalculateResult calculate(CalculateRequest request) {
        ProductRate productRate = productRateRepository.findDepositRate(request.getProduct().getId(), request.getDaysCount()).get(0);
        LocalDate startDate = LocalDate.now();
        LocalDate endDate = startDate.plusDays(request.getDaysCount());

        BigDecimal totalSum = request.getInitSum();
        BigDecimal profitSum = BigDecimal.ZERO;
        LocalDate nextPeriodDate = startDate;

        boolean isRefill = isGreatThenZero(request.getMonthRefillSum());
        boolean isLastPeriod = false;
        while (!isLastPeriod) {
            nextPeriodDate = nextPeriodDate.plusMonths(1);

            isLastPeriod = !nextPeriodDate.isBefore(endDate);
            long periodDays = isLastPeriod ? DAYS.between(startDate, endDate) : DAYS.between(startDate, nextPeriodDate);
            BigDecimal monthProfit = calculatePeriodSum(totalSum, productRate.getRate(), valueOf(periodDays));
            profitSum = profitSum.add(monthProfit);
            totalSum = totalSum.add(monthProfit);
            if(isRefill && !isLastPeriod) {
                totalSum = totalSum.add(request.getMonthRefillSum());
            }

            startDate = nextPeriodDate;
        }

        BigDecimal maxRate = V_100.multiply(
                valueOf(Math.pow(
                            BigDecimal.ONE.
                                    add(productRate.getRate()
                                            .multiply(valueOf(request.getDaysCount())
                                            .divide(DAYS_IN_YEAR, SCALE, RoundingMode.HALF_UP)
                                            .divide(V_100, SCALE, RoundingMode.HALF_UP))
                                    ).doubleValue(),
                            DAYS_IN_YEAR.divide(valueOf(request.getDaysCount()), SCALE, RoundingMode.HALF_UP).doubleValue()
                        )
                ).subtract(BigDecimal.ONE)
        ).setScale(2, RoundingMode.HALF_UP);

        return CalculateResult.builder()
                .maxRate(maxRate)
                .totalSum(totalSum.setScale(0, RoundingMode.UP))
                .profitSum(profitSum.setScale(0, RoundingMode.UP))
                .daysCount(request.getDaysCount())
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
