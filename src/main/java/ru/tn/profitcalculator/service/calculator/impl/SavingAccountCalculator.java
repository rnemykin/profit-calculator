package ru.tn.profitcalculator.service.calculator.impl;

import org.springframework.stereotype.Service;
import ru.tn.profitcalculator.model.SavingAccount;
import ru.tn.profitcalculator.model.enums.ProductTypeEnum;
import ru.tn.profitcalculator.service.calculator.CalculateRequest;
import ru.tn.profitcalculator.service.calculator.CalculateResult;
import ru.tn.profitcalculator.service.calculator.Calculator;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

import static java.math.BigDecimal.valueOf;
import static java.time.temporal.ChronoUnit.DAYS;

@Service
public class SavingAccountCalculator implements Calculator {
    private static final BigDecimal DAYS_IN_YEAR = valueOf(365);
    private static final BigDecimal V_100 = valueOf(100);
    private static final Map<Integer, BigDecimal> PERIOD_RATES = new HashMap<Integer, BigDecimal>(){{
        put(1, BigDecimal.valueOf(4));
        put(3, BigDecimal.valueOf(5));
        put(6, BigDecimal.valueOf(6));
        put(12, BigDecimal.valueOf(8.5));
    }};


    @Override
    public CalculateResult calculate(CalculateRequest request) {
        SavingAccount savingAccount = (SavingAccount) request.getProduct();

        Integer daysCount = request.getDaysCount();
        LocalDate startDate = LocalDate.now();
        LocalDate nextPeriodDate = startDate;
        LocalDate endDate = startDate.plusDays(daysCount);

        BigDecimal totalProfit = BigDecimal.ZERO;
        BigDecimal totalSum = request.getInitSum();
        for(int i = 1; nextPeriodDate.isBefore(endDate); i++, startDate = nextPeriodDate) {
            nextPeriodDate = nextPeriodDate.plusMonths(i);

            boolean isLastPeriod = nextPeriodDate.compareTo(endDate) >= 0;
            long periodDays = isLastPeriod ? DAYS.between(startDate, endDate) : DAYS.between(startDate, nextPeriodDate);
            BigDecimal monthProfit = calculatePeriodSum(totalSum, getRate4Month(i), valueOf(periodDays));

            totalProfit = totalSum.add(monthProfit);
            totalSum = totalSum.add(monthProfit);
        }

        return CalculateResult.builder()
                .totalSum(totalSum)
                .profitSum(totalProfit)
                .maxRate(null)
                .daysCount(daysCount)
                .build();
    }


    private BigDecimal getRate4Month(int month) {
        return PERIOD_RATES.entrySet().stream()
                .filter(e -> e.getKey().compareTo(month) <= 0)
                .max((x, y) -> x.getKey().compareTo(y.getKey()))
                .orElseThrow(() -> new RuntimeException("rate not found fot month " + month))
                .getValue();
    }

    private BigDecimal calculatePeriodSum(BigDecimal totalSum, BigDecimal rate, BigDecimal periodDays) {
        return totalSum.multiply(
                rate.multiply(periodDays.divide(DAYS_IN_YEAR, 10, RoundingMode.HALF_UP))
        ).divide(V_100, 0, RoundingMode.HALF_UP);
    }

    @Override
    public ProductTypeEnum type() {
        return ProductTypeEnum.SAVING_ACCOUNT;
    }
}
