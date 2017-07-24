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
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import static java.math.BigDecimal.ZERO;
import static java.math.BigDecimal.valueOf;
import static java.time.temporal.ChronoUnit.DAYS;
import static java.time.temporal.ChronoUnit.MONTHS;
import static ru.tn.profitcalculator.util.MathUtils.isGreatThenZero;

@Service
public class SavingAccountCalculator implements Calculator {
    private static final BigDecimal DAYS_IN_YEAR = valueOf(365);
    private static final BigDecimal V_100 = valueOf(100);

    private ProductRateRepository productRateRepository;

    @Autowired
    public SavingAccountCalculator(ProductRateRepository productRateRepository) {
        this.productRateRepository = productRateRepository;
    }


    @Override
    public CalculateResult calculate(CalculateRequest request) {
        Map<Integer, BigDecimal> periodRates = initSavingAccountRates(request.getProduct().getId());

        Integer daysCount = request.getDaysCount();
        LocalDate startDate = LocalDate.now();
        LocalDate endDate = startDate.plusDays(daysCount);
        BigDecimal totalSum = request.getInitSum();
        BigDecimal totalProfit = ZERO;

        BigDecimal refillSum = ZERO;
        Map<LocalDate, BigDecimal> layers = new TreeMap<>();
        layers.put(startDate, totalSum);
        if(isGreatThenZero(request.getMonthRefillSum())) {
            long monthsCount = MONTHS.between(startDate, endDate);
            for (int i = 1; i <= monthsCount ; i++) {
                layers.put(startDate.plusMonths(i), request.getMonthRefillSum());
            }

            refillSum = request.getMonthRefillSum().multiply(valueOf(monthsCount));
        }

        System.out.println("********************* start calculating");
        List<List<BigDecimal>> accountState = new ArrayList<>();
        for (Map.Entry<LocalDate, BigDecimal> layer : layers.entrySet()) {

            List<BigDecimal> layerAccountState = new ArrayList<>();

            LocalDate layerStartDate = layer.getKey();
            LocalDate layerNextPeriodDate = layerStartDate;
            BigDecimal layerProfitSum = layer.getValue();
            for(int i = 1; layerNextPeriodDate.isBefore(endDate); i++) {
                layerAccountState.add(layerProfitSum);

                layerNextPeriodDate = layerNextPeriodDate.plusMonths(1);

                boolean isLastPeriod = !layerNextPeriodDate.isBefore(endDate);
                long periodDays = isLastPeriod ? DAYS.between(layerStartDate, endDate) : DAYS.between(layerStartDate, layerNextPeriodDate);
                BigDecimal monthProfit = calculatePeriodSum(layerProfitSum, getRate4Month(periodRates, i), valueOf(periodDays));

                layerProfitSum = layerProfitSum.add(monthProfit);
                layerStartDate = layerNextPeriodDate;

                totalSum = totalSum.add(monthProfit);
                totalProfit = totalProfit.add(monthProfit);

                System.out.println("monthProfit " + monthProfit);
                System.out.println("layerProfitSum = " + layerProfitSum);
            }

            accountState.add(layerAccountState);
            System.out.println("\n\n next layer \n\n");
        }

        return CalculateResult.builder()
                .totalSum(totalSum.add(refillSum))
                .profitSum(totalProfit)
                .maxRate(null)  //todo
                .daysCount(daysCount)
                .build();
    }

    private Map<Integer, BigDecimal> initSavingAccountRates(Long savingAccountId) {
        Map<Integer, BigDecimal> rates = new HashMap<>();
        List<ProductRate> productRates = productRateRepository.findAllByProductId(savingAccountId);
        productRates.forEach(r -> rates.put(r.getFromPeriod(), r.getRate()));
        return rates;
    }

    public BigDecimal getRate4Month(Map<Integer, BigDecimal> periodRates, int month) {
        return periodRates.entrySet().stream()
                .filter(e -> e.getKey().compareTo(month) <= 0)
                .max(Comparator.comparing(Map.Entry::getKey))
                .orElseThrow(() -> new RuntimeException("rate not found fot month " + month))
                .getValue();
    }

    private BigDecimal calculatePeriodSum(BigDecimal totalSum, BigDecimal rate, BigDecimal periodDays) {
        System.out.println("totalSum = " + totalSum + " rate = " + rate + " period = " + periodDays);
        return totalSum.multiply(
                rate.multiply(periodDays.divide(DAYS_IN_YEAR, 10, RoundingMode.HALF_UP))
        ).divide(V_100, 0, RoundingMode.HALF_UP);
    }

    @Override
    public ProductTypeEnum type() {
        return ProductTypeEnum.SAVING_ACCOUNT;
    }
}
