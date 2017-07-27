package ru.tn.profitcalculator.comparator;

import ru.tn.profitcalculator.model.Product;
import ru.tn.profitcalculator.web.model.ProductGroup;

import java.util.Comparator;

public class ProductGroupComparator implements Comparator<ProductGroup> {
    @Override
    public int compare(ProductGroup one, ProductGroup two) {
        Integer weight1 = one.getProducts().stream()
                .mapToInt(Product::getWeight)
                .sum();

        Integer weight2 = two.getProducts().stream()
                .mapToInt(Product::getWeight)
                .sum();

        return weight1.compareTo(weight2);
    }
}
