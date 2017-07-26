package ru.tn.profitcalculator.web.model;

import lombok.Builder;
import lombok.Data;
import ru.tn.profitcalculator.model.Product;

import java.math.BigDecimal;
import java.util.List;
import java.util.Set;

@Data
@Builder
public class ProductGroup implements Comparable<ProductGroup> {
    private List<Product> products;
    private List<Product> optionalProducts;
    private Set<String> notes;
    private BigDecimal resultSum;
    private BigDecimal profitSum;
    private BigDecimal maxRate;

    @Override
    public int compareTo(ProductGroup other) {  // todo
        Integer sum1 = getProducts().stream()
                .mapToInt(Product::getWeight)
                .sum();

        Integer sum2 = other.getProducts().stream()
                .mapToInt(Product::getWeight)
                .sum();

        return sum1.compareTo(sum2);
    }
}
