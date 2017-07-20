package ru.tn.profitcalculator.web.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import ru.tn.profitcalculator.model.Product;

import java.util.Set;

@Data
@AllArgsConstructor
public class ProductResponse {
    private Product product;
    private Set<String> notes;
}
