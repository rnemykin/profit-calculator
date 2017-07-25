package ru.tn.profitcalculator.service.calculator.impl;

import lombok.AllArgsConstructor;
import org.apache.commons.lang3.Range;
import org.springframework.stereotype.Service;
import ru.tn.profitcalculator.model.Card;
import ru.tn.profitcalculator.model.CardOption;
import ru.tn.profitcalculator.model.SavingAccount;
import ru.tn.profitcalculator.model.enums.BonusOptionEnum;
import ru.tn.profitcalculator.model.enums.PosCategoryEnum;
import ru.tn.profitcalculator.model.enums.ProductTypeEnum;
import ru.tn.profitcalculator.service.calculator.Calculator;
import ru.tn.profitcalculator.service.calculator.ProductCalculateRequest;
import ru.tn.profitcalculator.service.calculator.ProductCalculateResult;
import ru.tn.profitcalculator.web.model.CalculateParams;

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

    @Override
    public ProductCalculateResult calculate(ProductCalculateRequest request) {
        SavingAccount savingAccount = (SavingAccount) request.getProduct();
        Map<Integer, BigDecimal> periodRates = initSavingAccountRates(savingAccount);

        CalculateParams params = request.getParams();
        Integer daysCount = params.getDaysCount();
        LocalDate startDate = LocalDate.now();
        LocalDate endDate = startDate.plusDays(daysCount);
        BigDecimal totalSum = params.getInitSum();
        BigDecimal totalProfit = ZERO;

        BigDecimal refillSum = ZERO;
        Map<LocalDate, BigDecimal> layers = new TreeMap<>();
        layers.put(startDate, totalSum);

        if (isGreatThenZero(params.getMonthRefillSum())) {
            long monthsCount = MONTHS.between(startDate, endDate);
            for (int i = 1; i <= monthsCount; i++) {
                layers.put(startDate.plusMonths(i), params.getMonthRefillSum());
            }

            refillSum = params.getMonthRefillSum().multiply(valueOf(monthsCount));
        }

        System.out.println("********************* start calculating");
        List<List<BigDecimal>> accountState = new ArrayList<>();

        BigDecimal savingOptionRate = getSavingOptionRate(savingAccount, params);
        for (Map.Entry<LocalDate, BigDecimal> layer : layers.entrySet()) {

            List<BigDecimal> layerAccountState = new ArrayList<>();

            LocalDate layerStartDate = layer.getKey();
            LocalDate layerNextPeriodDate = layerStartDate;
            BigDecimal layerProfitSum = layer.getValue();

            for (int i = 1; layerNextPeriodDate.isBefore(endDate); i++) {
                layerAccountState.add(layerProfitSum);

                layerNextPeriodDate = layerNextPeriodDate.plusMonths(1);

                boolean isLastPeriod = !layerNextPeriodDate.isBefore(endDate);
                long periodDays = isLastPeriod ? DAYS.between(layerStartDate, endDate) : DAYS.between(layerStartDate, layerNextPeriodDate);
                BigDecimal rate4Month = getRate4Month(periodRates, i).add(savingOptionRate);
                BigDecimal monthProfit = calculatePeriodSum(layerProfitSum, rate4Month, valueOf(periodDays));

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

        normalizeAccountState(accountState);

        return ProductCalculateResult.builder()
                .totalSum(totalSum.add(refillSum))
                .profitSum(totalProfit)
                .maxRate(new EffectiveRateCalculator(periodRates, accountState).calculate())
                .daysCount(daysCount)
                .build();
    }

    private BigDecimal getSavingOptionRate(SavingAccount savingAccount, CalculateParams params) {
        Map<PosCategoryEnum, BigDecimal> categories2Costs = params.getCategories2Costs();
        BigDecimal savingOptionRate = BigDecimal.ZERO;
        if (savingAccount.getLinkedProduct() != null && categories2Costs != null && !categories2Costs.isEmpty()) {
            Card card = (Card) savingAccount.getLinkedProduct();
            savingOptionRate = new SavingOptionRateCalculator(card, categories2Costs).calculate();
        }
        return savingOptionRate;
    }

    private Map<Integer, BigDecimal> initSavingAccountRates(SavingAccount savingAccount) {
        Map<Integer, BigDecimal> rates = new HashMap<>();
        savingAccount.getRates().forEach(r -> rates.put(r.getFromPeriod(), r.getRate()));
        return rates;
    }

    private BigDecimal getRate4Month(Map<Integer, BigDecimal> periodRates, int month) {
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

    private void normalizeAccountState(List<List<BigDecimal>> accountState) {
        int maxSize = accountState.get(0).size();
        for (List<BigDecimal> state : accountState) {
            while (state.size() != maxSize) {
                state.add(0, BigDecimal.ZERO);
            }
        }
    }

    @Override
    public ProductTypeEnum type() {
        return ProductTypeEnum.SAVING_ACCOUNT;
    }

    @AllArgsConstructor
    private class EffectiveRateCalculator {
        private Map<Integer, BigDecimal> rates;
        private List<List<BigDecimal>> accountState;

        private BigDecimal calculate() {
            BigDecimal b12 = getMinSumForPeriod(12);
            BigDecimal b6 = getMinSumForPeriod(6);
            BigDecimal b3 = getMinSumForPeriod(3);
            BigDecimal b1 = getMinSumForPeriod(1);

            BigDecimal rate12 = getRate4Month(rates, 12);
            BigDecimal rate6 = getRate4Month(rates, 6);
            BigDecimal rate3 = getRate4Month(rates, 3);
            BigDecimal rate1 = getRate4Month(rates, 1);

            return (b12.multiply(rate12)
                    .add(b6.subtract(b12).multiply(rate6))
                    .add(b3.subtract(b6).multiply(rate3))
                    .add(b1.subtract(b3).multiply(rate1))
            ).divide(b1, 1, RoundingMode.HALF_UP);
        }

        private BigDecimal getMinSumForPeriod(int period) {
            List<BigDecimal> initLayer = accountState.get(0);
            if (initLayer.size() < period) {
                return BigDecimal.ZERO;
            }

            int size = initLayer.size();
            List<BigDecimal> monthsSum = new ArrayList<>();
            for (int i = size - period; i < size; i++) {
                BigDecimal monthSum = ZERO;
                for (List<BigDecimal> state : accountState) {
                    monthSum = monthSum.add(state.get(i));
                }
                monthsSum.add(monthSum);
            }

            return monthsSum.stream()
                    .min(BigDecimal::compareTo)
                    .orElse(BigDecimal.ZERO);
        }
    }

    @AllArgsConstructor
    private static class SavingOptionRateCalculator {

        private static final Range<BigDecimal> RANGE_1 = Range.between(BigDecimal.valueOf(5000), BigDecimal.valueOf(14999.99));
        private static final Range<BigDecimal> RANGE_2 = Range.between(BigDecimal.valueOf(15000), BigDecimal.valueOf(74999.99));
        private static final Range<BigDecimal> RANGE_3 = Range.between(BigDecimal.valueOf(75000), BigDecimal.valueOf(Double.MAX_VALUE));

        private Card card;
        private Map<PosCategoryEnum, BigDecimal> categories2Costs;

        private BigDecimal calculate() {
            BigDecimal savingOptionRate = BigDecimal.ZERO;
            CardOption cardOption = card.getCardOption();

            if (cardOption != null && cardOption.getOption() == BonusOptionEnum.SAVING) {
                BigDecimal totalTransactionSum = BigDecimal.ZERO;

                for (BigDecimal categoryTransactionsSum : categories2Costs.values()) {
                    totalTransactionSum = totalTransactionSum.add(categoryTransactionsSum);
                }
                if (RANGE_1.contains(totalTransactionSum)) {
                    savingOptionRate = cardOption.getRate1();
                } else if (RANGE_2.contains(totalTransactionSum)) {
                    savingOptionRate = cardOption.getRate2();
                } else if (RANGE_3.contains(totalTransactionSum)) {
                    savingOptionRate = cardOption.getRate3();
                }
            }
            return savingOptionRate;
        }
    }
}
