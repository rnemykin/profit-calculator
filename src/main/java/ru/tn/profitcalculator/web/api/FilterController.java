package ru.tn.profitcalculator.web.api;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.tn.profitcalculator.service.FilterCalculationService;
import ru.tn.profitcalculator.web.model.CalculateParams;

@RestController
@RequestMapping("/api/v1/filters")
@Slf4j
public class FilterController {
    private final FilterCalculationService filterService;

    @Autowired
    public FilterController(FilterCalculationService filterService) {
        this.filterService = filterService;
    }

    @PostMapping
    public CalculateParams calculateProducts(@RequestBody CalculateParams request) {
        return filterService.calculateFilterParameter(request);
    }
}
