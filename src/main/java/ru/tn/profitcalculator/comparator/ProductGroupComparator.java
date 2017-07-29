package ru.tn.profitcalculator.comparator;

import ru.tn.profitcalculator.model.Product;
import ru.tn.profitcalculator.web.model.ProductGroup;

import java.math.BigDecimal;
import java.math.RoundingMode;
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
        double profitSumPrecision = Math.pow(10, profitSum.precision());

        BigDecimal rate = productGroup.getMaxRate().multiply(valueOf(profitSumPrecision));
        BigDecimal weight = valueOf(weightSum * profitSumPrecision);

        BigDecimal profitRatio = profitSum.multiply(SUM_RATIO);
        BigDecimal rateRatio = rate.multiply(RATE_RATIO);
        BigDecimal weightRatio = weight.multiply(WEIGHT_RATIO);

        return profitRatio.add(rateRatio.add(weightRatio)).setScale(0, RoundingMode.HALF_UP);
    }
}
