package ru.tn.profitcalculator.model;

import lombok.Data;
import ru.tn.profitcalculator.model.enums.ProductTypeEnum;

import java.math.BigDecimal;

@Data
public class SavingAccount extends Product {

    public SavingAccount() {
        setType(ProductTypeEnum.SAVING_ACCOUNT);
    }

    private BigDecimal rate1;
    private BigDecimal rate2;
    private BigDecimal rate3;
    private BigDecimal rate4;
}
