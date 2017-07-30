package ru.tn.profitcalculator.comparator;

import ru.tn.profitcalculator.model.Product;
import ru.tn.profitcalculator.web.model.ProductGroup;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Optional;

import static java.math.BigDecimal.ZERO;
import static java.math.BigDecimal.valueOf;

public class ProductGroupComparator implements Comparator<ProductGroup> {

    private static final BigDecimal SUM_RATIO = valueOf(0.7);
    private static final BigDecimal RATE_RATIO = valueOf(0.5);
    private static final BigDecimal WEIGHT_RATIO = valueOf(0.3);

    @Override
    public int compare(ProductGroup one, ProductGroup two) {
        return calculateRating(two).compareTo(calculateRating(one));
    }

    public BigDecimal calculateRating(ProductGroup productGroup) {
        int weightSum = productGroup.getProducts().stream()
                .mapToInt(Product::getWeight)
                .reduce(0, (a, b) -> a + b);

        Optional<BigDecimal> optionProfitSum = Optional.ofNullable(productGroup.getOptionProfitSum());
        BigDecimal profitSum = productGroup.getProfitSum().add(optionProfitSum.orElse(ZERO));

        BigDecimal rate = productGroup.getMaxRate();
        BigDecimal weight = valueOf(weightSum);

        BigDecimal profitRank = profitSum.multiply(SUM_RATIO);
        BigDecimal rateRank = rate.multiply(RATE_RATIO);
        BigDecimal weightRank = weight.multiply(WEIGHT_RATIO);

        BigDecimal max = Collections.max(Arrays.asList(profitRank, rateRank, weightRank));
        int digitsCount = getDigitsCount(max);

        profitRank = alignValue(profitRank, digitsCount);
        rateRank = alignValue(rateRank, digitsCount);
        weightRank = alignValue(weightRank, digitsCount);

        return profitRank.add(rateRank.add(weightRank)).setScale(0, RoundingMode.HALF_UP);
    }

    private BigDecimal alignValue(BigDecimal value, int digitsCount) {
        int valueDigitsCount = getDigitsCount(value);
        int diff = digitsCount - valueDigitsCount;
        return value.multiply(valueOf(Math.pow(10, diff)));
    }

    private int getDigitsCount(BigDecimal value) {
        return String.valueOf(value.longValue()).length();
    }
}
