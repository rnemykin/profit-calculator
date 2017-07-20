package ru.tn.profitcalculator.web.comparator;

import ru.tn.profitcalculator.web.model.ProductGroup;

import java.util.Comparator;

public class ProductResponseComparator implements Comparator<ProductGroup> {
    @Override
    public int compare(ProductGroup o1, ProductGroup o2) {
        Integer sum1 = o1.getProducts().stream()
                .mapToInt(p -> p.getProduct().getWeight())
                .sum();

        Integer sum2 = o2.getProducts().stream()
                .mapToInt(p -> p.getProduct().getWeight())
                .sum();

        return sum1.compareTo(sum2);
    }
}
