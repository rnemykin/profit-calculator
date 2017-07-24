package ru.tn.profitcalculator.model;

import lombok.Data;
import ru.tn.profitcalculator.model.enums.ProductTypeEnum;

import javax.persistence.Entity;
import javax.persistence.Transient;

@Data
@Entity
public class SavingAccount extends Product {

    public SavingAccount() {
        setType(ProductTypeEnum.SAVING_ACCOUNT);
    }

    @Transient
    private RefillOption refillOption;
}
