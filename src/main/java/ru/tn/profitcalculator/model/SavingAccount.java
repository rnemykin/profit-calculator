package ru.tn.profitcalculator.model;

import lombok.Data;
import ru.tn.profitcalculator.model.enums.ProductTypeEnum;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import javax.persistence.Transient;
import java.util.List;

@Data
@Entity
public class SavingAccount extends Product {

    public SavingAccount() {
        setType(ProductTypeEnum.SAVING_ACCOUNT);
    }

    @OneToMany(fetch = FetchType.EAGER)
    @JoinColumn(name = "productId")
    private List<ProductRate> rates;

    @Transient
    private RefillOption refillOption;
}
