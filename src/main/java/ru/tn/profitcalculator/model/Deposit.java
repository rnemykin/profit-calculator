package ru.tn.profitcalculator.model;

import lombok.Data;
import ru.tn.profitcalculator.model.enums.DepositTypeEnum;

import java.math.BigDecimal;

@Data
public class Deposit extends Product {
    private DepositTypeEnum depositType;
    private BigDecimal nominalRate;
    private BigDecimal effectiveRate;
    private boolean refill;
    private boolean withdrawal;
}
