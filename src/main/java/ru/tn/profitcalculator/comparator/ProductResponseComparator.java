package ru.tn.profitcalculator.comparator;

import ru.tn.profitcalculator.model.Product;
import ru.tn.profitcalculator.web.model.ProductGroup;

import java.util.Comparator;

public class ProductResponseComparator implements Comparator<ProductGroup> {
    @Override
    public int compare(ProductGroup o1, ProductGroup o2) {
        Integer sum1 = o1.getProducts().stream()
                .mapToInt(Product::getWeight)
                .sum();

        Integer sum2 = o2.getProducts().stream()
                .mapToInt(Product::getWeight)
                .sum();

        return sum1.compareTo(sum2);
    }
}
