package ru.tn.profitcalculator.web.api;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.tn.profitcalculator.service.CalculatorService;
import ru.tn.profitcalculator.web.model.CalculateParams;
import ru.tn.profitcalculator.web.model.ProductGroup;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/api/v1/products")
public class ProductController {
    private final CalculatorService calculatorService;

    @Autowired
    public ProductController(CalculatorService calculatorService) {
        this.calculatorService = calculatorService;
    }


    @PostMapping
    public List<ProductGroup> calculateProducts(@Valid @RequestBody CalculateParams request) {
        return calculatorService.calculateOffers(request);
    }

}
