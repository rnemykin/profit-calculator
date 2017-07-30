package ru.tn.profitcalculator.util;

import java.math.BigDecimal;
import java.util.Comparator;
import java.util.Map;

public class SavingAccountUtils {

    public static BigDecimal getRate4Month(Map<Integer, BigDecimal> periodRates, int month) {
        return periodRates.entrySet().stream()
                .filter(e -> e.getKey().compareTo(month) <= 0)
                .max(Comparator.comparing(Map.Entry::getKey))
                .orElseThrow(() -> new RuntimeException("rate not found fot month " + month))
                .getValue();
    }
}
