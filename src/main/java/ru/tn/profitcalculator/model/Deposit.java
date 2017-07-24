package ru.tn.profitcalculator.model;

import lombok.Data;
import ru.tn.profitcalculator.model.enums.DepositTypeEnum;
import ru.tn.profitcalculator.model.enums.ProductTypeEnum;

import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import java.math.BigDecimal;
import java.util.List;

@Data
@Entity
public class Deposit extends Product {
    public Deposit() {
        setType(ProductTypeEnum.DEPOSIT);
    }

    @Enumerated(EnumType.STRING)
    private DepositTypeEnum depositType;
    private BigDecimal nominalRate;
    private boolean refill;
    private boolean withdrawal;
    private boolean earlyCancellation;
    private Integer minPeriod;
    private Integer maxPeriod;
    private Integer lastRefillDayRemains;
    private BigDecimal privilegeSum;
    private BigDecimal privilegeRate;
    private BigDecimal privateBankingRate;
    private BigDecimal privateBankingSum;

    @OneToMany(fetch = FetchType.EAGER)
    @JoinColumn(name = "productId")
    private List<ProductRate> rates;
}
