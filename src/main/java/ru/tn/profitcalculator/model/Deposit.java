package ru.tn.profitcalculator.model;

import lombok.Data;
import ru.tn.profitcalculator.model.enums.DepositTypeEnum;
import ru.tn.profitcalculator.model.enums.ProductTypeEnum;

import java.math.BigDecimal;

@Data
public class Deposit extends Product {
    public Deposit() {
        setType(ProductTypeEnum.DEPOSIT);
    }

    private DepositTypeEnum depositType;
    private BigDecimal nominalRate;
    private BigDecimal effectiveRate;
    private boolean refill;
    private boolean withdrawal;
    private boolean earlyCancellation;
    private Integer minPeriod;
    private Integer maxPeriod;
    private BigDecimal privilegeSum;
    private BigDecimal privilegeRate;
    private BigDecimal privateBankingRate;
    private BigDecimal privateBankingSum;
}
