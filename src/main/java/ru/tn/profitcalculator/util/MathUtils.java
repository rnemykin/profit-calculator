package ru.tn.profitcalculator.util;

import java.math.BigDecimal;

public class MathUtils {
    public static boolean isGreatThenZero(BigDecimal source) {
        return source != null && source.compareTo(BigDecimal.ZERO) > 0;
    }
}
