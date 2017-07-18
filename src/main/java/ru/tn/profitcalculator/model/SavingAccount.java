package ru.tn.profitcalculator.model;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class SavingAccount extends Product {
    private BigDecimal rate1;
    private BigDecimal rate2;
    private BigDecimal rate3;
    private BigDecimal rate4;
}
