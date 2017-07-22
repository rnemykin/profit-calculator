package ru.tn.profitcalculator.service.calculator.impl;

import org.springframework.stereotype.Service;
import ru.tn.profitcalculator.model.Deposit;
import ru.tn.profitcalculator.model.enums.ProductTypeEnum;
import ru.tn.profitcalculator.service.calculator.CalculateRequest;
import ru.tn.profitcalculator.service.calculator.CalculateResult;
import ru.tn.profitcalculator.service.calculator.CalculatorService;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;

import static java.math.BigDecimal.valueOf;
import static java.time.temporal.ChronoUnit.DAYS;

@Service
public class DepositCalculatorService implements CalculatorService {
    private static final BigDecimal DAYS_IN_YEAR = valueOf(365);
    private static final BigDecimal V_100 = valueOf(100);

    @Override
    public CalculateResult calculate(CalculateRequest request) {
        Deposit deposit = (Deposit) request.getProduct();

        LocalDate startDate = LocalDate.now();
        LocalDate endDate = startDate.plusDays(request.getDaysCount());

        BigDecimal totalSum = request.getInitSum();
        BigDecimal profitSum = BigDecimal.ZERO;
        LocalDate nextPeriodDate = startDate;

        while (true) {
            nextPeriodDate = nextPeriodDate.plusMonths(1);
            if(nextPeriodDate.compareTo(endDate) <= 0) {
                long periodDays = DAYS.between(startDate, nextPeriodDate);
                BigDecimal monthProfit = calculatePeriodSum(totalSum, deposit.getNominalRate(), valueOf(periodDays));
                profitSum = profitSum.add(monthProfit);
                totalSum = totalSum.add(monthProfit);
                if(request.getMonthRefillSum() != null) {
                    totalSum = totalSum.add(request.getMonthRefillSum());
                }
            } else {
                long periodDays = DAYS.between(startDate, endDate);
                BigDecimal monthProfit = calculatePeriodSum(totalSum, deposit.getNominalRate(), valueOf(periodDays));
                profitSum = profitSum.add(monthProfit);
                totalSum = totalSum.add(monthProfit);
                break;
            }

            startDate = nextPeriodDate;
        }

        return CalculateResult.builder()
                .totalSum(totalSum)
                .profitSum(profitSum)
                .daysCount(request.getDaysCount())
                .build();
    }

    private BigDecimal calculatePeriodSum(BigDecimal totalSum, BigDecimal rate, BigDecimal periodDays) {
        return totalSum.multiply(
                rate.multiply(periodDays.divide(DAYS_IN_YEAR, 10, RoundingMode.HALF_UP))
        ).divide(V_100, 0, RoundingMode.HALF_UP);
    }

    @Override
    public ProductTypeEnum type() {
        return ProductTypeEnum.DEPOSIT;
    }
}
