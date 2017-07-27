package ru.tn.profitcalculator.comparator;

import ru.tn.profitcalculator.model.Product;
import ru.tn.profitcalculator.web.model.ProductGroup;

import java.math.BigDecimal;
import java.util.Comparator;

import static java.math.BigDecimal.valueOf;

public class ProductGroupComparator implements Comparator<ProductGroup> {
    private static final BigDecimal SUM_RATIO = valueOf(0.7);
    private static final BigDecimal WEIGHT_RATIO = valueOf(0.3);

    @Override
    public int compare(ProductGroup one, ProductGroup two) {
        return calculateRating(two).compareTo(calculateRating(one));
    }

    private BigDecimal calculateRating(ProductGroup productGroup) {
        Double weightSum = productGroup.getProducts().stream()
                .mapToInt(Product::getWeight)
                .average()
                .orElse(1.0);

        BigDecimal profitSum = productGroup.getProfitSum();
        BigDecimal weight = valueOf(weightSum * Math.pow(10, profitSum.precision()));
        return profitSum.multiply(SUM_RATIO).add(weight.multiply(WEIGHT_RATIO));
    }
}
