package ru.tn.profitcalculator.repository.specification;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ProductFilter {
    private boolean refill;
    private boolean withdrawal;
    private Integer daysCount;
}