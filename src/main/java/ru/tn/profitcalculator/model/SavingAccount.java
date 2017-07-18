package ru.tn.profitcalculator.model;

import lombok.Data;

import java.util.Set;

@Data
public class SavingAccount extends Product {
    private Set<SavingAccountRate> rates;
}
