package ru.tn.profitcalculator.model;

import lombok.Data;

import java.math.BigDecimal;

@Data
@Entity
public class SavingAccountRate {
    private Long id;
    private Integer monthStart;
    private Integer monthEnd;
    private BigDecimal rate;
}
