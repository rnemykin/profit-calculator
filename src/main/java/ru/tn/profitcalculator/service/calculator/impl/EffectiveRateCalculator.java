package ru.tn.profitcalculator.service.calculator.impl;

import lombok.AllArgsConstructor;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static ru.tn.profitcalculator.util.SavingAccountUtils.getRate4Month;

@AllArgsConstructor
public class EffectiveRateCalculator {
    private Map<Integer, BigDecimal> rates;
    private List<List<BigDecimal>> accountState;

    public BigDecimal calculate() {
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
        if (accountState != null && !accountState.isEmpty()) {

            List<BigDecimal> initLayer = accountState.get(0);
            if (initLayer.size() < period) {
                return BigDecimal.ZERO;
            }

            int size = initLayer.size();
            List<BigDecimal> monthsSum = new ArrayList<>();
            for (int i = size - period; i < size; i++) {
                BigDecimal monthSum = BigDecimal.ZERO;
                for (List<BigDecimal> state : accountState) {
                    monthSum = monthSum.add(state.get(i));
                }
                monthsSum.add(monthSum);
            }
            return monthsSum.stream()
                    .min(BigDecimal::compareTo)
                    .orElse(BigDecimal.ZERO);
        }
        return BigDecimal.ZERO;
    }
}