package ru.tn.profitcalculator.web.model;

import lombok.Builder;
import lombok.Data;
import ru.tn.profitcalculator.model.Product;

import java.math.BigDecimal;
import java.util.List;
import java.util.Set;

@Data
@Builder
public class ProductGroup {
    private List<Product> products;
    private List<Product> optionalProducts;
    private Set<String> notes;
    private BigDecimal resultSum;
    private BigDecimal profitSum;
    private BigDecimal optionProfitSum;
    private BigDecimal maxRate;
    private boolean offerByClientProduct;
}
