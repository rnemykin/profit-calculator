package ru.tn.profitcalculator.web.api;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.tn.profitcalculator.web.comparator.ProductResponseComparator;
import ru.tn.profitcalculator.web.model.ProductResponse;

import java.util.Set;
import java.util.TreeSet;

@RestController
@RequestMapping("/products")
public class ProductController {

    @GetMapping
    public Set<ProductResponse> findProducts() {
        return new TreeSet<>(new ProductResponseComparator());
    }

}
